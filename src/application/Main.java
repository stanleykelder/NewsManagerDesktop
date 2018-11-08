package application;
	
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


//import org.omg.CORBA.portable.InputStream;

import application.news.Article;
import application.news.Categories;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import serverConection.ConnectionManager;
import serverConection.exceptions.AuthenticationError;
import serverConection.exceptions.ServerCommunicationError;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			//This start method allow us to load a Scene (only one).
			//Uncomment the desire to load scene and comment the other ones 
			/*
			 * We use an instance of Pane, so we don't worry about what kind of pane is used
			 * in the FXML file. Pane is the father of all container (BorderPane, 
			 * FlowPane, AnchorPane ...
			 */
			/*Pane root = FXMLLoader.load(getClass().getResource(
					AppScenes.LOGIN.getFxmlFile()));*/
			/*Pane root = FXMLLoader.load(getClass().getResource(
					AppScenes.IMAGE_PICKER.getFxmlFile()));*/
			//Code for reader main window
			FXMLLoader loader = new FXMLLoader (getClass().getResource(
					AppScenes.READER.getFxmlFile()));
			Pane root = loader.load();
			NewsReaderController controller = loader.<NewsReaderController>getController();
		
			//Create properties for server connection
			Properties prop = buildServerProperties();
			ConnectionManager connection = new ConnectionManager(prop);
			//Connecting as public (anonymous) for your group
			connection.setAnonymousAPIKey(""/*Put your group API Key here*/);
		//Login whitout login form:
			//	connection.login("Reader2", "reader2"); //User: Reader2 and password "reader2" 
		//    User user = new User ("Reader2", 
		//	Integer.parseInt(connection.getIdUser()));
		//	controller.setUsr(user);
			controller.setConnectionManager(connection);		
			
			//end code for main window reader
			
		
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.initStyle(StageStyle.UNDECORATED);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(AuthenticationError e) {
			Logger.getGlobal().log(Level.SEVERE, "Error in loging process");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	final static Properties buildServerProperties() {
		Properties prop = new Properties();
		prop.setProperty(ConnectionManager.ATTR_SERVICE_URL, "https://sanger.dia.fi.upm.es/pui-rest-news/");
		prop.setProperty(ConnectionManager.ATTR_REQUIRE_SELF_CERT, "TRUE");
		
		/* For http & https proxy 
		prop.setProperty(ConnectionManager.ATTR_PROXY_HOST, "http://proxy.fi.upm.es");
		prop.setProperty(ConnectionManager.ATTR_PROXY_PORT, "80");
		*/
		/* For proxy or apache password auth 
		prop.setProperty(ConnectionManager.ATTR_PROXY_USER, "...");
		prop.setProperty(ConnectionManager.ATTR_PROXY_PASS, "...");
		*/
		return prop;
	}
	
}
