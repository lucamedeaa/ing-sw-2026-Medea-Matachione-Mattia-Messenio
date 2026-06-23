package it.polimi.ingsw.client.network;

import it.polimi.ingsw.common.message.server.AvailableGamesResponseMessage;
import it.polimi.ingsw.common.message.server.DeltaEventMessage;
import it.polimi.ingsw.common.message.server.ErrorMessage;
import it.polimi.ingsw.common.network.dto.event.ErrorDto;
import it.polimi.ingsw.common.message.server.FullSyncMessage;
import it.polimi.ingsw.common.message.server.GameAbortedMessage;
import it.polimi.ingsw.common.message.server.GameCompletedMessage;
import it.polimi.ingsw.common.message.server.GameLeftSuccessMessage;
import it.polimi.ingsw.common.message.server.LeaderboardResponseMessage;
import it.polimi.ingsw.common.message.server.MatchmakingSuccessMessage;
import it.polimi.ingsw.common.message.server.RoomUpdateMessage;
import it.polimi.ingsw.common.message.server.ServerDisconnectedMessage;
import it.polimi.ingsw.common.visitor.ClientMessageVisitor;

/**
 * Converts decoded socket server messages into client notification callbacks.
 */
public class SocketServerMessageVisitor implements ClientMessageVisitor {
    private final ServerNotificationReceiver receiver;

    /**
     * Creates a visitor that forwards messages to the given receiver.
     *
     * @param receiver receiver notified after message dispatch
     */
    public SocketServerMessageVisitor(ServerNotificationReceiver receiver) {
        this.receiver = receiver;
    }

    /** {@inheritDoc} */
    @Override
    public void visit(FullSyncMessage message) {
        receiver.fullSync(message.board(), message.players(), message.activePlayer(), message.actions(), message.turnOrderTileDto());
    }

    /** {@inheritDoc} */
    @Override
    public void visit(DeltaEventMessage message) {
        receiver.deltaEvent(message.events(), message.nextActions(), message.activePlayer());
    }

    /** {@inheritDoc} */
    @Override
    public void visit(ErrorMessage message) {
        receiver.error(message.error());
    }

    /** {@inheritDoc} */
    @Override
    public void visit(ErrorDto message) {
        receiver.error(message.error());
    }

    /** {@inheritDoc} */
    @Override
    public void visit(MatchmakingSuccessMessage message) {
        receiver.matchmakingSuccess(message.text());
    }

    /** {@inheritDoc} */
    @Override
    public void visit(AvailableGamesResponseMessage message) {
        receiver.availableGames(message.games());
    }

    /** {@inheritDoc} */
    @Override
    public void visit(GameAbortedMessage message) {
        receiver.gameAborted(message.reason());
    }

    /** {@inheritDoc} */
    @Override
    public void visit(GameCompletedMessage message) {
        receiver.gameCompleted(message.completedGame());
    }

    /** {@inheritDoc} */
    @Override
    public void visit(LeaderboardResponseMessage message) {
        receiver.leaderboard(message.leaderboard());
    }

    /** {@inheritDoc} */
    @Override
    public void visit(RoomUpdateMessage message) {
        receiver.roomUpdate(message.notification(), message.currentPlayers());
    }

    /** {@inheritDoc} */
    @Override
    public void visit(GameLeftSuccessMessage message) {
        receiver.gameLeftSuccess(message.text());
    }

    /** {@inheritDoc} */
    @Override
    public void visit(ServerDisconnectedMessage message) {
        receiver.serverDisconnected(message.reason());
    }
}
