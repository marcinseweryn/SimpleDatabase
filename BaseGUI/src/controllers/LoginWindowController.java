package controllers;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import mysql.Bans;
import mysql.ConnectionToDatabase;

public class LoginWindowController {

	private static Integer LIBRARY_CARD_NUMBER;
	private static String NAME, BANNED_INFO;
	
    @FXML
    private JFXPasswordField passwordField;

    @FXML
    private JFXTextField loginField;
    
    @FXML
    private JFXButton loginInButton, menuButton;
    
	
	public static String getBannedInfo() {
		return BANNED_INFO;
	}

	public static void setBannedInfo(String bannedInfo) {
		LoginWindowController.BANNED_INFO = bannedInfo;
	}

	public static String getName() {
		return NAME;
	}

	public static Integer getLibraryCardNumber() {
		return LIBRARY_CARD_NUMBER;
	}


    @FXML
    void initialize() throws ClassNotFoundException, SQLException{
    	Bans bans = new Bans();
    	bans.deleteExpiretBans();
    }
    
    
    @FXML
    void backToStartAction(ActionEvent event) throws IOException {
    	Parent parent = FXMLLoader.load(getClass().getResource("/fxml/StartWindow.fxml"));
    	Scene scene = new Scene(parent);
    	Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    	
    	stage.setScene(scene);
    	stage.show();
    }

    @FXML
    void loginInAction(ActionEvent event) throws IOException, SQLException, ClassNotFoundException {
    	
    	String username = null, password = null;
    	
		try {
			FileInputStream fis = new FileInputStream("ConfigurationFile");
			DataInputStream dis =  new DataInputStream(fis);
			dis.readUTF();dis.readUTF();dis.readUTF();
			username = dis.readUTF();
			password = dis.readUTF();

			dis.close();
			fis.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
    	
    	
    	if(passwordField.getText().equals(password) && loginField.getText().equals(username)){
        	Parent parent = FXMLLoader.load(getClass().getResource("/fxml/admin/MenuWindow.fxml"));
        	Scene scene = new Scene(parent);
        	Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        	
        	stage.setScene(scene);
        	stage.show();
    		
    	}else{
    		ConnectionToDatabase connectionToDatabase = new ConnectionToDatabase();
    		Connection con=connectionToDatabase.getConnection();
    		PreparedStatement getUsers=con.prepareStatement("SELECT LibraryCardNumber,Password,FirstName,Banned FROM Users "
    				+ "WHERE LibraryCardNumber=" + Integer.parseInt(loginField.getText()) );
    		ResultSet rs=getUsers.executeQuery();
    		
    		
    		boolean passwordOK=false;
    		while(rs.next()){
    			LIBRARY_CARD_NUMBER=rs.getInt("LibraryCardNumber");
    			password=rs.getString("Password");
    			NAME = rs.getString("FirstName");
    			BANNED_INFO = rs.getString("Banned");
    			
    			if(LIBRARY_CARD_NUMBER == Integer.parseInt(loginField.getText()) && password.equals(passwordField.getText())){   				
    	        	Parent parent = FXMLLoader.load(getClass().getResource("/fxml/user/UserMenuWindow.fxml"));
    	        	Scene scene = new Scene(parent);
    	        	Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    	        	
    	        	stage.setScene(scene);
    	        	stage.show();
    	        	passwordOK=true;
    	        	break;
    			}
    		}
    		if(passwordOK==false){
    			Alert alert = new Alert(AlertType.ERROR);
    			alert.setTitle("ERROR");
    			alert.setHeaderText("Wrong password!");
    			alert.showAndWait();
    		}
    		con.close();
    	}
    }

    @FXML
    void loginInMouseEntered(MouseEvent event) {
    	loginInButton.setStyle("-fx-background-color:  #673ab7");
    }

    @FXML
    void loginInMouseExited(MouseEvent event) {
    	loginInButton.setStyle("-fx-background-color:  #2196f3");
    }

    @FXML
    void menuMouseEntered(MouseEvent event) {
    	menuButton.setStyle("-fx-background-color:  #673ab7");
    }

    @FXML
    void menuMouseExited(MouseEvent event) {
    	menuButton.setStyle("-fx-background-color:  #2196f3");
    }
	
}
