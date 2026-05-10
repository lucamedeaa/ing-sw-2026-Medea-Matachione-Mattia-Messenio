package it.polimi.ingsw.client.network;

import it.polimi.ingsw.client.model.EventApplier;
import it.polimi.ingsw.client.model.LobbyModel;
import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.view.listeners.GameEndedView;
import it.polimi.ingsw.client.view.listeners.InGameView;
import it.polimi.ingsw.client.view.listeners.LobbyView;
import it.polimi.ingsw.client.view.listeners.MatchmakingView;
import it.polimi.ingsw.common.network.dto.action.ActionDto;
import it.polimi.ingsw.common.network.dto.BoardDto;
import it.polimi.ingsw.common.network.dto.LeaderboardSnapshotDto;
import it.polimi.ingsw.common.network.dto.PlayerGameCompletedDto;
import it.polimi.ingsw.common.network.dto.event.GameEventDto;
import it.polimi.ingsw.common.network.dto.PlayerDto;
import it.polimi.ingsw.common.network.dto.GameInfoDto;

import java.util.List;

public class ClientNotificationController implements ServerNotificationReceiver {
    private final LobbyModel lobbyModel;
    private final GameModel gameModel;
    private final EventApplier applier;

    private MatchmakingView matchmakingView;
    private LobbyView lobbyView;
    private InGameView inGameView;
    private GameEndedView gameEndedView;

    public ClientNotificationController(LobbyModel lobbyModel, GameModel gameModel, EventApplier applier) {
        this.lobbyModel = lobbyModel;
        this.gameModel = gameModel;
        this.applier = applier;
    }

    // sino imetodi per permettere agli stati di registrarsi/de-registrarsi
    public void setMatchmakingView(MatchmakingView v) { this.matchmakingView = v; }
    public void setLobbyView(LobbyView v) { this.lobbyView = v; }
    public void setInGameView(InGameView v) { this.inGameView = v; }
    public void setGameEndedView(GameEndedView v) { this.gameEndedView = v; }

    @Override
    public void availableGames(List<GameInfoDto> games) {
        lobbyModel.setAvailableGames(games); // Aggiorna sempre i dati
        if (matchmakingView != null) matchmakingView.onAvailableGames(games); // Avvisa la UI (se esiste)
    }

    @Override
    public void matchmakingSuccess(String text) {
        if (matchmakingView != null) matchmakingView.onMatchmakingSuccess(text);

        lobbyModel.executeBatch(() -> {
            lobbyModel.setLobbyData(lobbyModel.getLobbyPlayers(), text);
        });

    }

    @Override
    public void roomUpdate(String notification, List<String> currentPlayers) {
        lobbyModel.executeBatch(() -> {
            lobbyModel.setLobbyData(currentPlayers, notification);

            if (lobbyView != null) lobbyView.onRoomUpdate(notification, currentPlayers);
        });
    }

    @Override
    public void fullSync(BoardDto board, List<PlayerDto> players, String activePlayer, List<ActionDto> actions) {
        if (lobbyView != null) {
            lobbyView.onGameStarted();
        }

        gameModel.executeBatch(() -> {
            gameModel.reset();
            gameModel.setFullState(board, players, activePlayer);
            gameModel.setAvailableActions(actions);
        });
    }

    @Override
    public void deltaEvent(List<GameEventDto> events, List<ActionDto> nextActions, String activePlayer) {
        for (GameEventDto event : events) {
            gameModel.executeBatch(() -> {
                event.accept(applier);
            });

        }

        gameModel.executeBatch(() -> {
            gameModel.setAvailableActions(nextActions);
            gameModel.setActivePlayer(activePlayer);
        });
    }

    @Override
    public void error(String error) {
        if (inGameView != null) {
            gameModel.setGlobalError(error);
            inGameView.onError(error);
        }
        else {
            lobbyModel.setGlobalError(error);
            if (matchmakingView != null) matchmakingView.onError(error);
            if (lobbyView != null) lobbyView.onError(error);
        }
    }

    @Override
    public void gameAborted(String reason) {
        gameModel.reset();
        lobbyModel.setGlobalErrorSilent(reason);

        if (inGameView != null) inGameView.onReturnToMatchmaking(reason);
        if (lobbyView != null) lobbyView.onReturnToMatchmaking(reason);

    }

    @Override
    public void gameLeftSuccess(String text) {
        gameModel.reset();
        lobbyModel.setGlobalErrorSilent(text);

        if (inGameView != null) inGameView.onReturnToMatchmaking(text);
        if (gameEndedView != null) gameEndedView.onReturnToMatchmaking(text);
        if (lobbyView != null) lobbyView.onReturnToMatchmaking(text);

    }

    @Override
    public void gameCompleted(PlayerGameCompletedDto completedGame) {
        gameModel.setGameCompleted(completedGame);
    }

    @Override
    public void leaderboard(LeaderboardSnapshotDto leaderboard) {
        gameModel.setGlobalLeaderboard(leaderboard);
    }

    @Override
    public void serverDisconnected(String reason) {
        if (matchmakingView != null) matchmakingView.onServerDisconnected(reason);
        else if (lobbyView != null) lobbyView.onServerDisconnected(reason);
        else if (inGameView != null) inGameView.onServerDisconnected(reason);
        else if (gameEndedView != null) gameEndedView.onServerDisconnected(reason);
    }
}
