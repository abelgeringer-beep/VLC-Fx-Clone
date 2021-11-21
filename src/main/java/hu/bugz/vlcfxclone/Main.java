package hu.bugz.vlcfxclone;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Main extends Application {

    private static final Logger logger = LogManager.getLogger(Main.class);

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Controller-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("VLC-Fx-Clone");

        stage.setMinWidth(600.0);
        stage.setMinHeight(64 + 25 + 24);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}