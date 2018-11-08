package serverConection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import javax.json.JsonWriter;

import application.news.Article;
import application.utils.JsonArticle;
import application.utils.exceptions.ErrorMalFormedNews;

import serverConection.exceptions.AuthenticationError;
import serverConection.exceptions.ServerCommunicationError;

public class ConnectionManager {

	private String idUser;
	private String authType;
	private String apikey;
	private boolean loggedOK = false;

	private boolean isAdministrator = false;

	private String serviceUrl ;

	private boolean requireSelfSigned = false;

	public static final String ATTR_LOGIN_USER = "username";
	public static final String ATTR_LOGIN_PASS = "password";
	public static final String ATTR_SERVICE_URL = "service_url";
	public static final String ATTR_REQUIRE_SELF_CERT = "require_self_signed_cert";
	public static final String ATTR_PROXY_HOST = "";
	public static final String ATTR_PROXY_PORT = "";
	public static final String ATTR_PROXY_USER = "";
	public static final String ATTR_PROXY_PASS = "";
	public static final String ATTR_APACHE_AUTH_USER = "";
	public static final String ATTR_APACHE_AUTH_PASS = "";

	/**
	 * 
	 * @param ini Initializes entity manager urls and users
	 * @throws AuthenticationError
	 */
	public ConnectionManager(Properties ini) throws AuthenticationError{
		if (!ini.containsKey(ATTR_SERVICE_URL)){
			throw new IllegalArgumentException("Required attribute '"+ ATTR_SERVICE_URL+"' not found!");
		}

		// disable auth from self signed certificates
		requireSelfSigned =  (ini.containsKey(ATTR_REQUIRE_SELF_CERT) && ((String)ini.get(ATTR_REQUIRE_SELF_CERT)).equalsIgnoreCase("TRUE"));

		// add proxy http/https to the system
		if (ini.contains(ATTR_PROXY_HOST) && ini.contains(ATTR_PROXY_PORT)){
			String proxyHost = (String)ini.get(ATTR_PROXY_HOST);
			String proxyPort = (String)ini.get(ATTR_PROXY_PORT);

			System.setProperty("http.proxyHost", proxyHost);
			System.setProperty("http.proxyPort", proxyPort);
			System.setProperty("https.proxyHost", proxyHost);
			System.setProperty("https.proxyPort", proxyPort);
		}

		if (ini.contains(ATTR_PROXY_USER) && ini.contains(ATTR_PROXY_PASS))	{
			String proxyUser = (String)ini.get(ATTR_PROXY_USER);
			String proxyPassword = (String)ini.get(ATTR_PROXY_PASS);

			System.setProperty("http.proxyUser", proxyUser);
			System.setProperty("http.proxyPassword", proxyPassword);
			System.setProperty("https.proxyUser", proxyUser);
			System.setProperty("https.proxyPassword", proxyPassword);

			Authenticator.setDefault(
					new Authenticator() {
						public PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(proxyUser, proxyPassword.toCharArray());
						}
					}
					);
		}

