package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.server.ConnectionState;

public record SkipActionMessage() implements ClientMessage {
    @Override
    public void dispatchTo(ConnectionState state) {
        state.skipAction();
    }
}
