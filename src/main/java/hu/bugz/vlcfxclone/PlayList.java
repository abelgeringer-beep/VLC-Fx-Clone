package hu.bugz.vlcfxclone;

import javafx.scene.media.Media;

import java.util.ArrayList;

public class PlayList extends ArrayList<Media> {
    private int trackNumber;

    public int getTrackNumber() {
        return trackNumber;
    }

    public void nextTrack() {
        if (trackNumber + 1 < this.size())
            trackNumber++;
        else
            trackNumber = 0;
    }

    public void prevTrack(){
        if (trackNumber -1  > -1)
            trackNumber--;
        else
            trackNumber = this.size()-1;
    }
}
