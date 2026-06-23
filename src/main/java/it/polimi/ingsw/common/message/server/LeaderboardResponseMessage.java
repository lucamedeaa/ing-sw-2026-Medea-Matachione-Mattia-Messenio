package it.polimi.ingsw.common.message.server;

import java.io.Serial;

import it.polimi.ingsw.common.network.dto.LeaderboardSnapshotDto;
import it.polimi.ingsw.common.visitor.ClientMessageVisitor;

/**
 * Immutable data transfer object for leaderboard response message.
 *
 * @param leaderboard leaderboard snapshot
 */
public record LeaderboardResponseMessage(LeaderboardSnapshotDto leaderboard) implements ServerMessage {
    @Serial
    private static final long serialVersionUID = 1L;

    /** {@inheritDoc} */
    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
