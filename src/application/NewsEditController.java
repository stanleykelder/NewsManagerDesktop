/**
 * 
 */
package application;

import java.io.FileWriter;
import java.io.IOException;

import javax.json.JsonObject;


import application.news.Article;
import application.news.Categories;
import application.news.User;
import application.utils.JsonArticle;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import serverConection.ConnectionManager;
import serverConection.exceptions.ServerCommunicationError;

/**
 * @author Ã�ngelLucas
 * @author StanleyKelder
 */
public class NewsEditController {
    private ConnectionManager connection;
	private NewsEditModel editingArticle;
	private User usr;
	private boolean body = true;
	private boolean html = false;
	
	
	//TODO add attributes and methods as needed
	@FXML 
    private ImageView articleImage;
	@FXML
	private TextField articleTitle;
	@FXML
	private TextField articleSubtitle;
	@FXML
	private TextArea bodyText;
	@FXML
	private TextArea abstractText;
	@FXML
	private HTMLEditor bodyHTML;
	@FXML
	private HTMLEditor abstractHTML;
	@FXML
    private ComboBox<Categories> categoryBox;


	@FXML //code to select image
	void onImageClicked(MouseEvent event) {
		if (event.getClickCount() >= 2) {
			Scene parentScene = ((Node) event.getSource()).getScene();
			FXMLLoader loader = null;
			try {
				loader = new FXMLLoader(getClass().getResource(AppScenes.IMAGE_PICKER.getFxmlFile()));
				Pane root = loader.load();
				// Scene scene = new Scene(root, 570, 420);
				Scene scene = new Scene(root);
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				Window parentStage = parentScene.getWindow();
				Stage stage = new Stage();
				stage.initOwner(parentStage);
				stage.setScene(scene);
				stage.initStyle(StageStyle.UNDECORATED);
				stage.initModality(Modality.WINDOW_MODAL);
				stage.showAndWait();
				ImagePickerController controller = loader.<ImagePickerController>getController();
				Image image = controller.getImage();
				if (image != null) {
					//IMAGE ON UI
					articleImage.setImage(image);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	
	@FXML 
	void initialize() {
		//initialize picture
		assert articleImage != null : "fx:id=\"articleImage\" was not injected: check your FXML file 'NewsEdit.fxml'.";
        Image image = new Image("images/selectImage.png", true);
        articleImage.setImage(image);
        
        //initialize category dropdown
        this.categoryBox.getItems().setAll(Categories.values());
        this.categoryBox.getSelectionModel().selectFirst();
    }
	
	@FXML
	void onBodyAbstractClicked (ActionEvent event) {
		if (html) {
			abstractHTML.setVisible(body);
			body = !body;
			bodyHTML.setVisible(body);
		} else {
			abstractText.setVisible(body);
			body = !body;
			bodyText.setVisible(body);
		}
	}
	
	@FXML
	void onTextHTMLClicked (ActionEvent event) {
		if (body) {
			bodyText.setVisible(html);
			html = !html;
			bodyHTML.setVisible(html);
		} else {
			abstractText.setVisible(html);
			html = !html;
			abstractHTML.setVisible(html);
		}
	}
	
	@FXML
	void onSaveFileClicked (ActionEvent event) {
		this.editingArticle.setTitle(articleTitle.getText());
		System.out.println(articleTitle.getText());
		
		this.editingArticle.setSubtitle(articleSubtitle.getText());
		System.out.println(articleSubtitle.getText());
		
		this.editingArticle.setBody(bodyText.getText());
		System.out.println(bodyText.getText());
		
		this.editingArticle.setCategory(categoryBox.getValue());
		System.out.println(categoryBox.getValue());
		
		this.editingArticle.setImage(articleImage.getImage());
		System.out.println(articleImage.getImage());
				
		
		write();
	}
	
	/**
	 * Send and article to server,
	 * Title and category must be defined and category must be different to ALL
	 * @return true if the article has been saved
	 */
	private boolean send() {
		String titleText = null; // TODO Get article title
		Categories category = null; //TODO Get article cateory
		if (titleText == null || category == null || 
				titleText.equals("") || category == Categories.ALL) {
			Alert alert = new Alert(AlertType.ERROR, "Imposible to send the article. Title and categoy are mandatory", ButtonType.OK);
			alert.showAndWait();
			return false;
		}
//TODO prepare and send using connection.saveArticle( ...)
		
		return true;
	}
	
	/**
	 * This method is used to set the connection manager which is
	 * needed to save a news 
	 * @param connection connection manager
	 */
	void setConnectionMannager(ConnectionManager connection) {
		this.connection = connection;
		//TODO enable save and send button
	}

	/**
	 * 
	 * @param usr the usr to set
	 */
	void setUsr(User usr) {
		this.usr = usr;
		
		System.out.println("ReceivedID: " + usr.getIdUser());
		//TODO Update UI and controls 
		
	}

//Where should I use this?
	Article getArticle() {
		Article result = null;
		if (this.editingArticle != null) {
			result = this.editingArticle.getArticleOriginal();
		}
		return result;
	}

	/**
	 * PRE: User must be set
	 * 
	 * @param article
	 *            the article to set
	 */
	void setArticle(Article article) {

		this.editingArticle = (article != null) ? new NewsEditModel(usr, article) : new NewsEditModel(usr);
		//TODO update UI
//		Article editedArticle = this.getArticle();
		this.editingArticle.setTitle(articleTitle.getText());
	}
	
	/**
	 * Save an article to a file in a json format
	 * Article must have a title
	 */
	private void write() {
		
		//TODO Consolidate all changes	
		this.editingArticle.commit();
		//Removes special characters not allowed for filenames
		String name = this.getArticle().getTitle().replaceAll("\\||/|\\\\|:|\\?","");
		String fileName ="saveNews//"+name+".news";
		JsonObject data = JsonArticle.articleToJson(this.getArticle());
		  try (FileWriter file = new FileWriter(fileName)) {
	            file.write(data.toString());
	            file.flush();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	}
}
