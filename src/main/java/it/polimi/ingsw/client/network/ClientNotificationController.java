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

/**
 * Coordinates server notifications, client models, and the currently active UI views.
 */
public class ClientNotificationController implements ServerNotificationReceiver {
    private final LobbyModel lobbyModel;
    private final GameModel gameModel;
    private final EventApplier applier;

    private volatile MatchmakingView matchmakingView;
    private volatile LobbyView lobbyView;
    private volatile InGameView inGameView;
    private volatile GameEndedView gameEndedView;

    /**
     * Creates the notification controller.
     *
     * @param lobbyModel lobby model to update before game start
     * @param gameModel game model to update during and after a game
     * @param applier event applier used for delta updates
     */
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

        MatchmakingView view = matchmakingView;
        if (view != null) view.onAvailableGames(games);
    }

    @Override
    public void matchmakingSuccess(String text) {
        if (matchmakingView != null) matchmakingView.onMatchmakingSuccess(text);

        lobbyModel.executeBatch(() ->
            lobbyModel.setLobbyData(lobbyModel.getLobbyPlayers(), text)
        );

    }

    @Override
    public void roomUpdate(String notification, List<String> currentPlayers) {
        lobbyModel.executeBatch(() -> {
            lobbyModel.setLobbyData(currentPlayers, notification);

            LobbyView view = lobbyView;
            if (view != null) view.onRoomUpdate(notification, currentPlayers);
        });
    }

    @Override
    public void fullSync(BoardDto board, List<PlayerDto> players, String activePlayer, List<ActionDto> actions) {
        LobbyView view = lobbyView;
        if (view != null) {
            view.onGameStarted();
        }

        gameModel.executeBatch(() -> {
            gameModel.reset();
            gameModel.setFullState(board, players, activePlayer);
            gameModel.setAvailableActions(actions);
        });
    }

    @Override
    public void deltaEvent(List<GameEventDto> events, List<ActionDto> nextActions, String activePlayer) {

        gameModel.executeBatch(() -> {
            for (GameEventDto event : events) {
                event.accept(applier);
            }
            gameModel.setAvailableActions(nextActions);
            gameModel.setActivePlayer(activePlayer);
        });
    }

    @Override
    public void error(String error) {
        InGameView igView = inGameView;
        if (igView != null) {
            gameModel.setGlobalError(error);
            igView.onError(error);
        } else {
            lobbyModel.setGlobalError(error);
            MatchmakingView mmView = matchmakingView;
            if (mmView != null) mmView.onError(error);
            LobbyView lView = lobbyView;
            if (lView != null) lView.onError(error);
        }
    }

    @Override
    public void gameAborted(String reason) {
        gameModel.reset();
        lobbyModel.setGlobalErrorSilent(reason);

        // uso riferimenti in variabili locali per evitare che
        // diventino null tra il check (if != null) e l'esecuzione.
        InGameView currentInGame = inGameView;
        LobbyView currentLobby = lobbyView;

        if (currentInGame != null) currentInGame.onReturnToMatchmaking(reason);
        if (currentLobby != null) currentLobby.onReturnToMatchmaking(reason);

    }

    @Override
    public void gameLeftSuccess(String text) {
        gameModel.reset();
        lobbyModel.setGlobalErrorSilent(text);

        InGameView igView = inGameView;
        GameEndedView geView = gameEndedView;
        LobbyView lView = lobbyView;

        if (igView != null) igView.onReturnToMatchmaking(text);
        if (geView != null) geView.onReturnToMatchmaking(text);
        if (lView != null) lView.onReturnToMatchmaking(text);

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
        MatchmakingView currentMatchmaking = matchmakingView;
        LobbyView currentLobby = lobbyView;
        InGameView currentInGame = inGameView;
        GameEndedView currentGameEnded = gameEndedView;

        if (currentMatchmaking != null) currentMatchmaking.onServerDisconnected(reason);
        else if (currentLobby != null) currentLobby.onServerDisconnected(reason);
        else if (currentInGame != null) currentInGame.onServerDisconnected(reason);
        else if (currentGameEnded != null) currentGameEnded.onServerDisconnected(reason);
        else {
            // Policy di fallback obbligatoria: se l'evento fatale arriva esattamente
            // mentre la UI sta cambiando schermata (tutte le view sono null),
            // bisogna comunque forzare la chiusura.
            System.err.println("FATAL CONNECTION ERROR: " + reason);
            System.exit(1);
        }
    }
}
