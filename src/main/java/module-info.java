module hu.bugz.vlcfxclone {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.media;

    opens hu.bugz.vlcfxclone to javafx.fxml;
    exports hu.bugz.vlcfxclone;
}