		serviceUrl = ini.getProperty(ATTR_SERVICE_URL);

	}

	/**
	 * Login onto remote service
	 * @param username the username 
	 * @param password the password for the user
	 * @throws AuthenticationError
	 */
	public void login(String username, String password) throws AuthenticationError{
		try{
			String parameters =  "";
			String request = serviceUrl + "login";
			setLoggedOK(false); 
			HttpURLConnection connection;
			connection = prepareHttpURLConection(request, parameters, 
					"POST", "application/json; charset=UTF-8");      

			JsonObjectBuilder jsonParam =  Json.createObjectBuilder();

			jsonParam.add("username", username);
			jsonParam.add("passwd", password);
			OutputStream os = connection.getOutputStream();
			JsonWriter jsonWriter = Json.createWriter(os);
			jsonWriter.writeObject(jsonParam.build());			
			os.close();
			int HttpResult =connection.getResponseCode();  
			if(HttpResult == HttpURLConnection.HTTP_OK){ 
				try (InputStream is = connection.getInputStream();
						JsonReader rdr = Json.createReader(is)){//try
					JsonObject jsonObj = rdr.readObject();
					System.out.println("Leido en login: "+ jsonObj.toString());
					idUser = jsonObj.getString("user","");
					authType = jsonObj.getString("Authorization","");
					apikey =   jsonObj.getString("apikey","");		
					isAdministrator = jsonObj.containsKey("administrator");
					setLoggedOK(true); //user was logged;
				}//try
			}else{  
				Logger.getGlobal().log(Level.SEVERE,connection.getResponseMessage()); 
				throw new AuthenticationError(connection.getResponseMessage());
			}  
		} catch (MalformedURLException e) {  
			//e.printStackTrace();  
			throw new AuthenticationError(e.getMessage());
		}  
		catch (IOException e) {  
			//e.printStackTrace();  
			throw new AuthenticationError(e.getMessage());
		} 
		catch (Exception e) {  
			//e.printStackTrace();  
			throw new AuthenticationError(e.getMessage());
		} 

	}

	/****************************/

	/**
	 * 
	 * @return user id logged in
	 */
	public String getIdUser(){
		return idUser;
	}

	/**
	 * 
	 * @return true if user logged is an administrator
	 */
	public boolean isAdministrator(){
		return isAdministrator;
	}


	/**
	 * 
	 * @return auth token header for user logged in
	 */
	private  String getAuthTokenHeader(){
		//trick for anonymous group APIKEY 
		String aux = (authType == null)? "PUIRESTAUTH": authType;
		String authHeader = aux + " apikey=" + apikey;
		return authHeader;
	}

	/**
	 * @return the loggedOK
	 */
	public boolean isLoggedOK() {
		return loggedOK;
	}

	/**
	 * Sets an API key for access without login. It can only be setup once time.
	 * After login, the API key is replaced with the user API key
	 * @param key
	 */
	public void setAnonymousAPIKey (String key) {
		if (this.apikey == null) {
			this.apikey = key;
		}
	}
	
	/**
	 * @param loggedOK the loggedOK to set
	 */
	private void setLoggedOK(boolean loggedOK) {
		this.loggedOK = loggedOK;
	}

	/**
	 * This method asks for articles that can be shown to the user logged or to any user
	 * @return the list of articles in remote service
	 * @throws ServerCommunicationError
	 * @throws IOException 
	 */
	public List<Article> getArticles() throws ServerCommunicationError, IOException{
		List<Article> result = new ArrayList<Article>();
		String parameters =  "";
		String request = serviceUrl + "articles"; 
		HttpURLConnection connection =  this.getHttpURLConection(request, parameters,
				"GET", "application/x-www-form-urlencoded; charset=UTF-8"); 
		if(connection!=null){//IF OK
			try (InputStream is = connection.getInputStream();
					JsonReader rdr = Json.createReader(is)) {//try 2
				JsonArray arryObj = rdr.readArray();
				System.out.println("Leido: "+arryObj.size());
				for (int i = 0; i< arryObj.size(); i++) {//for
					JsonObject obj = arryObj.getJsonObject(i);
					//System.out.println("element read ("+i+"): "+obj.toString());	
					try {
						Article article;
						obj = getFullArticle(obj.getString("id"));
						if (obj != null) {
					
							article = JsonArticle.jsonToArticle(obj);
							result.add(article);	
						}
						
					} catch (ErrorMalFormedNews e) {
						// TODO Auto-generated catch block
						Logger.getGlobal().log(Level.SEVERE, e.getMessage());
					}
				}//for

			}//Try 2
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//IF OK
	  
		return result;
	}

	/**
	 * This method retrieve the full article 
	 * @param idArticle the id for article to retrieve
	 * @return a jsonObject with the article data
	 */
	private JsonObject getFullArticle(String idArticle) {
		String parameters =  "";
		String request = serviceUrl + "article/"+idArticle;
		HttpURLConnection connection =  this.getHttpURLConection(request, parameters,
				"GET", "application/x-www-form-urlencoded; charset=UTF-8"); 
		if(connection!=null){//IF OK
			try (InputStream is = connection.getInputStream();
					JsonReader rdr = Json.createReader(is)) {//try 2
				JsonObject fullArticle = rdr.readObject();
				return fullArticle;
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Logger.getGlobal().log(Level.SEVERE, e.getMessage());
			}
		}//IF
		return null;
	}

	/**
	 * Send an article to server. 
	 * @param article
	 * @return the article id
	 * @throws ServerCommunicationError
	 */
	public int saveArticle(Article article) throws ServerCommunicationError{
		try{
			String parameters =  "";
			String request = serviceUrl + "article";

			HttpURLConnection connection;
			connection = prepareHttpURLConection(request, parameters, 
					"POST", "application/json; charset=UTF-8");
			OutputStream os = connection.getOutputStream();
			JsonWriter jsonWriter = Json.createWriter(os);
			JsonObject obj = JsonArticle.articleToJson(article);
			jsonWriter.writeObject(obj);			
			os.close();
			int HttpResult =connection.getResponseCode();
			if(HttpResult ==HttpURLConnection.HTTP_OK){
				int idArticle = 0;
				// get id from status ok when saved
				try (InputStream is = connection.getInputStream();
						JsonReader rdr = Json.createReader(is)){//try
					JsonObject jsonObj = rdr.readObject();
					String id = jsonObj.getString("id", "0");
					idArticle = Integer.parseInt(id);
				}
				
				 
				Logger.getGlobal().log(Level.INFO, "Object inserted, returned id:" + idArticle);
				return idArticle;
			}else{  
				throw new ServerCommunicationError(connection.getResponseMessage());
			}  
		} catch (Exception e) {  
			Logger.getGlobal().log(Level.SEVERE,"Inserting article ["+article.getTitle()+"] : " + e.getClass() + " ( "+e.getMessage() + ")");
			throw new ServerCommunicationError(e.getClass() + " ( "+e.getMessage() + ")");
		}
	}

	/**
	 * This method deletes the given article, if only if the logged user is the article's author
	 * @param idArticle
	 * @throws ServerCommunicationError
	 */
	public void deleteArticle(int idArticle) throws ServerCommunicationError{
		try{
			String parameters =  "";
			String request = serviceUrl + "article/" + idArticle;
			HttpURLConnection connection;
			connection = this.getHttpURLConection(request, parameters, 
					"DELETE", "application/x-www-form-urlencoded; charset=UTF-8");
			if (connection != null) {
				Logger.getGlobal().log (Level.INFO,"Article (id:"+idArticle+") deleted");
			}
		} catch (Exception e) {  
			Logger.getGlobal().log(Level.SEVERE, "Deleting article (id:"+idArticle+") : " + e.getClass() + " ( "+e.getMessage() + ")");
			throw new ServerCommunicationError(e.getClass() + " ( "+e.getMessage() + ")");
		}  
	}

	



	//Auxiliary services
	private HttpURLConnection getHttpURLConection (String request, String parameters,
			String requestMethd, String contentType) {
		HttpURLConnection connection = null;
		
		try {
			connection = prepareHttpURLConection(request, parameters, requestMethd, contentType);
			int HttpResult =connection.getResponseCode();  
			if(HttpResult !=HttpURLConnection.HTTP_OK 
					&& HttpResult !=HttpURLConnection.HTTP_NO_CONTENT ){//IF OK
				connection = null; //It was impossible establish a connection
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			connection = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			connection = null;
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			connection = null;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			connection = null;
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			connection = null;
		} 
		return connection;
	}
	
	private HttpURLConnection prepareHttpURLConection (String request, String parameters,
			String requestMethd, String contentType) throws IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		HttpURLConnection connection = null;
		URL url;
		url = new URL(request);
		connection = (HttpURLConnection) url.openConnection();      
		if(requireSelfSigned)
			TrustModifier.relaxHostChecking(connection); 
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setInstanceFollowRedirects(false); 
		connection.setRequestMethod(requestMethd); 
		connection.setRequestProperty("Content-Type",contentType); 
		connection.setRequestProperty("Authorization", getAuthTokenHeader()); //pasar API KEY 
		connection.setRequestProperty("charset", "utf-8");
		connection.setRequestProperty("Content-Length", "" + Integer.toString(parameters.getBytes().length));
		connection.setUseCaches (false);
		return connection;
	}
}
