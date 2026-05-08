package it.polimi.ingsw.network.messages;

import java.io.Serial;
import java.io.Serializable;

public record DisconnectionMessage() implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
