package it.polimi.ingsw.client.view.gui.screen;

import javafx.stage.WindowEvent;

public interface RefreshableScreen {
    void refresh();
    default void onEnter() {}
    default void onExit() {}
    default void handleWindowClose(WindowEvent event) {}
}