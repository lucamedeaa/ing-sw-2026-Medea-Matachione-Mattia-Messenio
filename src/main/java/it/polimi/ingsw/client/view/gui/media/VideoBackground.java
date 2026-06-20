package it.polimi.ingsw.client.view.gui.media;

import javafx.beans.value.ObservableValue;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.net.URL;

/**
 * Helper that manages a looping video background and shared volume setting.
 */
public class VideoBackground {
    private MediaPlayer mediaPlayer;
    private StackPane currentContainer;

    private static double globalVolume = 0.15;

    /**
     * Returns the last selected global video volume.
     *
     * @return global volume in the range 0..1
     */
    public static double getGlobalVolume() { return globalVolume; }

    /**
     * Starts a looping video inside the target container.
     *
     * @param container container that will host the media view
     * @param resourcePath classpath path to the video resource
     * @param volumeProperty optional property used to update the shared volume
     */
    public void start(StackPane container, String resourcePath, ObservableValue<? extends Number> volumeProperty) {
        stop();
        currentContainer = container;

        URL resource = getClass().getResource(resourcePath);
        if (resource == null) {
            System.err.println("Video non trovato: " + resourcePath);
            return;
        }

        Media media = new Media(resource.toExternalForm());
        media.setOnError(() -> System.err.println("Errore Media: " + media.getError()));

        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setVolume(globalVolume);

        if (volumeProperty != null) {
            // Se lo slider si muove, aggiorna sia il player che la memoria globale
            volumeProperty.addListener((obs, old, newVal) -> {
                globalVolume = newVal.doubleValue();
                if (mediaPlayer != null) mediaPlayer.setVolume(globalVolume);
            });
        }

        mediaPlayer.setOnError(() -> System.err.println("Errore MediaPlayer: " + mediaPlayer.getError()));
        mediaPlayer.setOnReady(mediaPlayer::play);

        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.setPreserveRatio(false);
        mediaView.fitWidthProperty().bind(container.widthProperty());
        mediaView.fitHeightProperty().bind(container.heightProperty());

        container.getChildren().setAll(mediaView);
    }

    /**
     * Stops playback, releases the media player, and clears the current container.
     */
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.volumeProperty().unbind();
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
        if (currentContainer != null) {
            currentContainer.getChildren().clear();
            currentContainer = null;
        }
    }
}
