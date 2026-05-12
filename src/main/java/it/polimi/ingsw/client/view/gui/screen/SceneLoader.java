package it.polimi.ingsw.client.view.gui.screen;

import it.polimi.ingsw.client.view.gui.SceneId;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
            stage.setScene(new Scene(root));
            return loader.getController();
        } catch (IOException e) {
            throw new RuntimeException("Unable to load scene: " + sceneId.path(), e);
        }
    }
}