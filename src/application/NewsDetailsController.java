/**
 * 
 */
package application;




import javafx.scene.image.ImageView;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import application.news.Article;
import application.news.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;


/**
 * @author ÁngelLucas
 *
 */
public class NewsDetailsController {
	//TODO add attributes and methods as needed
	    private User usr;
	    private Article article;
	    
	    @FXML
	    private Button onBack;
	    
	    @FXML
	    private Button onChange;
	    
	    
	    @FXML // fx:id="titleBox"
	    private Label title; // Value injected by FXMLLoader

	    @FXML // fx:id="sutitleBox"
	    private Label subtitle; // Value injected by FXMLLoader

	    @FXML // fx:id="categoy"
	    private Label category; // Value injected by FXMLLoader
	    
	    
	    @FXML // fx:id="articleBox"
 	    private WebView bodyText; // Value injected by FXMLLoader
	    
	    @FXML // fx:id="imageView"
	    private ImageView imgView; // Value injected by FXMLLoader
	    
	    private boolean isBody = true;
//	    
//	    
	    @FXML
	    void onBackAction(ActionEvent event) {
	        Stage stage = (Stage) onBack.getScene().getWindow();
	        stage.close();
	    }
	    
	    @FXML
	    void changeAction(ActionEvent event) {
	    	if (this.isBody) {
//	    		System.out.println("changed abstract");
//	    		System.out.println(article.getBodyText());
//	    		System.out.println(article.getAbstractText());
	    		this.bodyText.getEngine().loadContent(article.getAbstractText());
	    		this.isBody = false;
	    	}else {
	    		this.bodyText.getEngine().loadContent(article.getBodyText());
	    		this.isBody = true;
	    	} 
	    }
	   
	    	    

	    

		/**
		 * @param usr the usr to set
		 */
		void setUsr(User usr) {
			this.usr = usr;
			if (usr == null) {
				return; //Not logged user
			}
			//TODO Update UI information
		}

		/**
		 * @param article the article to set
		 */
		void setArticle(Article article) {
			
			this.article = article;
			this.title.setText(article.getTitle());
			this.subtitle.setText(article.getSubtitle());
			this.category.setText(article.getCategory());
			this.bodyText.getEngine().loadContent(article.getBodyText());
			this.imgView.setImage(article.getImageData());
			
			//TODO complete this method
		}
}
