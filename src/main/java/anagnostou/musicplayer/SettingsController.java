package anagnostou.musicplayer;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.media.EqualizerBand;


public class SettingsController {

    private ChangeListener<Number> bassListener, lowMidListener, midListener, trebleListener, highTrebleListener;
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

            if (bassListener != null) {
                bassSlider.valueProperty().removeListener(bassListener);
                lowMidSlider.valueProperty().removeListener(lowMidListener);
                midSlider.valueProperty().removeListener(midListener);
                trebleSlider.valueProperty().removeListener(trebleListener);
                highTrebleSlider.valueProperty().removeListener(highTrebleListener);
            }

            bassListener = (observable, oldValue, newValue) -> bassBand.setGain(newValue.doubleValue());
            lowMidListener = (observable, oldValue, newValue) -> lowMidBand.setGain(newValue.doubleValue());
            midListener = (observable, oldValue, newValue) -> midBand.setGain(newValue.doubleValue());
            trebleListener = (observable, oldValue, newValue) -> trebleBand.setGain(newValue.doubleValue());
            highTrebleListener = (observable, oldValue, newValue) -> highTrebleBand.setGain(newValue.doubleValue());

            bassSlider.valueProperty().addListener(bassListener);
            lowMidSlider.valueProperty().addListener(lowMidListener);
            midSlider.valueProperty().addListener(midListener);
            trebleSlider.valueProperty().addListener(trebleListener);
            highTrebleSlider.valueProperty().addListener(highTrebleListener);

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