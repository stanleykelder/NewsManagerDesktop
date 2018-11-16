/**
 * 
 */
package application;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.function.Predicate;

import application.news.Article;
import application.news.Categories;
import application.news.User;
import application.utils.JsonArticle;
import application.utils.exceptions.ErrorMalFormedNews;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.FileChooser.ExtensionFilter;
import serverConection.ConnectionManager;

/**
 * @author ÁngelLucas
 *
 */
public class NewsReaderController {

	private NewsReaderModel newsReaderModel = new NewsReaderModel();
	private User usr;
	private ObservableList<Categories> categoryList;
	private ObservableList<Article> articleList;
	private ObservableList<String> menuItems;

	@FXML
    private Label idUserLabel;
	
	@FXML
    private ImageView img;

    @FXML
    private ListView<String> menuList;
    
    @FXML
    private ListView<Article> headlineList;
    
    @FXML
    private WebView bodyWebView;
	
	@FXML
    private ComboBox<Categories> categoryBox;
	
    @FXML
    private Button readMoreBtn;
    
    
	

	public NewsReaderController() {
		//Uncomment next sentence to use data from server instead dummy data
		// newsReaderModel.setDummyData(false);
		//Get text Label
		categoryList = newsReaderModel.getCategories();
		articleList = newsReaderModel.getArticles();
		menuItems = FXCollections.observableArrayList (
				"Load news from file", "Login", "New", "Exit", "Edit", "Delte");
		System.out.println(articleList + "constructor");
	}

	@FXML
    void initialize() {
        assert categoryList != null : "fx:id=\"categoryList\" was not injected: check your FXML file 'NewsReader.fxml'.";
        assert headlineList != null : "fx:id=\"headlineList\" was not injected: check your FXML file 'NewsReader.fxml'.";
        assert bodyWebView != null : "fx:id=\"bodyWebView\" was not injected: check your FXML file 'NewsReader.fxml'.";
        getData();
        this.categoryBox.setItems(categoryList);
        this.categoryBox.getSelectionModel().selectFirst();
        System.out.println(this.articleList.toString() + "initialize");
        this.headlineList.setItems(articleList);
        this.menuList.setItems(menuItems);
        
        this.headlineList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Article>() {

			@Override
			public void changed(ObservableValue<? extends Article> observable, Article oldValue, Article newValue) {
				if (newValue != null) {
					//btnNext.setDisable(false);
					bodyWebView.getEngine().loadContent(newValue.toString());
				} else {
					bodyWebView.getEngine().loadContent("");
				}
				
			}
        	
        });
    }	

	private void getData() {
		//TODO retrieve data and update UI
		//The method newsReaderModel.retrieveData() can be used to retrieve data  
		newsReaderModel.retrieveData();
	}

	/**
	 * @return the usr
	 */
	User getUsr() {
		return usr;
	}

	void setConnectionManager (ConnectionManager connection){
		// this.newsReaderModel.setDummyData(false); //System is connected so dummy data are not needed
		this.newsReaderModel.setConnectionManager(connection);
		this.getData();
	}
	
	/**
	 * @param usr the usr to set
	 */
	void setUsr(User usr) {
		
		this.usr = usr;
		//Reload articles
		this.getData();
		//TODO Update UI
	}

	// Auxiliary methods
	private interface  InitUIData <T>{
		void initUIData (T loader);
	}
}
