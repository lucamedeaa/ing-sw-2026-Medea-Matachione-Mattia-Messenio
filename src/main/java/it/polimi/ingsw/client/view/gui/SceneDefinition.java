package it.polimi.ingsw.client.view.gui;

/**
 * Definition of an FXML scene and its sizing behavior.
 *
 * @param fxmlPath classpath path to the FXML file
 * @param minWidth minimum scene width
 * @param minHeight minimum scene height
 * @param scaleToFill true to scale the scene from a fixed design resolution
 */
public record SceneDefinition(String fxmlPath, double minWidth, double minHeight, boolean scaleToFill) {}
