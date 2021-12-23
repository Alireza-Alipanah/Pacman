package view;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class Music {

    private static Music music;

    private final MediaPlayer BACKGROUND, EAT_GHOST, BEGINNING, DEATH, EXTRA_PAC, INTERMISSION;
    private double volume = 0.5f;

    private Music() {
        BACKGROUND = new MediaPlayer(new Media(new File("src/resources/sounds/background.mp3").toURI().toString()));
        BACKGROUND.setCycleCount(MediaPlayer.INDEFINITE);
        BEGINNING = new MediaPlayer(new Media(new File("src/resources/sounds/beginning.wav").toURI().toString()));
        EAT_GHOST = new MediaPlayer(new Media(new File("src/resources/sounds/eatghost.wav").toURI().toString()));
        DEATH = new MediaPlayer(new Media(new File("src/resources/sounds/death.wav").toURI().toString()));
        EXTRA_PAC = new MediaPlayer(new Media(new File("src/resources/sounds/extrapac.wav").toURI().toString()));
        INTERMISSION = new MediaPlayer(new Media(new File("src/resources/sounds/intermission.wav").toURI().toString()));
        BEGINNING.setOnEndOfMedia(BEGINNING::stop);
        EAT_GHOST.setOnEndOfMedia(EAT_GHOST::stop);
        DEATH.setOnEndOfMedia(DEATH::stop);
        EXTRA_PAC.setOnEndOfMedia(EXTRA_PAC::stop);
        INTERMISSION.setOnEndOfMedia(INTERMISSION::stop);
        changeVolume(0.5);
    }

    public static Music getInstance() {
        if (music == null)
            music = new Music();
        return music;
    }

    public void changeVolume(double amount) {
        BACKGROUND.setVolume(amount);
        EAT_GHOST.setVolume(amount);
        BEGINNING.setVolume(amount);
        DEATH.setVolume(amount);
        EXTRA_PAC.setVolume(amount);
        INTERMISSION.setVolume(amount);
        volume = amount;
    }

    public double getVolume() {
        return volume;
    }

    public void playEatGhost() {
        EAT_GHOST.play();
    }

    public void playBeginning() {
        BEGINNING.play();
    }

    public void playDeath() {
        DEATH.play();
    }

    public void playExtrapac() {
        INTERMISSION.stop();
        BEGINNING.stop();
        DEATH.stop();
        EAT_GHOST.stop();
        EXTRA_PAC.play();
    }

    public void playIntermission() {
        INTERMISSION.play();
    }

    public void playBackground() {
        BACKGROUND.play();
    }

    public void stopBackground() {
        BACKGROUND.stop();
    }

}
