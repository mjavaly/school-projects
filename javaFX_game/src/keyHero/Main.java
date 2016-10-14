/**
 * Main.java
 * Matt Javaly & Elliot Mawby, 6 June 2016
 *
 * The main program for our KeyHero game, a Guitar Hero-style game in JavaFX.
 *
 * Submitted as our final project in CS 257.
 */

package keyHero;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;

public class Main extends Application {
    /**
     * Starts application and loads FXML.
     * @param primaryStage the stage for the game
     **/
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Since this application will be multi-threaded, we want to make
        // sure to terminate all the threads when the user closes the single window.
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
        FXMLLoader loader = new FXMLLoader(getClass().getResource("KeyHero.fxml"));
        Parent root = (Parent)loader.load();
        Controller controller = loader.getController();
        // Set up a KeyEvent handler so we can respond to keyboard activity.
        root.setOnKeyPressed(controller);
        primaryStage.setTitle("Key Hero");
        primaryStage.setScene(new Scene(root, 990, 775));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
