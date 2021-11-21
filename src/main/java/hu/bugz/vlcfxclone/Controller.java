package hu.bugz.vlcfxclone;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private static final Logger logger = LogManager.getLogger(Main.class);

    private MediaPlayer mediaPlayer;
    public static PlayList playList;

    @FXML
    private MediaView mediaView;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Slider timeSlider;
    @FXML
    private Button playBtn;
    @FXML
    private Label timeLabel;

    private File getFile() throws NullPointerException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Select a File (*.mp4)", "*.mp4");
        fileChooser.getExtensionFilters().add(filter);
        return fileChooser.showOpenDialog(null);
    }

    private void initMediaPlayer(Media media, Boolean dispose) {
        if (dispose)
            mediaPlayer.dispose();

        mediaPlayer = new MediaPlayer(media);

        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                timeSlider.setMax(media.getDuration().toSeconds());
            }
        });

        mediaView.setMediaPlayer(mediaPlayer);

        volumeSlider.setValue(mediaPlayer.getVolume() * 100);
        volumeSlider.valueProperty().addListener(observable -> mediaPlayer.setVolume(volumeSlider.getValue() / 100));

        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration duration, Duration t1) {
                timeSlider.setValue(t1.toSeconds());
                timeLabel.setText(PlaylistController.PlayListData.format(t1) + "/" + PlaylistController.PlayListData.format(media.getDuration()));
            }
        });

        timeSlider.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mediaPlayer.seek(Duration.seconds(timeSlider.getValue()));
            }
        });

        timeSlider.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mediaPlayer.seek(Duration.seconds(timeSlider.getValue()));
            }
        });

        playBtn.setText("Pause");
        mediaPlayer.play();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DoubleProperty mediaWidth = mediaView.fitWidthProperty();
        DoubleProperty mediaHeight = mediaView.fitHeightProperty();
        mediaWidth.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
        mediaHeight.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height").subtract(113));

        playList = new PlayList();
    }

    @FXML
    public void openFile(ActionEvent Event) {
        try {
            File file = getFile();
            logger.debug(file.toURI().toString());

            if (playList.size() == 0) {
                playList.add(new Media(file.toURI().toString()));
                initMediaPlayer(playList.get(0), false);
            } else {
                playList.remove(0);
                playList.add(new Media(file.toURI().toString()));
                initMediaPlayer(playList.get(0), true);
            }

        } catch (NullPointerException e) {
            logger.error(e.getMessage());
        }
    }

    @FXML
    private void stopVideo(ActionEvent event) {
        playBtn.setText("Play");
        mediaPlayer.stop();
    }

    @FXML
    private void playVideo(ActionEvent event) {
        if (mediaPlayer == null) return;
        if (mediaPlayer.getStatus().toString().equals("PAUSED") ||
                mediaPlayer.getStatus().toString().equals("STOPPED")) {
            playBtn.setText("Pause");
            mediaPlayer.play();
        } else {
            playBtn.setText("Play");
            mediaPlayer.pause();
        }
        mediaPlayer.setRate(1);
    }

    @FXML
    private void nextMedia(ActionEvent event) {
        if (playList.size() > 0) {
            playList.nextTrack();
            initMediaPlayer(playList.get(playList.getTrackNumber()), true);
        }
    }

    @FXML
    private void prevMedia(ActionEvent event) {
        if (playList.size() > 0) {
            playList.prevTrack();
            initMediaPlayer(playList.get(playList.getTrackNumber()), true);
        }
    }

    @FXML
    private void fastVideo(ActionEvent event) {
        mediaPlayer.setRate(2);
    }

    @FXML
    private void slowVideo(ActionEvent event) {
        mediaPlayer.setRate(0.5);
    }

    @FXML
    private void closeButton(ActionEvent event) {
        logger.debug("Closing application...");
        System.exit(0);
    }

    @FXML
    public void addToPlaylist(ActionEvent actionEvent) {
        try {
            File file = getFile();
            playList.add(new Media(file.toURI().toString()));
            if (playList.size() == 1) {
                initMediaPlayer(playList.get(0), false);
            }

            logger.debug(file.toURI().toString());
        } catch (NullPointerException e) {
            logger.error(e.getMessage());
        }
    }

    @FXML
    public void openPlaylist(ActionEvent actionEvent) {
        Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Playlist-view.fxml"));
            root = fxmlLoader.load();
            //PlaylistController playlistController = fxmlLoader.getController();
            //playlistController.setPlayList(playList);

            Stage stage = new Stage();
            stage.setTitle("Playlist");
            stage.setScene(new Scene(root, 700, 500));

            stage.show();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}