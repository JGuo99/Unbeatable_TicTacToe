package application;

import java.net.URL;
import java.rmi.server.ServerCloneException;
import java.util.ResourceBundle;

import ServerLogic.Server;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class StartUpController implements Initializable {
	private FXMLLoader loader;
	private Parent newRoot;
	private Stage stage;
	MainController mController;
	
    @FXML
    private Node root;

    @FXML
    private TextField serverPort_TextField;

    @FXML
    private Button startServer_Button;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}

	 @FXML
    void startServer(ActionEvent event) {
		 Server serverConnection;
         
		 try {
				int portNumber = Integer.parseInt(serverPort_TextField.getText());
				
				stage = (Stage) root.getScene().getWindow();
				loader = new FXMLLoader(getClass().getResource("/FXML/MainScene.fxml"));
	            newRoot = loader.load();        
	            mController = loader.getController();
	            
	            
	            try {    
		            serverConnection = new Server(d-> {
		    			Platform.runLater(()->{
			    			PopUps.alert(d.toString());
		    			});
		    		}, null, portNumber);
		            mController.setServerConnection(serverConnection);
	            } catch(Exception e) {
	            	PopUps.alert("Failed to open port: " + portNumber);
	            	e.printStackTrace();
				}
	            
	            stage.setScene(new Scene(newRoot));		
			} catch(Exception e) {
				PopUps.alert("Please write a number for the port.");
				e.printStackTrace();
			}
		
	}    	
}