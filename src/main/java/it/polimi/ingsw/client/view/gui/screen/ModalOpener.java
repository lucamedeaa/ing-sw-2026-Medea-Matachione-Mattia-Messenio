package it.polimi.ingsw.client.view.gui.screen;

import it.polimi.ingsw.client.view.gui.SceneDefinition;

/**
 * Contract for screens that can open modal scenes.
 */
public interface ModalOpener {
    /**
     * Opens a scene as a modal window.
     *
     * @param def scene definition to load
     */
    void openModal(SceneDefinition def);
}
