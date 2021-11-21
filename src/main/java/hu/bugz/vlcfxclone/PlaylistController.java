package hu.bugz.vlcfxclone;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.media.Media;
import javafx.util.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PlaylistController implements Initializable {
    public static class PlayListData {
        private String trackNumber;
        private String trackTitle;
        private String trackTime;

        public PlayListData(String trackNumber, String trackTitle, String trackTime) {
            this.trackNumber = trackNumber;
            this.trackTitle = trackTitle;
            this.trackTime = trackTime;
        }

        public PlayListData(int i, Media media) {
            this.trackNumber = String.valueOf(i);
            this.trackTitle = media.getSource();
            this.trackTime = format(media.getDuration());
        }

        private String format(Duration d) {
            int hours = (int) d.toHours();
            int minutes = (int) d.toMinutes() % 60;
            int seconds = (int) d.toSeconds() % 60;

            return (hours == 0 ? "" : hours + ":") +
                    (minutes == 0 ? "" : minutes + ":") +
                    (seconds == 0 ? "" : seconds);
        }

        public String getTrackNumber() {
            return trackNumber;
        }

        public void setTrackNumber(String trackNumber) {
            this.trackNumber = trackNumber;
        }

        public String getTrackTitle() {
            return trackTitle;
        }

        public void setTrackTitle(String trackTitle) {
            this.trackTitle = trackTitle;
        }

        public String getTrackTime() {
            return trackTime;
        }

        public void setTrackTime(String trackTime) {
            this.trackTime = trackTime;
        }
    }

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static class PlayListDataArray extends ArrayList<PlayListData> {

        public PlayListDataArray(PlayList playList) {
            for (int i = 0; i < playList.size(); i++) {
                this.add(new PlayListData(i + 1, playList.get(i)));
            }
        }
    }

    private PlayListDataArray observablePlayListData = new PlayListDataArray(Controller.playList);

    private ObservableList<PlayListData> list = FXCollections.observableArrayList(
            observablePlayListData
    );

    @FXML
    private TableView<PlayListData> playlistTable;

    @FXML
    private TableColumn<PlayListData, String> trackNumber;

    @FXML
    private TableColumn<PlayListData, String> trackTime;

    @FXML
    private TableColumn<PlayListData, String> trackTitle;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.debug(Controller.playList.size());

        trackNumber.setCellValueFactory(new PropertyValueFactory<PlayListData, String>("trackNumber"));
        trackTime.setCellValueFactory(new PropertyValueFactory<PlayListData, String>("trackTime"));
        trackTitle.setCellValueFactory(new PropertyValueFactory<PlayListData, String>("trackTitle"));

        playlistTable.setItems(list);
    }
}
