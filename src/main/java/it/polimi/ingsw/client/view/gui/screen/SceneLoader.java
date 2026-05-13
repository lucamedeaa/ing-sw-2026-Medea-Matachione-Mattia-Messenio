package it.polimi.ingsw.client.view.gui.screen;

import it.polimi.ingsw.client.view.gui.SceneId;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Function;

public class SceneLoader {

    private final Stage stage;
    private final Function<Class<?>, Object> controllerFactory;

    public SceneLoader(Stage stage, Function<Class<?>, Object> controllerFactory) {
        this.stage = stage;
        this.controllerFactory = controllerFactory;
    }

    public Object load(SceneId sceneId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(sceneId.path()));
            loader.setControllerFactory(controllerFactory::apply);
            Parent root = loader.load();

            // Definisco se la schermata deve avere proporzioni fisse (InGame) o stretch (Lobby/Matchmaking)
            boolean isFixedRatio = (sceneId == SceneId.IN_GAME || sceneId == SceneId.GAME_ENDED);

            if (root instanceof Region region) {
                region.setPrefSize(1920, 1080);
                region.setMinSize(1920, 1080);
                region.setMaxSize(1920, 1080);
            }

            Group scaleGroup = new Group(root);
            StackPane wrapper = new StackPane(scaleGroup);
            wrapper.setStyle("-fx-background-color: black;"); // Sfondo per le bande nere

            double width = stage.getWidth() > 0 ? stage.getWidth() : 1280;
            double height = stage.getHeight() > 0 ? stage.getHeight() : 720;
            Scene scene = new Scene(wrapper, width, height);

            Scale scale = new Scale(1, 1, 0, 0);
            root.getTransforms().add(scale);

            ChangeListener<Number> resizeListener = (obs, oldV, newV) -> {
                double scaleX = scene.getWidth() / 1920.0;
                double scaleY = scene.getHeight() / 1080.0;

                if (isFixedRatio) {
                    // Proporzioni fisse
                    double factor = Math.min(scaleX, scaleY);
                    scale.setX(factor);
                    scale.setY(factor);
                } else {
                    //Adattamento Full Screen (stretch)
                    scale.setX(scaleX);
                    scale.setY(scaleY);
                }
            };

            scene.widthProperty().addListener(resizeListener);
            scene.heightProperty().addListener(resizeListener);

            Platform.runLater(() -> resizeListener.changed(null, null, null));

            stage.setScene(scene);
            return loader.getController();

        } catch (IOException e) {
            throw new RuntimeException("Unable to load scene: " + sceneId.path(), e);
        }
    }
}