package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.dto.LeaderboardSnapshot;
import it.polimi.ingsw.network.visitor.ClientMessageVisitor;

public record LeaderboardResponseMessage(LeaderboardSnapshot leaderboard) implements ServerMessage {
    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
