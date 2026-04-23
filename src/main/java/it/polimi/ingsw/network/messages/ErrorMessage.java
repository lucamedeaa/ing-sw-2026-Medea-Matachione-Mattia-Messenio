package it.polimi.ingsw.network.messages;

public record ErrorMessage(String details) implements ServerMessage {
    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}

//TODO bisogna mettere visit nel clientMEssageVisitor e implementarlo nel client