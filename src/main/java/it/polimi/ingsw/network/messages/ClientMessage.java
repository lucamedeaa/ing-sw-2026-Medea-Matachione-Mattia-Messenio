package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.server.ConnectionState;

import java.io.Serializable;

public interface ClientMessage extends Serializable {

    default void dispatchTo(ConnectionState state) {
        throw new UnsupportedOperationException("This message is handled by the network layer.");
    }
}
