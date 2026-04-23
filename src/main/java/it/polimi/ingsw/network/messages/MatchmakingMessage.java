package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.visitor.MatchmakingVisitor;

public interface MatchmakingMessage extends ClientMessage {
    void accept(MatchmakingVisitor visitor);
}