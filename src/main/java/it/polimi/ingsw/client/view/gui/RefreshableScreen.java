package it.polimi.ingsw.client.view.gui;

import javafx.stage.WindowEvent;

public interface RefreshableScreen {
    void refresh();

    default void onEnter() {}
    /** Chiamato dal router prima di caricare la scena successiva. Override per cleanup (es. video). */
    default void onExit() {}

    default void handleWindowClose(WindowEvent event, GuiContext ctx, GuiNavigator navigator) {
        ctx.lifecycle().requestShutdown();
    }
}