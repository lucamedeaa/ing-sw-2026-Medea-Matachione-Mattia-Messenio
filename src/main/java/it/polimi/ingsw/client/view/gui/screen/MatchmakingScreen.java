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

import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import java.net.URL;

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

    //@FXML private MediaView bgMediaView;
    @FXML private StackPane videoContainer; // Non più MediaView
    private MediaPlayer mediaPlayer;

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
        .or(gamesListView.getSelectionModel().selectedItemProperty().isNull())
    );

        startVideoBackground();
    }

/*    private void startVideoBackground() {
        URL videoUrl = getClass().getResource("/video/MesosMuroFinalRend.mp4");
        if (videoUrl != null) {
            Media media = new Media(videoUrl.toExternalForm());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop continuo

            bgMediaView.setMediaPlayer(mediaPlayer);

            // Per far sì che il video si adatti ridimensionando la finestra
            Platform.runLater(() -> {
                if (bgMediaView.getScene() != null) {
                    bgMediaView.fitWidthProperty().bind(bgMediaView.getScene().widthProperty());
                    bgMediaView.fitHeightProperty().bind(bgMediaView.getScene().heightProperty());
                }
            });

            mediaPlayer.play();
        } else {
            System.err.println("Impossibile trovare il file video!");
        }
    }*/

    private void startVideoBackground() {
        new Thread(() -> {
            try {
                URL resource = getClass().getResource("/video/MesosMuroFinalRend.mp4");
                if (resource == null) return;

                Media media = new Media(resource.toExternalForm());
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                mediaPlayer.setMute(true); // Evita conflitti audio tra client

                Platform.runLater(() -> {
                    MediaView mediaView = new MediaView(mediaPlayer);
                    mediaView.setPreserveRatio(false);
                    mediaView.fitWidthProperty().bind(videoContainer.widthProperty());
                    mediaView.fitHeightProperty().bind(videoContainer.heightProperty());

                    videoContainer.getChildren().add(mediaView);
                    mediaPlayer.play();
                });
            } catch (Exception e) {
                System.err.println("Impossibile caricare il video: " + e.getMessage());
            }
        }).start();
    }

    private void stopVideo() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose(); // Libera definitivamente le risorse
        }
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
            stopVideo();
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
            stopVideo();
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