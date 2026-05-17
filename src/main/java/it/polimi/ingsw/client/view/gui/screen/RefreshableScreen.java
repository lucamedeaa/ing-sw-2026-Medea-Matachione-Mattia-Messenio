package it.polimi.ingsw.client.view.gui.screen;

import it.polimi.ingsw.client.view.gui.GuiContext;
import it.polimi.ingsw.client.view.gui.GuiNavigator;
import javafx.stage.WindowEvent;

public interface RefreshableScreen {
    void refresh();
    default void onEnter() {}
    default void onExit() {}
    default void handleWindowClose(WindowEvent event) {}
}