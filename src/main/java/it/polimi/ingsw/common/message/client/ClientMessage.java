package it.polimi.ingsw.common.message.client;

import it.polimi.ingsw.server.network.state.ConnectionState;

import java.io.Serializable;

/** Defines the contract for client message. */
public interface ClientMessage extends Serializable {

    default void dispatchTo(ConnectionState state) {
        throw new UnsupportedOperationException("This message is handled by the network layer.");
    }
}
