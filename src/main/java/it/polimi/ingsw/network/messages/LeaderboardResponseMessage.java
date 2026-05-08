package it.polimi.ingsw.network.messages;

import java.io.Serial;

import it.polimi.ingsw.network.dto.LeaderboardSnapshot;
import it.polimi.ingsw.network.visitor.ClientMessageVisitor;

public record LeaderboardResponseMessage(LeaderboardSnapshot leaderboard) implements ServerMessage {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
