package it.polimi.ingsw.common.message.client;

import java.io.Serial;
import java.io.Serializable;

public record PingMessage() implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
