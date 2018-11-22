/**
 * 
 */
package application;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.json.JsonObject;

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
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
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

	private ConnectionManager connection;
	private NewsReaderModel newsReaderModel = new NewsReaderModel();
	private User usr;
	private ObservableList<Categories> categoryList;
	private ObservableList<Article> articleList;
	private ObservableList<String> menuItems;
	File folder = new File("saveNews//");
	File[] listOfFiles = folder.listFiles();

	@FXML
	private Label idUserLabel;

	@FXML
	private ImageView imgView;

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

	@FXML
	private Button closeButton;

	public NewsReaderController() {
		categoryList = newsReaderModel.getCategories();
		articleList = newsReaderModel.getArticles();
		menuItems = FXCollections.observableArrayList("Load news from file", "Login", "New", "Edit", "Delete");
	}

	@FXML
	void initialize() {
		assert categoryList != null : "fx:id=\"categoryList\" was not injected: check your FXML file 'NewsReader.fxml'.";
		assert headlineList != null : "fx:id=\"headlineList\" was not injected: check your FXML file 'NewsReader.fxml'.";
		assert bodyWebView != null : "fx:id=\"bodyWebView\" was not injected: check your FXML file 'NewsReader.fxml'.";
		getData();
		this.categoryBox.setItems(categoryList);
		this.categoryBox.getSelectionModel().selectFirst();
		this.headlineList.setItems(articleList);
		this.menuList.setItems(menuItems);
		this.idUserLabel.setText("Not logged in");

		
		
		
		// for category filter
		FilteredList<Article> filteredItems = new FilteredList<Article>(articleList, p -> true);
		this.headlineList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Article>() {
			@Override
			public void changed(ObservableValue<? extends Article> observable, Article oldValue, Article newValue) {
				if (newValue != null) {
					readMoreBtn.setDisable(false);
					bodyWebView.getEngine().loadContent(newValue.getAbstractText());
					imgView.setImage(newValue.getImageData());
				} else {
					bodyWebView.getEngine().loadContent("");
					readMoreBtn.setDisable(true);
				}

			}

		});

		this.categoryBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Categories>() {
			@Override
			public void changed(ObservableValue<? extends Categories> observable, Categories oldValue,
					Categories newValue) {
				if (newValue != Categories.ALL || newValue == oldValue) {
					filteredItems.setPredicate(item -> {
						if (item.getCategory().equals(newValue.toString())) {
							return true;
						} else {
							return false;
						}
					});
					headlineList.setItems(filteredItems);
					imgView.setImage(null);
				} else {
					headlineList.setItems(articleList);
				}

			}

		});

	}

