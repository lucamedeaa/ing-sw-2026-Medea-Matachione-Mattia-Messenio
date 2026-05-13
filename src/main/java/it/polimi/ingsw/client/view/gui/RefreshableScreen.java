package it.polimi.ingsw.client.view.gui;

public interface RefreshableScreen {
    void refresh();
    default void handleWindowClose(javafx.stage.WindowEvent event, GuiContext ctx, GuiNavigator navigator) {
        javafx.application.Platform.exit();
        System.exit(0);
    }
}
