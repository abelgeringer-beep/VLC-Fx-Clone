package hu.bugz.vlcfxclone;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
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
import javafx.scene.media.MediaMarkerEvent;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private static final Logger logger = LogManager.getLogger(Main.class);

    private MediaPlayer mediaPlayer;
    public static PlayList playList;
    ArrayList<Subtitle> subtitles = new ArrayList<>();

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
    @FXML
    private Label sub_label;

    private File getFile() throws NullPointerException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Select a File..", "*.mp4", "*.srt");
        fileChooser.getExtensionFilters().add(filter);

        return fileChooser.showOpenDialog(null);
    }

    private ArrayList<Subtitle> loadSubtitles(File file){
        ArrayList<Subtitle> subtitles = new ArrayList<>();
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
            String line = input.readLine();
            while(line != null){
                if(!line.equals("")){
                    line = line.replace("\uFEFF", "");// utf-8 bom because utf-8-bom
                    int id = Integer.parseInt(line);
                    line = input.readLine();
                    String[] times = line.split(" --> ");
                    String[] endTimes = times[1].split(":");
                    String[] startTimes = times[0].split(":");
                    int hour = Integer.parseInt(startTimes[0]);
                    int minute = Integer.parseInt(startTimes[1]);
                    double second = Double.parseDouble(startTimes[2].split(",")[0])
                            + Math.pow(10, -3) *
                            Double.parseDouble(startTimes[2].split(",")[1]);
                    int endHour = Integer.parseInt(endTimes[0]);
                    int endMinute = Integer.parseInt(endTimes[1]);
                    double endSecond = Double.parseDouble(endTimes[2].split(",")[0])
                            + Math.pow(10, -3) *
                            Double.parseDouble(endTimes[2].split(",")[1]);
                    String sub = "";

                    line = input.readLine();
                    while(line != null && !line.equals("")){
                        sub += " " + line;
                        logger.debug(line);

                        line = input.readLine();
                    }

//                    do{
//                        line = input.readLine();
//                        if(!line.isBlank()){
//                            sub += " " + line;
//                            logger.debug(line);
//                        }
//                        line = input.readLine();
//                    }while(!line.isEmpty());

                    Subtitle s = new Subtitle(id, hour, minute, second, endHour, endMinute, endSecond, sub);
                    subtitles.add(s);

//                    logger.debug(s.getId() + " " + s.getStartHour() + " " + s.getStartMinute() + " " + s.getStartSecond()+ " " +
//                            s.getEndHour() + " " + s.getEndMinute() + " " + s.getEndSecond() + " " + s.getSubtitle() + "\n");
                    line = input.readLine();

                }
            }
        } catch (IOException e) {
            logger.error(e.getStackTrace());
        }
        return subtitles;

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
        //mediaView.setViewOrder();
        //sub_label.setText("text");
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
    public void openSubtitles() {
        File sub = null;
        try {
            sub = getFile();
        } catch (NullPointerException e) {
            logger.error(e.getMessage());
        }
        logger.debug(sub.toURI().toString());
        subtitles = loadSubtitles(sub);
        //shouldDispose(sub, true);

        final ObservableMap<String, Duration> markers = mediaPlayer.getMedia().getMarkers();

        for (Subtitle caption: subtitles) {
            markers.put(caption.getSubtitle(), Duration.millis(caption.getStartMillis()));
//            logger.debug(markers.get(caption.getSubtitle()));
//            logger.debug(caption.getStartMillis());
        }

        for(String key: markers.keySet()){
            logger.debug(key + ", " + markers.get(key));
        }

        mediaPlayer.setOnMarker(new EventHandler<MediaMarkerEvent>() {
            @Override
            public void handle(MediaMarkerEvent mediaMarkerEvent) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        String[] text = mediaMarkerEvent.getMarker().getKey().split(",,");
                        sub_label.setText(text[1]);
                    }
                });
            }
        });


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
            mediaPlayer.setRate(2);
        } catch (NullPointerException e) {
            System.out.print(e.getMessage() + "\n");
        }
    }

    @FXML
    private void resetSpeed() {
        try {
            mediaPlayer.setRate(1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void slowVideo() {
        try {
            mediaPlayer.setRate(0.5);
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