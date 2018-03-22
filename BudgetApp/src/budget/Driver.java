package budget;

import java.io.IOException;

import budget.server.ServerAccess;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Driver extends Application {
	@Override
	public void start(Stage primaryStage) {		
		try {
			Parent root = FXMLLoader.load(getClass().getResource("view/LoginModal.fxml"));
			Scene scene = new Scene(root, 400, 200);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {		
		ServerAccess.testAccess();
		//launch(args);
		System.exit(0);
	}
}
