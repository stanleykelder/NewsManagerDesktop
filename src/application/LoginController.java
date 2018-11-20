package application;


import java.util.Properties;

import application.news.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import serverConection.ConnectionManager;
import serverConection.exceptions.AuthenticationError;

public class LoginController {
//TODO Add all attribute and methods as needed 
	private LoginModel loginModel = new LoginModel();
	
	private User loggedUsr = null;
	
	@FXML
	private TextField username;
	@FXML
	private TextField password;
	@FXML
	private Button Cancel;
	
	public LoginController () throws AuthenticationError{
		loginModel.setDummyData(false);
	}
	
	@FXML
	void onLoginClicked (ActionEvent event) {
		loggedUsr = this.loginModel.validateUser(username.getText(), password.getText());
		
		if (loggedUsr != null) {
	 		//close stage
			Stage stage = (Stage) Cancel.getScene().getWindow();
	        stage.close();
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Wrong username password");
			alert.setHeaderText("Your username or password seems to be incorrect. ");
			alert.setContentText("Please try again.");

			alert.showAndWait();
		}
		
	}

//	TODO
	@FXML
	void onCancelClicked (ActionEvent event) {
		//close stage
		Stage stage = (Stage) Cancel.getScene().getWindow();
        stage.close();
	}
	
	User getLoggedUsr() {
		return loggedUsr;
		
	}
		
	void setConnectionManager (ConnectionManager connection) {
		this.loginModel.setConnectionManager(connection);
	}
}