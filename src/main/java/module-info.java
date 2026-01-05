module anagnostou.musicplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.graphics;
    requires javafx.base;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires javafx.media;
    requires mp3agic;
	requires discord.rpc;

    opens anagnostou.musicplayer to javafx.fxml, javafx.graphics;
    exports anagnostou.musicplayer;
}