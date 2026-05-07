package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.network.ServerController;
import it.polimi.ingsw.client.tui.UIState;
import java.util.function.Consumer;

public interface ClientUI {
        void setController(ServerController controller);
        void start();
}