package it.polimi.ingsw.network.visitor;

import it.polimi.ingsw.network.messages.TakeCardMessage;
import it.polimi.ingsw.network.messages.PlaceTotemMessage;
import it.polimi.ingsw.network.messages.SkipBonusMessage;

public interface InGameVisitor {
    void visit(TakeCardMessage msg);
    void visit(PlaceTotemMessage msg);
    void visit(SkipBonusMessage msg);
}