// code for linking 
//	(I think this is not the best way to do it, because there is a lot of repetition. But my quick try 
//	to remove the repetition did not work)
	@FXML
	void onMenuClicked(MouseEvent event) {
		if (event.getClickCount() >= 2) {
			// load scene and loader responding to item
			if (menuList.getSelectionModel().getSelectedItem().equals("Login")) {
				Scene parentScene = ((Node) event.getSource()).getScene();
				FXMLLoader loader = null;
				try {
					loader = new FXMLLoader(getClass().getResource("Login.fxml"));
					Pane root = loader.load();
					Scene scene = new Scene(root);
					scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
					Window parentStage = parentScene.getWindow();
					Stage stage = new Stage();
					stage.initOwner(parentStage);
					stage.setScene(scene);
					// Get the controller for NewsEdit.fxml
					LoginController controller = loader.<LoginController>getController();
					controller.setConnectionManager(connection);
					// Uncomment next sentence if you want an undecorated window
					// stage.initStyle(StageStyle.UNDECORATED);
					// user response is required before continuing with the program
					stage.initModality(Modality.WINDOW_MODAL);
					stage.showAndWait();
					// set the User after having logged in
					usr = controller.getLoggedUsr();
					if (usr != null) {
						idUserLabel.setText(usr.getLogin());	
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (menuList.getSelectionModel().getSelectedItem().equals("New")) {
				Scene parentScene = ((Node) event.getSource()).getScene();
				FXMLLoader loader = null;
				try {
					loader = new FXMLLoader(getClass().getResource("NewsEdit.fxml"));
					Pane root = loader.load();
					Scene scene = new Scene(root);
					scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
					Window parentStage = parentScene.getWindow();
					Stage stage = new Stage();
					stage.initOwner(parentStage);
					stage.setScene(scene);
					// Get the controller for NewsEdit.fxml
					NewsEditController controller = loader.<NewsEditController>getController();
					// pass connection and user, Article has to be (and is) empty
					controller.setConnectionManager(connection);
					controller.setUsr(usr);
					controller.setArticle(new Article());
					// Uncomment next sentence if you want an undecorated window
					// stage.initStyle(StageStyle.UNDECORATED);
					// user response is required before continuing with the program
					stage.initModality(Modality.WINDOW_MODAL);
					stage.showAndWait();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (menuList.getSelectionModel().getSelectedItem().equals("Edit")) {
				Scene parentScene = ((Node) event.getSource()).getScene();
				FXMLLoader loader = null;
				try {
					loader = new FXMLLoader(getClass().getResource("NewsEdit.fxml"));
					Pane root = loader.load();
					Scene scene = new Scene(root);
					scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
					Window parentStage = parentScene.getWindow();
					Stage stage = new Stage();
					stage.initOwner(parentStage);
					stage.setScene(scene);
					// Get the controller for NewsEdit.fxml
					NewsEditController controller = loader.<NewsEditController>getController();
					controller.setConnectionManager(connection);
					controller.setUsr(usr);
					// here the selected article is passed
					Article selected = this.headlineList.getSelectionModel().getSelectedItem();
					controller.setArticle(selected);
					// Uncomment next sentence if you want an undecorated window
					// stage.initStyle(StageStyle.UNDECORATED);
					// user response is required before continuing with the program
					stage.initModality(Modality.WINDOW_MODAL);
					stage.showAndWait();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (menuList.getSelectionModel().getSelectedItem().equals("Load news from file")) {
				Scene parentScene = ((Node) event.getSource()).getScene();
				FXMLLoader loader = null;
				try {
					loader = new FXMLLoader(getClass().getResource("NewsEdit.fxml"));
					Pane root = loader.load();
					Scene scene = new Scene(root);
					scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
					Window parentStage = parentScene.getWindow();
					Stage stage = new Stage();
					stage.initOwner(parentStage);
					stage.setScene(scene);
					// Get the controller for NewsEdit.fxml
					NewsEditController controller = loader.<NewsEditController>getController();
					controller.setConnectionManager(connection);
					controller.setUsr(usr);
					
					List<String> listOfFileNames = new ArrayList<String>();
					
					// here the selected article is passed
					for (int i = 0; i < listOfFiles.length; i++) {
						  if (listOfFiles[i].isFile()) {
							  listOfFileNames.add(listOfFiles[i].getName());
						  } else if (listOfFiles[i].isDirectory()) {
						    System.out.println("Directory " + listOfFiles[i].getName());
						  }
						}
					
					String[] arrFileNames = new String[ listOfFileNames.size() ];
					listOfFileNames.toArray( arrFileNames );
					
					ChoiceDialog dialog = new ChoiceDialog(arrFileNames[0], arrFileNames);
					
					System.out.println("dialog " + dialog.getItems());
					dialog.setTitle("Load from file");
					dialog.setHeaderText("Select a file you want to edit:");
					dialog.setContentText("Article:");

					Optional<String> result = dialog.showAndWait();
					System.out.println("result " + result);
					result.ifPresent(articleName -> {
						System.out.println("articleName " + articleName);
						JsonObject jsonObj = JsonArticle.readFile("saveNews/" + articleName);
						System.out.println(jsonObj);
						try {
							Article selected = JsonArticle.jsonToArticle(jsonObj);
							controller.setArticle(selected);
						} catch (ErrorMalFormedNews e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					});

					
					// Uncomment next sentence if you want an undecorated window
					// stage.initStyle(StageStyle.UNDECORATED);
					// user response is required before continuing with the program
					stage.initModality(Modality.WINDOW_MODAL);
					stage.showAndWait();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Open the editing form and pass the contact data. It waits until edit is
	 * finished and, at the end, update contact data
	 */
	@FXML
	void onReadMore(ActionEvent event) {
		Scene parentScene = ((Node) event.getSource()).getScene();
		FXMLLoader loader = null;
		try {
			loader = new FXMLLoader(getClass().getResource("NewsDetails.fxml"));
			Pane root = loader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Window parentStage = parentScene.getWindow();
			Stage stage = new Stage();
			stage.initOwner(parentStage);
			stage.setScene(scene);
			// Get the controller for NewsDetailsController.fxml
			NewsDetailsController controller = loader.<NewsDetailsController>getController();
			Article selected = this.headlineList.getSelectionModel().getSelectedItem();
			controller.setArticle(selected);
			// Uncomment next sentence if you want an undecorated window
			// stage.initStyle(StageStyle.UNDECORATED);
			// user response is required before continuing with the program
			stage.initModality(Modality.WINDOW_MODAL);
			stage.showAndWait();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@FXML
	private void closeButtonAction() {
		// handle to the stage
		Stage stage = (Stage) closeButton.getScene().getWindow();
		stage.close();
	}

	private void getData() {
		// retrieve data and update UI
		newsReaderModel.retrieveData();
	}

	/**
	 * @return the usr
	 */
	User getUsr() {
		return usr;
	}

	void setConnectionManager(ConnectionManager connection) {
		this.newsReaderModel.setDummyData(false); // System is connected so dummy data
		// are not needed
		this.newsReaderModel.setConnectionManager(connection);
		this.getData();
		this.connection = connection;
	}

	/**
	 * @param usr the usr to set
	 */
	void setUsr(User usr) {

		this.usr = usr;
		// Reload articles
		this.getData();
		// TODO Update UI
	}

	// Auxiliary methods
	private interface InitUIData<T> {
		void initUIData(T loader);
	}
}
