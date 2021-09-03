package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import ServerLogic.Server;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class MainController implements Initializable {
	HashMap<Integer, String[]> matches;
	HashMap<String, Integer>  matchFinder;
	
	@FXML
	private Node root;
	
	@FXML
	private Label serverPortNumber_Label;
	
	@FXML
	private ListView server_listView;
	
	@FXML
	private ComboBox<String> matchSelector_ComboBox;
	
	
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
    private GridPane board_GridPane;
	
	@FXML
	void setMatchInstance(ActionEvent event) {
	
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		matches = new HashMap<Integer, String[]>();
		matchFinder = new HashMap<String, Integer>();
		
		matchSelector_ComboBox.setOnAction(event->{
			if(matchSelector_ComboBox.getValue() != null) {
//				System.out.println(matchSelector_ComboBox.getValue());
//				System.out.println(matchFinder.get(matchSelector_ComboBox.getValue()));
//				System.out.println(matches.get(matchFinder.get(matchSelector_ComboBox.getValue())));
				setImages(matches.get(matchFinder.get(matchSelector_ComboBox.getValue())));
				
			}
		});			
	}
	
	public void setServerConnection(Server server) {
		server.setListCallback(data->{
			Platform.runLater(() -> {
				server_listView.getItems().add(data);
				server_listView.scrollTo(server_listView.getItems().size()-1);
			});
		});

		server.setMatchVCallback(data -> { //data should be matches hash map
			Platform.runLater(()->{
				matches.clear();
				HashMap<Integer, String[]>tempMatches = (HashMap<Integer, String[]>) data;
				matches = (HashMap<Integer, String[]>) tempMatches.clone();
				
				String currentSelection = "";
				if(matchSelector_ComboBox.getValue() != null)
					currentSelection = matchSelector_ComboBox.getValue();
				
				matchFinder.clear();
				matchSelector_ComboBox.getItems().clear();

				String matchName;
				for(Integer key : matches.keySet()) {
					matchName = "Match " + key;
//					System.out.println("loop:" + matches.get(key));
					matchFinder.put(matchName, key);
					matchSelector_ComboBox.getItems().add(matchName);
					
				}
				if(currentSelection == "" || matchFinder.get(currentSelection) == null) {
					for(Node component : board_GridPane.getChildren()) {
						if(component instanceof ImageView) {
							ImageView tempImageView = (ImageView) component;
							tempImageView.setImage(null);
						}
					}
				}
				else {
					setImages(matches.get(matchFinder.get(currentSelection)));
					matchSelector_ComboBox.setValue(currentSelection);
				}
			});
		});
		
		
	}
	
	public void setImages(String[] board) {
		String[] urlBuilder = new String[board.length];

		
		for(int i = 0; i < board.length; i++) {
			if(!board[i].equals("b"))
				urlBuilder[i] = "/Images/" + board[i] + ".png" ;
		}
		
		//reset
		for(Node component : board_GridPane.getChildren()) {
			if(component instanceof ImageView) {
				ImageView tempImageView = (ImageView) component;
				tempImageView.setImage(null);
			}
		}
		
		//set new images
		int i = 0;
		for(Node component : board_GridPane.getChildren()) {
			if(component instanceof ImageView) {
				if(!board[i].equals("b")) {
					ImageView tempImageView = (ImageView) component;
					tempImageView.setImage(new Image(urlBuilder[i]));
				}
				i++;
			}
		}
		
	}
	
}
