package it.polimi.ingsw.client.network;

import it.polimi.ingsw.client.lightGameModel.EventApplier;
import it.polimi.ingsw.client.lightGameModel.LightGameModel;
import it.polimi.ingsw.client.tui.UIState;
import it.polimi.ingsw.client.view.ClientUI;
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
    private final LightGameModel model;
    private final EventApplier applier;

    private MatchmakingView matchmakingView;
    private LobbyView lobbyView;
    private InGameView inGameView;

    // Nota: ho rimosso ClientUI dal costruttore, non ci serve più!
    public ClientNotificationController(LightGameModel model, EventApplier applier) {
        this.model = model;
        this.applier = applier;
    }

    // sino imetodi per permettere agli stati di registrarsi/de-registrarsi
    public void setMatchmakingView(MatchmakingView v) { this.matchmakingView = v; }
    public void setLobbyView(LobbyView v) { this.lobbyView = v; }
    public void setInGameView(InGameView v) { this.inGameView = v; }

    @Override
    public void availableGames(List<GameInfoDTO> games) {
        model.setAvailableGames(games); // 1. Aggiorna sempre i dati
        if (matchmakingView != null) matchmakingView.onAvailableGames(games); // 2. Avvisa la UI (se esiste)
    }

    @Override
    public void matchmakingSuccess(String text) {
        model.setLobbyData(model.getLobbyPlayers(), text);
        if (matchmakingView != null) matchmakingView.onMatchmakingSuccess(text);
    }

    @Override
    public void roomUpdate(String notification, List<String> currentPlayers) {
        model.setLobbyData(currentPlayers, notification);
        if (lobbyView != null) lobbyView.onRoomUpdate(notification, currentPlayers);
    }

    @Override
    public void fullSync(BoardDTO board, List<PlayerDTO> players, String activePlayer, List<AvailableActionDTO> actions) {
        model.setFullState(board, players, activePlayer);
        model.setAvailableActions(actions);
        if(lobbyView != null){
            lobbyView.onGameStarted();
        }
    }

    @Override
    public void deltaEvent(List<GameEventDTO> events, List<AvailableActionDTO> nextActions, String activePlayer) {
        model.startBatch();
        for (GameEventDTO event : events) {
            event.accept(applier);
        }
        model.setAvailableActions(nextActions);
        model.setActivePlayer(activePlayer);
        model.endBatch();
        if (inGameView != null){
            inGameView.onDeltaEvent();
        }
    }

    @Override
    public void error(String error) {
        model.setGlobalError(error);
        if (matchmakingView != null) matchmakingView.onError(error);
        if (lobbyView != null) lobbyView.onError(error);
        if (inGameView != null) inGameView.onError(error);
    }

    @Override
    public void gameAborted(String reason) {
        model.setGameAborted(reason);
        if (inGameView != null) inGameView.onGameAborted(reason);
        if (lobbyView != null) lobbyView.onError("Partita annullata: " + reason);
    }


    @Override
    public void gameLeftSuccess(String text) {
        model.setGameAborted(text);
        //if (lobbyView != null) lobbyView.onGameLeftSuccess(text);
        //if (inGameView != null) inGameView.onGameLeftSuccess(text);
    }

    @Override
    public void gameCompleted(PlayerGameCompletedDTO completedGame) {
        // TODO implement client-side completed-game handling.
    }

    @Override
    public void leaderboard(LeaderboardSnapshot leaderboard) {
        // TODO implement client-side leaderboard handling.
    }
}
