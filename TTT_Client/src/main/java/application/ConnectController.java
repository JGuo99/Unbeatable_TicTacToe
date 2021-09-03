package application;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import ClientLogic.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ConnectController implements Initializable {
	LobbyController controller;
	
	//IP Address string pattern for matching
		//Credit to mkyong at: https://www.mkyong.com/regular-expressions/how-to-validate-ip-address-with-regular-expression/
		private static final String IPADDRESS_PATTERN =
				"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
				"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
				"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
				"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
		private Pattern ip_Pattern;
	@FXML
    private Node root;

    @FXML
    private TextField ipAddress_TextField;

    @FXML
    private TextField serverPort_TextField;

    @FXML
    private Button connect_Button;

	public Client clientConnection;


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

	}

	public void connect() {
		try {
			Client clientConnection;
			String address = ipAddress_TextField.getText();
			ip_Pattern = Pattern.compile(IPADDRESS_PATTERN);
			if(!ip_Pattern.matcher(address).matches())
				throw new WrongAddressFormatException("Address is not in a valid format");
			
			int portNumber = Integer.parseInt(serverPort_TextField.getText());
					
			clientConnection = new Client( address, portNumber);
			clientConnection.setStartCallback(data->{
				Platform.runLater(()->{
					Boolean serverStart = (Boolean)data;
					
					if(!serverStart) {
						PopUps.alert("Could not connect to server.");
						return;
					}
					
					try{
						Stage stage = (Stage) root.getScene().getWindow();
						FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/LobbyScene.fxml"));
						Parent newRoot = loader.load();
						controller = loader.getController();
						controller.setClientConnection(clientConnection);			
						stage.setScene(new Scene(newRoot));
					} catch(Exception e){
						e.printStackTrace();
					}
				});
			});

			
			clientConnection.start();

		} catch(WrongAddressFormatException e) {
			PopUps.alert(e.getMessage());
		} catch(NumberFormatException e) {
			PopUps.alert("Server port is not valid.");
		} catch(Exception e) {
			e.printStackTrace();
			PopUps.alert("Could not connect to server.");
		}

	}
	
	class WrongAddressFormatException extends Exception{
		WrongAddressFormatException(String message){
			super(message);
		}
	}//end WrongAddressFormatException{}...
		
}
