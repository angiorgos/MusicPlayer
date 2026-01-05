package anagnostou.musicplayer;

import com.mpatric.mp3agic.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioEqualizer;
import javafx.scene.media.EqualizerBand;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;



public class HelloController implements Initializable {
    @FXML
    private Pane pane, box;
    @FXML
    private Label songLabel, timePassed, timeMax, metadataText;
    @FXML
    private ImageView playButton, nextButton, previousButton, songPicture, musicDirectory, repeatIcon, soundIcon;
    @FXML
    private Slider volumeSlider, progressSlider;
    @FXML
    private ProgressBar songProgressBar;
    @FXML
    private TextField searchPrompt;


    private Media media;
    private MediaPlayer mediaPlayer;


    private Timer timer;
    private TimerTask task;
    private boolean running = false;


    private File directory;
    private File[] files;
    private ArrayList<File> songs;
    private int songNumber;

    private boolean repeat = false;

    private SettingsController settingsController;

    private AudioEqualizer equalizer;
    private EqualizerBand bassBand, lowMidBand, midBand, trebleBand, highTrebleBand;

    int totalSecondsMax;

    DiscordRichPresence rich;
    DiscordEventHandlers handlers;
    


    public HelloController() throws IOException {
    }




    public void initialize(URL arg0, ResourceBundle arg1) {

        songs = new ArrayList<File>();
        String musicFolderPath = Paths.get(System.getProperty("user.home"), "Music").toString();
        directory = new File(musicFolderPath);
        files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".mp3")) { //για να βάλετε μόνο αρχεία mp3 και όχι οποιουδήποτε τύπου αρχείο
                    songs.add(file);
                }
            }
        }
        try {
            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
        } catch (Exception e) {
            songLabel.setText("Ο συγκεκριμένος φάκελος δεν περιέχει τραγούδια! Διαλέξτε άλλον πατώντας τον φάκελο κάτω αριστερά!");
        }


        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {

                mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);

                if (volumeSlider.getValue() == 0) {
                    volumeSlider.getValue();
                    soundIcon.setImage(new Image(getClass().getResourceAsStream("soundMute.png")));

                } else if (volumeSlider.getValue() <= 50) {
                    soundIcon.setImage(new Image(getClass().getResourceAsStream("soundMiddle.png")));

                } else if (volumeSlider.getValue() <= 100) {
                    soundIcon.setImage(new Image(getClass().getResourceAsStream("sound.png")));

                }
            }

        });


        progressSlider.setMin(0);
        progressSlider.setMax(1);
        progressSlider.setValue(0);
        progressSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {


                mediaPlayer.seek(javafx.util.Duration.seconds(progressSlider.getValue() * totalSecondsMax));

            }

        });

        Tooltip tooltip1 = new Tooltip("Αναπαραγωγή");
        Tooltip tooltip2 = new Tooltip("Επόμενο");
        Tooltip tooltip3 = new Tooltip("Προηγούμενο");
        Tooltip tooltip4 = new Tooltip("Ένταση");
        Tooltip tooltip5 = new Tooltip("Φάκελος τραγουδιών");
        Tooltip tooltip6 = new Tooltip("Επανάληψη");
        Tooltip.install(playButton, tooltip1);
        Tooltip.install(nextButton, tooltip2);
        Tooltip.install(previousButton, tooltip3);
        Tooltip.install(volumeSlider, tooltip4);
        Tooltip.install(musicDirectory, tooltip5);
        Tooltip.install(repeatIcon, tooltip6);

        equalize();
        loadEqualizerSettings();


    }

    private Map<String, Double> equalizerSettings = new HashMap<>();

    private void saveEqualizerSettings() {
        if (equalizer != null) {
            equalizerSettings.put("bassGain", bassBand.getGain());
            equalizerSettings.put("lowMidGain", lowMidBand.getGain());
            equalizerSettings.put("midGain", midBand.getGain());
            equalizerSettings.put("trebleGain", trebleBand.getGain());
            equalizerSettings.put("highTrebleGain", highTrebleBand.getGain());
        }
    }


    private void loadEqualizerSettings() {
        if (equalizer != null && !equalizerSettings.isEmpty()) {
            bassBand.setGain(equalizerSettings.getOrDefault("bassGain", 0.0));
            lowMidBand.setGain(equalizerSettings.getOrDefault("lowMidGain", 0.0));
            midBand.setGain(equalizerSettings.getOrDefault("midGain", 0.0));
            trebleBand.setGain(equalizerSettings.getOrDefault("trebleGain", 0.0));
            highTrebleBand.setGain(equalizerSettings.getOrDefault("highTrebleGain", 0.0));

            if (settingsController != null) {
                Platform.runLater(() -> settingsController.setEqualizerBands(
                        bassBand, lowMidBand, midBand, trebleBand, highTrebleBand
                ));
            }
        }
    }

    public void enableDiscord(){
        handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
            System.out.println("Welcome " + user.username + "#" + user.discriminator + "!");
        }).build();
        DiscordRPC.discordInitialize("1311083972851273808", handlers, true);
        rich = new DiscordRichPresence.Builder("").setDetails("Idle").build();
        DiscordRPC.discordUpdatePresence(rich);

    }
    public void disableDiscord(){
        DiscordRPC.discordClearPresence();
        DiscordRPC.discordShutdown();
    }


    public void equalize() {
        if(mediaPlayer!=null){
            equalizer = mediaPlayer.getAudioEqualizer();
            equalizer.setEnabled(true);

            bassBand = new EqualizerBand(60, 80, 0);       // Μπάσο: Χαμηλές συχνότητες (20–250 Hz)
            lowMidBand = new EqualizerBand(250, 250, 0);  // Χαμηλό μεσαίο (250–500 Hz)
            midBand = new EqualizerBand(1000, 500, 0);    // Μεσαίο (500–2,000 Hz)
            trebleBand = new EqualizerBand(4000, 2000, 0); // Υψηλό (2,000–6,000 Hz)
            highTrebleBand = new EqualizerBand(8000, 4000, 0); // Πολύ Υψηλό (6,000–16,000 Hz)

            ObservableList<EqualizerBand> bands = equalizer.getBands();
            bands.clear(); // Clear default bands
            bands.add(bassBand);
            bands.add(lowMidBand);
            bands.add(midBand);
            bands.add(trebleBand);
            bands.add(highTrebleBand);
        }


    }

    public void directorySelector() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Επιλέξτε φάκελο τραγουδιών .MP3");
        File selectedDirectory = directoryChooser.showDialog(new Stage());
        if (selectedDirectory != null) {
            songLabel.setText("");
            metadataText.setText("");
            songPicture.setImage(new Image(HelloApplication.class.getResourceAsStream("default.png")));
            timePassed.setText("");
            timeMax.setText("");
            songProgressBar.setProgress(0);
            songNumber = 0;
            try {
                cancelTimer();
            } catch (Exception e) {
                // Continue χωρίς πρόβλημα
            }
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }

            directory = new File(selectedDirectory.getAbsolutePath());
            files = directory.listFiles();
            songs.clear();


            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".mp3")) {
                        songs.add(file);
                    }
                }
            }

            // Ενημέρωση UI αν δεν υπάρχουν τραγούδια στον επιλεγμένο φάκελο
            if (songs.isEmpty()) {
                songLabel.setText("Ο συγκεκριμένος φάκελος δεν περιέχει τραγούδια! Διαλέξτε άλλον πατώντας τον φάκελο κάτω αριστερά!");
                return;
            }


            try {
                media = new Media(songs.get(songNumber).toURI().toString());
                mediaPlayer = new MediaPlayer(media);
            } catch (Exception e) {
                songLabel.setText("Ο συγκεκριμένος φάκελος δεν περιέχει τραγούδια! Διαλέξτε άλλον πατώντας τον φάκελο κάτω αριστερά!");
            }

            // Ενημέρωση αποτελεσμάτων αναζήτησης μετά την αλλαγή φακέλου
            search();
        } else {
            System.out.println("No directory selected");
        }
    }


    @FXML
    public void play(MouseEvent event) {

        if (running == false) {
            if (media != null) {
                extractMetadata(songs.get(songNumber));
                mediaPlayer.play();
                mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
                beginTimer();
                running = true;
                songLabel.setText(songs.get(songNumber).getName());
                equalize();
                settingsController = fxmlSettingsLoader.getController();
                settingsController.setEqualizerBands(bassBand, lowMidBand, midBand, trebleBand, highTrebleBand);
            }
        } else {
            mediaPlayer.pause();
            cancelTimer();
            running = false;
        }
    }

    @FXML
    public void previous() {
        if (media != null) {
            saveEqualizerSettings();
            if (songNumber > 0) {
                songNumber--;
                mediaPlayer.stop();
                if (running) {
                    cancelTimer();
                }

                media = new Media(songs.get(songNumber).toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                equalize();
                loadEqualizerSettings();
                if (settingsController != null) {
                    settingsController.setEqualizerBands(bassBand, lowMidBand, midBand, trebleBand, highTrebleBand);
                }
                mediaPlayer.play();
                extractMetadata(songs.get(songNumber));
                mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
                beginTimer();
                running = true;
                songLabel.setText(songs.get(songNumber).getName());

            } else {
                songNumber = songs.size() - 1;
                mediaPlayer.stop();
                if (running) {
                    cancelTimer();
                }

                media = new Media(songs.get(songNumber).toURI().toString());
                mediaPlayer = new MediaPlayer(media);

                mediaPlayer.play();
                extractMetadata(songs.get(songNumber));
                mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
                beginTimer();
                running = true;
                songLabel.setText(songs.get(songNumber).getName());
            }
        }
    }

    @FXML
    public void next() {
        if (media != null) {
            saveEqualizerSettings();
            if (songNumber < songs.size() - 1) {
                songNumber++;
                mediaPlayer.stop();
                if (running) {
                    cancelTimer();
                }

                media = new Media(songs.get(songNumber).toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                equalize();
                loadEqualizerSettings();
                if (settingsController != null) {
                    settingsController.setEqualizerBands(bassBand, lowMidBand, midBand, trebleBand, highTrebleBand);
                }
                mediaPlayer.play();
                extractMetadata(songs.get(songNumber));
                mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
                beginTimer();
                running = true;
                songLabel.setText(songs.get(songNumber).getName());

            } else {
                songNumber = 0;
                mediaPlayer.stop();
                if (running) {
                    cancelTimer();
                }

                media = new Media(songs.get(songNumber).toURI().toString());
                mediaPlayer = new MediaPlayer(media);

                mediaPlayer.play();
                extractMetadata(songs.get(songNumber));
                mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
                beginTimer();
                running = true;
                songLabel.setText(songs.get(songNumber).getName());
            }
        }
    }


    @FXML
    public void beginTimer() {

        timer = new Timer();

        task = new TimerTask() {

            @Override
            public void run() {
                running = true;



                Platform.runLater(() -> {
                    double end = media.getDuration().toSeconds();
                    totalSecondsMax = (int) end;

                    // Calculate minutes and seconds
                    int minutesMax = totalSecondsMax / 60;
                    int secondsMax = totalSecondsMax % 60;

                    // Format the output to mm:ss format (ensuring 2 digits for seconds)
                    String formattedTimeMax = String.format("%02d:%02d", minutesMax, secondsMax);
                    timeMax.setText(formattedTimeMax);


                    double current = mediaPlayer.getCurrentTime().toSeconds();

                    int totalSeconds = (int) current; // example value (2 minutes and 5 seconds)

                    int minutes = totalSeconds / 60;
                    int seconds = totalSeconds % 60;
                    String formattedTimePassed = String.format("%02d:%02d", minutes, seconds);


                    timePassed.setText(formattedTimePassed);

                    songProgressBar.setProgress(current / end);


                    if(settingsController.isDiscordEnabled()) {
                        if(mp3File.getId3v2Tag() != null) {
                            rich = new DiscordRichPresence.Builder(formattedTimePassed + " | " + formattedTimeMax).setDetails("Listening to " + mp3File.getId3v2Tag().getTitle()).build();
                        }else if(mp3File.getId3v2Tag() == null && mp3File.getId3v1Tag() != null) {
                            rich = new DiscordRichPresence.Builder(formattedTimePassed + " | " + formattedTimeMax).setDetails("Listening to " + mp3File.getId3v1Tag().getTitle()).build();
                        }else
                            rich = new DiscordRichPresence.Builder(formattedTimePassed + " | " + formattedTimeMax).setDetails("Listening to " + songs.get(songNumber).getName()).build();
                        }
                        DiscordRPC.discordUpdatePresence(rich);


                    if (current / end == 1 && repeat == true) { //otan stamataei to tragoudi na stamatisei o timer ektos an theloume epanalipsi
                        mediaPlayer.seek(javafx.util.Duration.seconds(0));
                        mediaPlayer.play();
                    } else if (current / end == 1 && repeat == false && settingsController!=null) { //An to repeat einai anoixto den litourgei to autoPlay sto epomeno tragoudi alla epanalambanetai to idio
                        if (settingsController.isAutoplayEnabled()) {
                            next();
                        } else {
                            cancelTimer();
                        }
                    }
                });
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    @FXML
    public void cancelTimer() {
        running = false;
        rich = new DiscordRichPresence.Builder("Idle").setDetails("").build();
        DiscordRPC.discordUpdatePresence(rich);
        timer.cancel();
    }


    boolean settingsOpened = false;
    FXMLLoader fxmlSettingsLoader = new FXMLLoader(HelloApplication.class.getResource("settingsPane.fxml"));
    Scene scene = new Scene(fxmlSettingsLoader.load());
    @FXML
    public void settings() throws IOException {
        if (!settingsOpened) {
            Stage settingsStage = new Stage();




            String css = this.getClass().getResource("style.css").toExternalForm();
            scene.getStylesheets().add(css);

            settingsController = fxmlSettingsLoader.getController();
            settingsController.setEqualizerBands(bassBand, lowMidBand, midBand, trebleBand, highTrebleBand);

            settingsStage.setWidth(600);
            settingsStage.setHeight(700);
            settingsStage.setTitle("Anagnostou Music Player - Settings");
            settingsStage.setResizable(false);
            settingsStage.setScene(scene);
            settingsStage.getIcons().add(new Image(HelloApplication.class.getResourceAsStream("play.png")));

            settingsStage.setOnCloseRequest(event -> settingsOpened = false);

            settingsStage.show();

            settingsOpened = true;

            settingsController.getDiscordCheckBox().selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    enableDiscord();
                } else {
                    disableDiscord();
                }
            });
        }
    }



    boolean helpOpened = false;
    @FXML
    public void help() throws IOException {
        if (!helpOpened) {
            Stage helpStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("help.fxml"));
            Scene scene = new Scene(fxmlLoader.load());


            String css = this.getClass().getResource("style.css").toExternalForm();
            scene.getStylesheets().add(css);
            helpStage.getIcons().add(new Image(HelloApplication.class.getResourceAsStream("play.png")));


            HelpController helpController = new HelpController();
            helpController = fxmlLoader.getController();

            helpStage.setWidth(650);
            helpStage.setHeight(930);
            helpStage.setTitle("Anagnostou Music Player - Βοήθεια");
            helpStage.setResizable(false);
            helpStage.setScene(scene);

            helpStage.setOnCloseRequest(event -> helpOpened = false);

            helpStage.show();

            helpOpened = true;
        }
    }


    @FXML
    public void search() {
        if (searchPrompt.getText().isEmpty()) { //gia na kanei unfocus to search otan patas allou.
            pane.requestFocus();
        }
        ObservableList<String> observableSongs = FXCollections.observableArrayList();
        for (File song : songs) {
            observableSongs.add(song.getName());
        }

        ListView<String> resultList = new ListView<>();
        resultList.setPrefHeight(100);
        resultList.setVisible(false);
        box.getChildren().clear();
        box.getChildren().add(resultList);

        resultList.prefWidthProperty().bind(searchPrompt.widthProperty());



        resultList.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); "
                + "-fx-control-inner-background: rgba(0, 0, 0, 0.7); "
                + "-fx-background-insets: 0; "
                + "-fx-background-radius: 5; "
                + "-fx-border-width: 0; "
                + "-fx-text-fill: #b3b3b3; "
                + "-fx-font-size: 14;");


        searchPrompt.setOnMouseClicked(event -> {
            if (!Objects.equals(searchPrompt.getText(), "")) {
                resultList.setVisible(true);
            }
        });



        pane.setOnMouseClicked(event -> {
            if (event.getSource() != searchPrompt) {
                resultList.setVisible(false);
            }
        });


        searchPrompt.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                resultList.setItems(FXCollections.observableArrayList());
                resultList.setVisible(false);
            } else {
                ObservableList<String> filteredSongs = observableSongs.filtered(song ->
                        song.toLowerCase().contains(newValue.toLowerCase()));
                ObservableList<String> limitedResults = FXCollections.observableArrayList(
                        filteredSongs.subList(0, Math.min(filteredSongs.size(), 3))
                );
                resultList.setItems(limitedResults);
                resultList.setVisible(!limitedResults.isEmpty());
            }
        });

        resultList.setOnMouseClicked(event -> {
            String selectedSongName = resultList.getSelectionModel().getSelectedItem();
            if (selectedSongName != null) {
                for (int i = 0; i < songs.size(); i++) {
                    if (songs.get(i).getName().equals(selectedSongName)) {
                        songNumber = i;
                        playCurrentSong();
                        break;
                    }
                }
            }
        });
    }

    private void playCurrentSong() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            cancelTimer();
        }

        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        equalize();
        loadEqualizerSettings();
        if (settingsController != null) {
            settingsController.setEqualizerBands(bassBand, lowMidBand, midBand, trebleBand, highTrebleBand);
        }
        mediaPlayer.play();
        extractMetadata(songs.get(songNumber));
        mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
        beginTimer();
        running = true;
        songLabel.setText(songs.get(songNumber).getName());
    }

    Mp3File mp3File;
    private void extractMetadata(File file) {
        try {
            mp3File = new Mp3File(file);
            String metadata = "Δεν βρέθηκαν πληροφορίες για αυτό το τραγούδι";

            if (mp3File.hasId3v1Tag()) {
                ID3v1 id3v1Tag = mp3File.getId3v1Tag();
                metadata = "Title: " + id3v1Tag.getTitle() + "\n" +
                        "Artist: " + id3v1Tag.getArtist() + "\n" +
                        "Album: " + id3v1Tag.getAlbum() + "\n" +
                        "Year: " + id3v1Tag.getYear();
            } else if (mp3File.hasId3v2Tag()) {
                ID3v2 id3v2Tag = mp3File.getId3v2Tag();
                metadata = "Title: " + id3v2Tag.getTitle() + "\n" +
                        "Artist: " + id3v2Tag.getArtist() + "\n" +
                        "Album: " + id3v2Tag.getAlbum() + "\n" +
                        "Year: " + id3v2Tag.getYear();


                byte[] albumArt = id3v2Tag.getAlbumImage();

                if (albumArt != null) {
                    Image image = new Image(new ByteArrayInputStream(albumArt));
                    songPicture.setImage(image);
                } else {
                    setDefaultImage();
                }
            } else {
                setDefaultImage();
            }

            metadataText.setText(metadata);
        } catch (UnsupportedTagException | InvalidDataException | IOException e) {
            metadataText.setText("Error reading MP3 metadata: " + e.getMessage());
            setDefaultImage();
        }
    }

    private void setDefaultImage() {
        try {
            songPicture.setImage(new Image(getClass().getResourceAsStream("default.png")));
        } catch (NullPointerException e) {
            System.err.println("Default image not found!");
        }
    }

    public void repeater() {
        if (repeat) {
            repeat = false;
            Image repeatOff = new Image(getClass().getResourceAsStream("repeatOff.png"));
            repeatIcon.setImage(repeatOff);
        } else {
            repeat = true;
            Image repeatOn = new Image(getClass().getResourceAsStream("repeatOn.png"));
            repeatIcon.setImage(repeatOn);
        }
    }


}