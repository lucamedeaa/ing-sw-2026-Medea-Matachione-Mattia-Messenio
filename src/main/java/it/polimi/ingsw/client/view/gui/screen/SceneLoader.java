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

            Scene scene;
            double width = stage.getWidth() > 0 ? stage.getWidth() : 1280;
            double height = stage.getHeight() > 0 ? stage.getHeight() : 720;

            // Applica il ridimensionamento forzato (Scale) solo per i menu e la lobby
            if (sceneId == SceneId.MATCHMAKING || sceneId == SceneId.LOBBY || sceneId == SceneId.DISCONNECTED) {
                if (root instanceof Region region) {
                    region.setPrefSize(1920, 1080);
                    region.setMinSize(1920, 1080);
                    region.setMaxSize(1920, 1080);
                }
                Group scaleGroup = new Group(root);
                StackPane wrapper = new StackPane(scaleGroup);
                wrapper.setStyle("-fx-background-color: black;");
                scene = new Scene(wrapper, width, height);

                Scale scale = new Scale(1, 1, 0, 0);
                root.getTransforms().add(scale);

                ChangeListener<Number> resizeListener = (obs, oldV, newV) -> {
                    scale.setX(scene.getWidth() / 1920.0);
                    scale.setY(scene.getHeight() / 1080.0);
                };
                scene.widthProperty().addListener(resizeListener);
                scene.heightProperty().addListener(resizeListener);
                Platform.runLater(() -> resizeListener.changed(null, null, null));

            } else {
                // Per IN_GAME e GAME_ENDED: passiamo direttamente la root alla Scene
                scene = new Scene(root, width, height);
            }

            stage.setScene(scene);
            return loader.getController();

        } catch (IOException e) {
            throw new RuntimeException("Unable to load scene: " + sceneId.path(), e);
        }
    }
}