package it.polimi.ingsw.client.network;

import it.polimi.ingsw.network.client.ServerNotificationReceiver;
import it.polimi.ingsw.network.messages.AvailableGamesResponseMessage;
import it.polimi.ingsw.network.messages.DeltaEventMessage;
import it.polimi.ingsw.network.messages.ErrorMessage;
import it.polimi.ingsw.network.messages.ErrorMessageDTO;
import it.polimi.ingsw.network.messages.FullSyncMessage;
import it.polimi.ingsw.network.messages.GameAbortedMessage;
import it.polimi.ingsw.network.messages.GameCompletedMessage;
import it.polimi.ingsw.network.messages.GameLeftSuccessMessage;
import it.polimi.ingsw.network.messages.LeaderboardResponseMessage;
import it.polimi.ingsw.network.messages.MatchmakingSuccessMessage;
import it.polimi.ingsw.network.messages.RoomUpdateMessage;
import it.polimi.ingsw.network.visitor.ClientMessageVisitor;

public class SocketServerMessageVisitor implements ClientMessageVisitor {
    private final ServerNotificationReceiver receiver;

    public SocketServerMessageVisitor(ServerNotificationReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void visit(FullSyncMessage message) {
        receiver.fullSync(message.board(), message.players(), message.activePlayer(), message.actions());
    }

    @Override
    public void visit(DeltaEventMessage message) {
        receiver.deltaEvent(message.events(), message.nextActions(), message.activePlayer());
    }

    @Override
    public void visit(ErrorMessage message) {
        receiver.error(message.error());
    }

    @Override
    public void visit(ErrorMessageDTO message) {
        receiver.error(message.error());
    }

    @Override
    public void visit(MatchmakingSuccessMessage message) {
        receiver.matchmakingSuccess(message.text());
    }

    @Override
    public void visit(AvailableGamesResponseMessage message) {
        receiver.availableGames(message.games());
    }

    @Override
    public void visit(GameAbortedMessage message) {
        receiver.gameAborted(message.reason());
    }

    @Override
    public void visit(GameCompletedMessage message) {
        receiver.gameCompleted(message.completedGame());
    }

    @Override
    public void visit(LeaderboardResponseMessage message) {
        receiver.leaderboard(message.leaderboard());
    }

    @Override
    public void visit(RoomUpdateMessage message) {
        receiver.roomUpdate(message.notification(), message.currentPlayers());
    }

    @Override
    public void visit(GameLeftSuccessMessage message) {
        receiver.gameLeftSuccess(message.text());
    }
}
