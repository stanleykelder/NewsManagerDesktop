package application;


import java.util.Properties;

import application.news.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
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

	public LoginController () throws AuthenticationError{
		loginModel.setDummyData(false);
		
		
		//Create properties here? NO! Remove and setConnectionManager
		Properties prop = Main.buildServerProperties();
		ConnectionManager connection = new ConnectionManager(prop);
		loginModel.setConnectionManager(connection);
	}
	
	@FXML
	void onLoginClicked (ActionEvent event) {
		loggedUsr = this.loginModel.validateUser(username.getText(), password.getText());
		
		//NewsReaderController.setUsr(loggedUsr);
		
		
	}
	
	User getLoggedUsr() {
		return loggedUsr;
		
	}
		
	void setConnectionManager (ConnectionManager connection) {
		this.loginModel.setConnectionManager(connection);
	}
}