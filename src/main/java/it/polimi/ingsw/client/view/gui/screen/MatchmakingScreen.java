package it.polimi.ingsw.client.view.gui.screen;

import it.polimi.ingsw.client.view.gui.GuiContext;
import it.polimi.ingsw.client.view.gui.GuiNavigator;
import it.polimi.ingsw.client.view.gui.RefreshableScreen;
import it.polimi.ingsw.client.view.listeners.MatchmakingView;
import it.polimi.ingsw.common.network.dto.GameInfoDto;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.ComboBox;

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
    @FXML private ComboBox<Integer> maxPlayersComboBox;
    @FXML private ListView<String> gamesListView;
    @FXML private Label errorLabel;

    public MatchmakingScreen(GuiContext ctx, GuiNavigator navigator) {
        this.ctx = ctx;
        this.navigator = navigator;
    }

    @FXML private Button createGameButton;
    @FXML private Button joinGameButton;

    @FXML
    public void initialize() {
        ctx.notificationController().setMatchmakingView(this);

        maxPlayersComboBox.getItems().addAll(2, 3, 4, 5);

        // 2. Blocco sottomissione: il bottone è disabilitato se il nickname è vuoto o non è selezionato un numero della tendina
        createGameButton.disableProperty().bind(
            nicknameField.textProperty().isEmpty()
            .or(maxPlayersComboBox.valueProperty().isNull())
        );

        // 3. Regola per "Unisciti": disabilitato se Nickname è vuoto O nessuna partita è selezionata
        joinGameButton.disableProperty().bind(
        nicknameField.textProperty().isEmpty()
        .or(gamesListView.getSelectionModel().selectedItemProperty().isNull()));

        Platform.runLater(() -> {
            if (nicknameField != null && nicknameField.getScene() != null) {
                javafx.stage.Stage stage = (javafx.stage.Stage) nicknameField.getScene().getWindow();
                if (stage != null) {
                    stage.setMinWidth(350);
                    stage.setMinHeight(450);

                    stage.setWidth(350);
                    stage.setHeight(450);
                }
            }
        });
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
                                .map(g -> g.getGameId() + " — Creator: " + g.getCreatorNickname() + " (" + g.getCurrentPlayers() + "/" + g.getMaxPlayers() + ")")
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
        // Non serve più il try-catch, il TextFormatter garantisce che ci sia un numero valido
        int maxPlayers = maxPlayersComboBox.getValue();
        ctx.controller().createGame(pendingNickname, maxPlayers);
    }

    @FXML
    private void onJoinGame() {
        // Il binding garantisce che nicknameField non sia vuoto e che ci sia una selezione
        pendingNickname = nicknameField.getText().trim();
        String selected = gamesListView.getSelectionModel().getSelectedItem();

        // Estrai l'ID (il formato "ID — Giocatori" è gestito a monte nel refresh)
        String gameId = selected.split(" — ")[0];

        ctx.controller().joinGame(pendingNickname, gameId);
    }

    @FXML
    private void onRefreshList() {
        ctx.controller().getAvailableGames();
    }
}