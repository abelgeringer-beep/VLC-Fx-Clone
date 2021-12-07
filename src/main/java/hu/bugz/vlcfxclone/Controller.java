package hu.bugz.vlcfxclone;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
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

    private float speed;

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

        mediaPlayer.setOnReady(() -> timeSlider.setMax(media.getDuration().toSeconds()));

        mediaView.setMediaPlayer(mediaPlayer);

        volumeSlider.setValue(mediaPlayer.getVolume() * 100);
        volumeSlider.valueProperty().addListener(observable -> mediaPlayer.setVolume(volumeSlider.getValue() / 100));

        mediaPlayer.currentTimeProperty().addListener((observableValue, duration, t1) -> {
            timeSlider.setValue(t1.toSeconds());
            timeLabel.setText(PlaylistController.PlayListData.format(t1) + "/"
                    + PlaylistController.PlayListData.format(media.getDuration()));
        });

        timeSlider.setOnMousePressed(mouseEvent -> mediaPlayer.seek(Duration.seconds(timeSlider.getValue())));

        timeSlider.setOnMouseDragged(mouseEvent -> mediaPlayer.seek(Duration.seconds(timeSlider.getValue())));

        playBtn.setText("Pause");
        mediaPlayer.play();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DoubleProperty mediaWidth = mediaView.fitWidthProperty();
        DoubleProperty mediaHeight = mediaView.fitHeightProperty();
        mediaWidth.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
        mediaHeight.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height").subtract(113));
        speed = 1;

        playList = new PlayList();

        mediaView.setOnDragOver(dragEvent -> {
            Dragboard db = dragEvent.getDragboard();
            if (db.hasFiles()) {
                dragEvent.acceptTransferModes(TransferMode.COPY);
            } else {
                dragEvent.consume();
            }
        });

        mediaView.setOnDragDropped(dragEvent -> {
            Dragboard db = dragEvent.getDragboard();
            File file = db.getFiles().get(0);
            logger.debug(file.getAbsolutePath());
            shouldDispose(file, false);
        });

    }

    private void shouldDispose(File file, boolean remove){
        if (playList.size() == 0) {
            playList.add(new Media(file.toURI().toString()));
            initMediaPlayer(playList.get(0), false);
        } else {
            if(remove)
                playList.remove(0);
            playList.add(new Media(file.toURI().toString()));
            initMediaPlayer(playList.get(0), true);
        }
    }

    @FXML
    public void openFile() {
        try {
            File file = getFile();
            logger.debug(file.toURI().toString());

            shouldDispose(file, true);

        } catch (NullPointerException e) {
            logger.error(e.getMessage());
        }
    }

    @FXML
    private void stopVideo() {
        if (mediaPlayer == null) return;
        playBtn.setText("Play");
        mediaPlayer.stop();
    }

    @FXML
    private void playVideo() {
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
    private void nextMedia() {
        if (playList.size() > 0) {
            playList.nextTrack();
            initMediaPlayer(playList.get(playList.getTrackNumber()), true);
        }
    }

    @FXML
    private void prevMedia() {
        if (playList.size() > 0) {
            playList.prevTrack();
            initMediaPlayer(playList.get(playList.getTrackNumber()), true);
        }
    }

    @FXML
    private void fastVideo() {
        try{
            if(speed < 2)
                speed += 0.25;
            mediaPlayer.setRate(speed);
        } catch (NullPointerException e) {
            System.out.print(e.getMessage() + "\n");
        }
    }

    @FXML
    private void slowVideo() {
        try {
            if(speed >= 0.25)
                speed -= 0.25;
            mediaPlayer.setRate(speed);
        } catch (NullPointerException e) {
            System.out.print(e.getMessage() + "\n");
        }
    }

    @FXML
    private void closeButton() {
        logger.debug("Closing application...");
        System.exit(0);
    }

    @FXML
    public void addToPlaylist() {
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
    public void openPlaylist() {
        Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Playlist-view.fxml"));
            root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.setTitle("Playlist");
            stage.setScene(new Scene(root, 700, 500));

            stage.show();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}