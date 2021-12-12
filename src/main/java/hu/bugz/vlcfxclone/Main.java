package hu.bugz.vlcfxclone;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class Main extends Application {


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Controller-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("VLC-Fx-Clone");

        scene.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getClickCount() == 2){
                stage.setFullScreen(!stage.isFullScreen());
            }
        });

        stage.setMinWidth(600.0);
        stage.setMinHeight(64 + 25 + 24);

        stage.setScene(scene);
        stage.show();


    }

    public static void main(String[] args) {
        launch();
    }
}
