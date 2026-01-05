package anagnostou.musicplayer;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.media.EqualizerBand;


public class SettingsController {
    @FXML
    private CheckBox autoPlayCheckBox;

    public CheckBox getDiscordCheckBox() {
        return discordCheckBox;
    }

    public void setDiscordCheckBox(CheckBox discordCheckBox) {
        this.discordCheckBox = discordCheckBox;
    }

    @FXML
    private CheckBox discordCheckBox;

    @FXML
    private Slider bassSlider, lowMidSlider, midSlider, trebleSlider, highTrebleSlider;


    private EqualizerBand bassBand, lowMidBand, midBand, trebleBand, highTrebleBand;


    public void setEqualizerBands(EqualizerBand bass, EqualizerBand lowMid, EqualizerBand mid, EqualizerBand treble, EqualizerBand highTreble) {
        if (bass != null && lowMid != null && mid != null && treble != null && highTreble != null) {
            this.bassBand = bass;
            this.lowMidBand = lowMid;
            this.midBand = mid;
            this.trebleBand = treble;
            this.highTrebleBand = highTreble;

            bassSlider.valueProperty().addListener((observable, oldValue, newValue) -> bassBand.setGain(newValue.doubleValue()));
            lowMidSlider.valueProperty().addListener((observable, oldValue, newValue) -> lowMidBand.setGain(newValue.doubleValue()));
            midSlider.valueProperty().addListener((observable, oldValue, newValue) -> midBand.setGain(newValue.doubleValue()));
            trebleSlider.valueProperty().addListener((observable, oldValue, newValue) -> trebleBand.setGain(newValue.doubleValue()));
            highTrebleSlider.valueProperty().addListener((observable, oldValue, newValue) -> highTrebleBand.setGain(newValue.doubleValue()));

            bassSlider.setValue(bassBand.getGain());
            lowMidSlider.setValue(lowMidBand.getGain());
            midSlider.setValue(midBand.getGain());
            trebleSlider.setValue(trebleBand.getGain());
            highTrebleSlider.setValue(highTrebleBand.getGain());
        } else{
            System.err.println("null bands");
        }
    }
    public void equalizerRefresh(){

        bassSlider.setValue(0);
        lowMidSlider.setValue(0);
        midSlider.setValue(0);
        trebleSlider.setValue(0);
        highTrebleSlider.setValue(0);
    }


    public boolean isDiscordEnabled() {
        return discordCheckBox.isSelected();
    }
    public boolean isAutoplayEnabled() {
        return autoPlayCheckBox.isSelected();
    }
}