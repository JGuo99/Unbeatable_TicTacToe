package application;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import ClientLogic.Client;
import application.GameInfo.MessageType;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.SplitPane.Divider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class GameController implements Initializable {
	public ArrayList<ImageView> images;
	public Client clientConnection;
	private ImageEnabler enabler;

	@FXML
	private Node root;

	@FXML
	private GridPane board_GridPane;

	@FXML
	private SplitPane divider_SplitPane;

	@FXML
	private ImageView cell00_ImageView;

	@FXML
	private ImageView cell01_ImageView;

	@FXML
	private ImageView cell02_ImageView;

	@FXML
	private ImageView cell10_ImageView;

	@FXML
	private ImageView cell11_ImageView;

	@FXML
	private ImageView cell12_ImageView;

	@FXML
	private ImageView cell20_ImageView;

	@FXML
	private ImageView cell21_ImageView;

	@FXML
	private ImageView cell22_ImageView;

	@FXML
	private Label matchResult;

	@FXML
	private Button lobby_Button;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		divider_SplitPane.setOnMouseDragEntered(null);

		images = new ArrayList<ImageView>();
		enabler = new ImageEnabler();

		for (Node component : board_GridPane.getChildren()) {

			if (component instanceof ImageView) {
				ImageView tempImageView = (ImageView) component;
				images.add(tempImageView);

				tempImageView.setOnMouseEntered(e -> {
					ImageView imageV = (ImageView) e.getSource();
					imageV.setImage(new Image("/Images/O_greyedOut.png"));
				});

				tempImageView.setOnMouseExited(e -> {
					ImageView imageV = (ImageView) e.getSource();
					imageV.setImage(null);
				});

				tempImageView.addEventHandler(MouseEvent.MOUSE_CLICKED, new CellClickHandler());
			}
		}

		lobby_Button.setOnAction(event -> {
			try {
				Stage stage = (Stage) root.getScene().getWindow();
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/LobbyScene.fxml"));
				Parent newRoot = loader.load();
				LobbyController controller = loader.getController();
				controller.setClientConnection(clientConnection);
				stage.setScene(new Scene(newRoot));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		lobby_Button.setVisible(false);

		Divider d = divider_SplitPane.getDividers().get(0);
		double pos = d.getPosition();
		d.positionProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				d.setPosition(pos);
			}
		});
		
	}

	public void setClientConnection(Client sourceClient) {
		clientConnection = sourceClient;
		
		clientConnection.setGameCallback(data ->{
			Platform.runLater(()->{
				int aiMoveIndex = clientConnection.localGInfo.aiMove;
				
				if(aiMoveIndex != -1 && clientConnection.localGInfo.messageID != MessageType.NEW_GAME) {
					enabler.disable(aiMoveIndex);
					images.get(aiMoveIndex).setImage(new Image("/Images/X.png"));
					
				}
				
				
				if(clientConnection.localGInfo.result == null) {
					for(int i = 0; i < images.size(); i++) {
						if(enabler.activeList[i]) {
							images.get(i).setDisable(false);
						} 
					}
				} else {
					matchResult.setText(clientConnection.localGInfo.result.toString());
					lobby_Button.setVisible(true);
				}				
			});
		});
	}
	
	
	//Handle clicking on an enabled tile
	class CellClickHandler implements EventHandler<Event>{

		@Override
		public void handle(Event event) {
			ImageView imageV = (ImageView) event.getSource();
			imageV.setImage(new Image("/Images/O.png"));
			imageV.setOnMouseEntered(null);
			imageV.setOnMouseExited(null);
			enabler.disable(images.indexOf(imageV));
			
			clientConnection.localGInfo.messageID = MessageType.PLAY;
			clientConnection.localGInfo.board[images.indexOf(imageV)] = "O";
			clientConnection.send();
			
			for(ImageView i : images) {	
				i.setDisable(true);
			}
		}
		
	}
	
	class ImageEnabler{
		boolean[] activeList;
		int boardSize;
		
		ImageEnabler(){
			boardSize = 9;
			activeList = new boolean[boardSize];
			for(int i = 0; i < boardSize; i++) {
				activeList[i] = true;
			}
		}
		
		void disable(int i) {
			activeList[i] = false;
		}
	}
	
	
}
