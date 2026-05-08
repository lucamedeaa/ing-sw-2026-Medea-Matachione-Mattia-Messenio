package it.polimi.ingsw.client.network;

import it.polimi.ingsw.client.lightGameModel.EventApplier;
import it.polimi.ingsw.client.lightGameModel.LobbyModel;
import it.polimi.ingsw.client.lightGameModel.MatchModel;
import it.polimi.ingsw.client.view.listeners.GameEndedView;
import it.polimi.ingsw.client.view.listeners.InGameView;
import it.polimi.ingsw.client.view.listeners.LobbyView;
import it.polimi.ingsw.client.view.listeners.MatchmakingView;
import it.polimi.ingsw.network.client.ServerNotificationReceiver;
import it.polimi.ingsw.network.dto.AvailableActionDTO;
import it.polimi.ingsw.network.dto.BoardDTO;
import it.polimi.ingsw.network.dto.LeaderboardSnapshot;
import it.polimi.ingsw.network.dto.PlayerGameCompletedDTO;
import it.polimi.ingsw.network.dto.GameEventDTO;
import it.polimi.ingsw.network.dto.PlayerDTO;
import it.polimi.ingsw.network.messages.GameInfoDTO;

import java.util.List;

public class ClientNotificationController implements ServerNotificationReceiver {
    private final LobbyModel lobbyModel;
    private final MatchModel matchModel;
    private final EventApplier applier;

    private MatchmakingView matchmakingView;
    private LobbyView lobbyView;
    private InGameView inGameView;
    private GameEndedView gameEndedView;

    public ClientNotificationController(LobbyModel lobbyModel, MatchModel matchModel, EventApplier applier) {
        this.lobbyModel = lobbyModel;
        this.matchModel = matchModel;
        this.applier = applier;
    }

    // sino imetodi per permettere agli stati di registrarsi/de-registrarsi
    public void setMatchmakingView(MatchmakingView v) { this.matchmakingView = v; }
    public void setLobbyView(LobbyView v) { this.lobbyView = v; }
    public void setInGameView(InGameView v) { this.inGameView = v; }
    public void setGameEndedView(GameEndedView v) { this.gameEndedView = v; }

    @Override
    public void availableGames(List<GameInfoDTO> games) {
        lobbyModel.setAvailableGames(games); // Aggiorna sempre i dati
        if (matchmakingView != null) matchmakingView.onAvailableGames(games); // Avvisa la UI (se esiste)
    }

    @Override
    public void matchmakingSuccess(String text) {
        lobbyModel.startBatch();
        lobbyModel.setLobbyData(lobbyModel.getLobbyPlayers(), text);
        if (matchmakingView != null) matchmakingView.onMatchmakingSuccess(text);
        lobbyModel.endBatch();
    }

    @Override
    public void roomUpdate(String notification, List<String> currentPlayers) {
        lobbyModel.setLobbyData(currentPlayers, notification);
        if (lobbyView != null) lobbyView.onRoomUpdate(notification, currentPlayers);
    }

    @Override
    public void fullSync(BoardDTO board, List<PlayerDTO> players, String activePlayer, List<AvailableActionDTO> actions) {
        matchModel.startBatch();
        matchModel.reset();
        matchModel.setFullState(board, players, activePlayer);
        matchModel.setAvailableActions(actions);
        matchModel.endBatch();
        if(lobbyView != null){
            lobbyView.onGameStarted();
        }
    }

    @Override
    public void deltaEvent(List<GameEventDTO> events, List<AvailableActionDTO> nextActions, String activePlayer) {
        matchModel.startBatch();
        for (GameEventDTO event : events) {
            event.accept(applier);
        }
        matchModel.setAvailableActions(nextActions);
        matchModel.setActivePlayer(activePlayer);
        matchModel.endBatch();

    }

    @Override
    public void error(String error) {
        lobbyModel.setGlobalError(error);
        if (matchmakingView != null) matchmakingView.onError(error);
        if (lobbyView != null) lobbyView.onError(error);
        if (inGameView != null) inGameView.onError(error);
    }

    @Override
    public void gameAborted(String reason) {
       // lobbyModel.startBatch();

        lobbyModel.setGlobalError(reason);
        matchModel.reset();
        if (inGameView != null) inGameView.onReturnToMatchmaking(reason);
        if (lobbyView != null) lobbyView.onReturnToMatchmaking(reason);


        //lobbyModel.endBatch();
    }

    @Override
    public void gameLeftSuccess(String text) {
       // lobbyModel.startBatch();

        lobbyModel.setGlobalError(text);
        matchModel.reset();
        if (inGameView != null) inGameView.onReturnToMatchmaking(text);
        if (gameEndedView != null) gameEndedView.onReturnToMatchmaking(text);
        if (lobbyView != null) lobbyView.onReturnToMatchmaking(text);


        //lobbyModel.endBatch();
    }

    @Override
    public void gameCompleted(PlayerGameCompletedDTO completedGame) {
        matchModel.setGameCompleted(completedGame);
    }

    @Override
    public void leaderboard(LeaderboardSnapshot leaderboard) {
        matchModel.setGlobalLeaderboard(leaderboard);
    }

    @Override
    public void serverDisconnected(String reason) {
        // TODO: scriverlo
    }
}
