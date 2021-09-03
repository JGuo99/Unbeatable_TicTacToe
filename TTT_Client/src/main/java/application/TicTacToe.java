package application;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class TicTacToe extends Application {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
            // Read file fxml and draw interface.
            Parent root = FXMLLoader.load(getClass()
                    .getResource("/FXML/ConnectScene.fxml"));
 
            primaryStage.setTitle("Tic-Tac-Toe");
            Scene s1 = new Scene(root);
            primaryStage.setScene(s1);
			primaryStage.show();
			primaryStage.setResizable(false);
         
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
	 
	 primaryStage.setOnCloseRequest(e->{
			Platform.exit();
			System.exit(0);
		});

	}

}
