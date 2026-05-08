package it.polimi.ingsw.common.message.server;

import java.io.Serial;

import it.polimi.ingsw.common.visitor.ClientMessageVisitor;

import java.util.List;


public record RoomUpdateMessage(
        String notification,
        List<String> currentPlayers
) implements ServerMessage {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
