package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.messages.PlaceTotemMessage;
import it.polimi.ingsw.network.messages.SkipActionMessage;
import it.polimi.ingsw.network.messages.TakeCardMessage;
import it.polimi.ingsw.network.visitor.InGameVisitor;

public class SocketGameActionVisitor implements InGameVisitor {

    private final ConnectionState connectionState;

    public SocketGameActionVisitor(ConnectionState connectionState) {
        this.connectionState = connectionState;
    }

    @Override
    public void visit(TakeCardMessage msg) {
        connectionState.takeCard(msg.row(), msg.col());
    }

    @Override
    public void visit(PlaceTotemMessage msg) {
        connectionState.placeTotem(msg.positionIndex());
    }

    @Override
    public void visit(SkipActionMessage msg) {
        connectionState.skipAction();
    }
}
