package application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

//Contains static methods to display alerts depending on the case
public class PopUps {
	//Display warning for the user when they try to start a new game
	public static void alertWithOption(String message, String yes, String no, Choice choice) {
		Stage stage = new Stage();
		
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("RPSLS");
		stage.setWidth(300);
		stage.setHeight(150);
		
		Label text = new Label(message);
		
		Button cancelButton = new Button(no);
		cancelButton.setMinSize(80, 20);
		cancelButton.setOnAction(e->stage.close());
		
		Button okButton = new Button(yes);
		okButton.setMinSize(80, 20);
		
		okButton.setOnAction(e->{
			choice.setChoice(true);
			stage.close();
		});
		
		cancelButton.setOnAction(e->{
			choice.setChoice(false);
			stage.close();
		});
		
		HBox buttonLayout = new HBox(20,okButton,cancelButton);
		buttonLayout.setAlignment(Pos.BOTTOM_CENTER);
		
		VBox mainLayout = new VBox(20, text, buttonLayout);
		mainLayout.setAlignment(Pos.CENTER);
		
		Scene scene = new Scene(mainLayout);
		
		stage.setScene(scene);
		stage.showAndWait();
	}
	
	//Warning for when user inputs a non accepted value into the betField
	public static void alert(String s) {
		Stage stage = new Stage();
		
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("RPSLS");
		stage.setWidth(300);
		stage.setHeight(150);
		
		
		Label text = new Label(s);
		
		Button ok = new Button("Ok");
		ok.setMinSize(80, 20);
		ok.setOnAction(e->stage.close());
		
		ok.setOnAction(e->{
			stage.close();
		});
		

		HBox buttonLayout = new HBox(20,ok);
		buttonLayout.setAlignment(Pos.BOTTOM_CENTER);
		
		VBox mainLayout = new VBox(20, text, buttonLayout);
		mainLayout.setAlignment(Pos.CENTER);
		
		Scene scene = new Scene(mainLayout);
		
		stage.setScene(scene);
		stage.showAndWait();
	}
	
	//Helper class to send and return a boolean to and from popups
	public class Choice{
		public boolean b;
		
		public void setChoice(boolean b) {
			this.b = b;
		}
		
		public boolean getChoice() {
			return b;
		}
	}

}
