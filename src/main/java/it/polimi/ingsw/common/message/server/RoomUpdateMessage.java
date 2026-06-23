package it.polimi.ingsw.common.message.server;

import java.io.Serial;

import it.polimi.ingsw.common.visitor.ClientMessageVisitor;

import java.util.List;


/**
 * Immutable data transfer object for room update message.
 *
 * @param notification room notification text
 * @param currentPlayers current players in the room
 */
public record RoomUpdateMessage(
        String notification,
        List<String> currentPlayers
) implements ServerMessage {
    @Serial
    private static final long serialVersionUID = 1L;

    /** {@inheritDoc} */
    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
