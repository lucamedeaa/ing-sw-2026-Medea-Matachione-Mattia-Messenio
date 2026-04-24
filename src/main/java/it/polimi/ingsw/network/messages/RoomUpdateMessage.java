package it.polimi.ingsw.network.messages;
import it.polimi.ingsw.network.messages.ServerMessage;
import it.polimi.ingsw.network.visitor.ClientMessageVisitor;

import java.io.Serializable;
import java.util.List;


public record RoomUpdateMessage(
        String notification,
        List<String> currentPlayers
) implements ServerMessage {

    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}