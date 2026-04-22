package it.polimi.ingsw.network.visitor;

import it.polimi.ingsw.network.messages.TakeCardMessage;

public interface ServerMessageVisitor {
    // Gestione matchmaking
    void visit(CreateGameMessage msg);
    void visit(JoinGameMessage msg);

    // Gestione mosse in-game
    void visit(TakeCardMessage msg);

    // altri messaggi da mettere (es. PlaceTotemMessage)

}
