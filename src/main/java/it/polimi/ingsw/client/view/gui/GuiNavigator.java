package it.polimi.ingsw.client.view.gui;

public interface GuiNavigator {
    //TODO:Interfaccia di navigazione tra schermate. Unico modo con cui le Screen cambiano stato: chiamano toMatchmaking(),
    // toLobby(), toInGame(), toGameEnded(), toDisconnected(reason).
    // Le Screen dipendono da questa interfaccia, mai dalla concreta.
    // toViewTribe() e toInfo() non ci sono — sono dialoghi interni a InGameScreen.
}
