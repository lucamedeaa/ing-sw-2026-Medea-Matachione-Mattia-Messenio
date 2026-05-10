package it.polimi.ingsw.client.view.gui.screen;

import it.polimi.ingsw.client.view.gui.GuiContext;
import it.polimi.ingsw.client.view.gui.GuiNavigator;
import it.polimi.ingsw.client.view.gui.RefreshableScreen;
import it.polimi.ingsw.client.view.listeners.MatchmakingView;
import it.polimi.ingsw.common.network.dto.GameInfoDto;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.List;

// Controller FXML della schermata iniziale. Implementa MatchmakingView e RefreshableScreen.
// Si auto-registra in initialize() su ClientNotificationController e si de-registra prima di navigare.
// I callback arrivano dal thread di rete — Platform.runLater obbligatorio.
// refresh() è il punto unico di rendering: legge da LobbyModel sotto read lock.
// I callback non aggiornano la UI direttamente: impostano flag/stato, poi refresh() li legge.
public class MatchmakingScreen implements MatchmakingView, RefreshableScreen {

    private final GuiContext ctx;
    private final GuiNavigator navigator;
    private boolean showGamesList = false;
    private String pendingNickname = "";

    @FXML private TextField nicknameField;
    @FXML private TextField maxPlayersField;
    @FXML private ListView<String> gamesListView;
    @FXML private Label errorLabel;

    public MatchmakingScreen(GuiContext ctx, GuiNavigator navigator) {
        this.ctx = ctx;
        this.navigator = navigator;
    }

    @FXML
    public void initialize() {
        ctx.notificationController().setMatchmakingView(this);
    }

    @Override
    public void refresh() {
        String error = ctx.lobbyModel().consumeGlobalError();
        errorLabel.setText(error != null ? error : "");

        if (showGamesList) {
            ctx.lobbyModel().getReadLock().lock();
            try {
                List<GameInfoDto> games = ctx.lobbyModel().getAvailableGames();
                gamesListView.getItems().setAll(
                        games.stream()
                                .map(g -> g.getGameId() + " — " + g.getCurrentPlayers() + "/" + g.getMaxPlayers())
                                .toList()
                );
            } finally {
                ctx.lobbyModel().getReadLock().unlock();
            }
        }
    }

    @Override
    public void onAvailableGames(List<GameInfoDto> games) {
        Platform.runLater(() -> {
            showGamesList = true;
            refresh();
        });
    }

    @Override
    public void onMatchmakingSuccess(String text) {
        Platform.runLater(() -> {
            ctx.notificationController().setMatchmakingView(null);
            ctx.session().setNickname(pendingNickname);
            navigator.toLobby();
        });
    }

    @Override
    public void onError(String error) {
        Platform.runLater(this::refresh);
    }

    @Override
    public void onServerDisconnected(String reason) {
        // Replica comportamento TUI: torna a toLobby() anche da Matchmaking.
        Platform.runLater(() -> {
            ctx.notificationController().setMatchmakingView(null);
            navigator.toLobby();
        });
    }

    @FXML
    private void onCreateGame() {
        pendingNickname = nicknameField.getText().trim();
        String maxStr = maxPlayersField.getText().trim();
        try {
            int maxPlayers = Integer.parseInt(maxStr);
            ctx.controller().createGame(pendingNickname, maxPlayers);
        } catch (NumberFormatException e) {
            ctx.lobbyModel().setGlobalError("Errore: max_players deve essere un numero.");
            refresh();
        }
    }

    @FXML
    private void onJoinGame() {
        pendingNickname = nicknameField.getText().trim();
        String selected = gamesListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            ctx.lobbyModel().setGlobalError("Seleziona una partita dalla lista.");
            refresh();
            return;
        }
        String gameId = selected.split(" — ")[0];
        ctx.controller().joinGame(pendingNickname, gameId);
    }

    @FXML
    private void onRefreshList() {
        ctx.controller().getAvailableGames();
    }
}