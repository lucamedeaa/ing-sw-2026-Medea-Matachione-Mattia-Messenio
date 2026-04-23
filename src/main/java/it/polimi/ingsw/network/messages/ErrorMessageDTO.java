package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.visitor.ClientMessageVisitor;

public record ErrorMessageDTO(String error) implements ServerMessage {
    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}