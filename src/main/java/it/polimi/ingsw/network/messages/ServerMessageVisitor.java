package it.polimi.ingsw.network.messages;

public interface ServerMessageVisitor {
    //da gestire priam di entrare in partita
    void visit(CreateGameMessage msg);
    void visit(JoinGameMessage msg);
    void visit(GetAvailableGamesMessage msg);

    // Messaggi gestiti in gioco (gestiti da VV)
    void visit(TakeCardMessage msg);
    void visit(PlaceTotemMessage msg);
    void visit(SkipBonusMessage msg);
}