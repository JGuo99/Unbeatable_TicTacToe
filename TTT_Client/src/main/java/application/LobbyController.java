package application;

import java.net.URL;
import java.util.ResourceBundle;

import ClientLogic.Client;
import application.GameInfo.Difficulty;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LobbyController implements Initializable {
	Client clientConnection;

    @FXML
    private VBox root;

    @FXML
    private Button easy_Button;

    @FXML
    private Button medium_Button;

    @FXML
    private Button hard_Button;

    @FXML
    private Button impossible_Button;

    @FXML
    private Label place1_Label;

    @FXML
    private Label place2_Label;

    @FXML
    private Label place3_Label;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		easy_Button.setOnMouseClicked(new NewGameHandler(Difficulty.EASY));
		medium_Button.setOnMouseClicked(new NewGameHandler(Difficulty.MEDIUM));
		hard_Button.setOnMouseClicked(new NewGameHandler(Difficulty.HARD));
		easy_Button.setDisable(true);
		medium_Button.setDisable(true);
		hard_Button.setDisable(true);
	}

	public void setClientConnection(Client sourceConnection){
		this.clientConnection = sourceConnection;
		
		if(easy_Button.isDisable())   easy_Button.setDisable(false);
		if(medium_Button.isDisable()) medium_Button.setDisable(false);
		if(hard_Button.isDisable())   hard_Button.setDisable(false);
		
		
		if(clientConnection.localLeaderBoard.size() > 0 ) {
			place1_Label.setText("Player #" + clientConnection.localLeaderBoard.get(0));
			if(clientConnection.localGInfo.leaderboard.size() > 1 ) {
				place2_Label.setText("Player #" + clientConnection.localLeaderBoard.get(1));
				if(clientConnection.localGInfo.leaderboard.size() > 2 )
					place3_Label.setText("Player #" + clientConnection.localLeaderBoard.get(2));
			}
		}
		
		clientConnection.setUpdateCallback(data ->{
			Platform.runLater(()->{
				if(clientConnection.localGInfo.leaderboard.size() > 0 ) {
					place1_Label.setText("Player #" + clientConnection.localGInfo.leaderboard.get(0));
					if(clientConnection.localGInfo.leaderboard.size() > 1 ) {
						place2_Label.setText("Player #" + clientConnection.localGInfo.leaderboard.get(1));
						if(clientConnection.localGInfo.leaderboard.size() > 2 )
							place3_Label.setText("Player #" + clientConnection.localGInfo.leaderboard.get(2));
					}
				}
			});
		});
		
		clientConnection.setShutDownCallback(data->{
			Platform.runLater(()->{
				PopUps.alert("Server shut down, exiting application");
				Platform.exit();
				System.exit(0);
			});
		});
	}
	
	class NewGameHandler implements EventHandler<Event>{
		Difficulty difficulty;

		public NewGameHandler(Difficulty d) {
			difficulty = d;
		}

		@Override
		public void handle(Event event) {
			// TODO Auto-generated method stub
			clientConnection.localGInfo.setMessageID(GameInfo.MessageType.NEW_GAME);
			clientConnection.localGInfo.setDiff(difficulty);
			Platform.runLater(()->{		
				try {
					Stage stage = (Stage) root.getScene().getWindow();
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/GameScene.fxml"));
					Parent newRoot = loader.load();		
					GameController controller = loader.getController();
					controller.setClientConnection(clientConnection);
					clientConnection.send();
					stage.setScene(new Scene(newRoot));
				} catch(Exception e) {
					PopUps.alert("Could not load main scene.");
					e.printStackTrace();
				}
			});
		}
		
	}
	
}
