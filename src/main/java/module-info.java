module hu.bugz.vlcfxclone {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;


    opens hu.bugz.vlcfxclone to javafx.fxml;
    exports hu.bugz.vlcfxclone;
}