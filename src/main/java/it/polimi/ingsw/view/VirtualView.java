package it.polimi.ingsw.view;

import it.polimi.ingsw.model.ModelObserver;
import it.polimi.ingsw.network.messages.ClientMessage;
import it.polimi.ingsw.network.messages.ServerMessageVisitor;
import it.polimi.ingsw.network.server.ClientConnection;
import it.polimi.ingsw.controller.GameController;

public class VirtualView implements ModelObserver, ServerMessageVisitor {

    private final String nickname;
    private final ClientConnection connection;
    private final GameController controller;

    public VirtualView(String nickname, ClientConnection connection, GameController controller) {
        this.nickname = nickname;
        this.connection = connection;
        this.controller = controller;
    }
    //TODO: gestione timer se turno

    @Override
    public void onModelUpdate(GameMemento memento) {
        // TODO: trasformarlo in snapshot e mandarlo
        //connection.send(Gamesnapshot);
    }

    public void onMessageReceived(ClientMessage message) {
        message.accept(this);
    }

    //TODO: scrivere sta roba che chiami il controller e faccia zompare la partita
    public void handleDisconnection(String nickname){}
    //TODO: fare i vari visit che chiamano il controller
}
