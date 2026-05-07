package it.polimi.ingsw.client.lightGameModel;

import it.polimi.ingsw.network.dto.*;
import it.polimi.ingsw.model.enums.TotemColor;
import it.polimi.ingsw.network.messages.GameInfoDTO;

import java.util.*;


public class LightGameModel {
    private final List<Integer> upperRowCards = new ArrayList<>();
    private final List<Integer> lowerRowCards = new ArrayList<>();
    private final Map<String, LightPlayer> players = new HashMap<>();
    private final Map<String, Integer> playerTotemPositions = new HashMap<>();
    private final Map<String, List<Integer>> playerTribes = new HashMap<>();

    private List<AvailableActionDTO> actions = new ArrayList<>();
    private int currentEra = 1;
    private int currentRound = 1;

    private final List<UIObserver> observers = new ArrayList<>();

    private boolean isGameOver = false;
    private List<PlayerScoreDTO> leaderboard = new ArrayList<>();
    private List<String> winners = new ArrayList<>();

    private boolean batchMode = false;

    private String activePlayer = "";

    private final Map<String, Integer> playerReturnPositions = new HashMap<>();

    private String abortReason = null;

    private List<GameInfoDTO> availableGames = new ArrayList<>();
    private List<String> lobbyPlayers = new ArrayList<>();
    private String lobbyNotification = "";
    private String globalError = "";


    public void setGameAborted(String reason) {
        this.abortReason = reason;
        notifyUI();
    }

    public String getAbortReason() {
        return abortReason;
    }

    public void returnTotemToTrack(String nickname, int returnIndex) {
        playerTotemPositions.remove(nickname);
        playerReturnPositions.put(nickname, returnIndex);
        notifyUI();
    }

    public Map<String, Integer> getReturnPositions() {
        return new HashMap<>(playerReturnPositions);
    }

    public void startBatch() { this.batchMode = true; }
    public void endBatch() {
        this.batchMode = false;
        notifyUI(); // Chiama la TUI UNA SOLA VOLTA alla fine
    }

    public void addObserver(UIObserver observer) {
        this.observers.add(observer);
    }

    private void notifyUI() {
        if (batchMode) return; // SE È IN BATCH, BLOCCA LO SPAM
        for (UIObserver obs : observers) {
            obs.onStateChanged();
        }
    }

    public void setFullState(BoardDTO board, List<PlayerDTO> playersList, String activePlayer) {
        this.upperRowCards.clear();
        this.upperRowCards.addAll(board.UpperRowCards());
        this.lowerRowCards.clear();
        this.lowerRowCards.addAll(board.LowerRowCards());
        this.currentEra = board.currentEra();
        this.currentRound = board.currentRound();

        this.players.clear();
        this.playerReturnPositions.clear();
        for (PlayerDTO p : playersList) {
            this.players.put(p.nickname(), new  LightPlayer(p));
            this.playerTribes.putIfAbsent(p.nickname(), new ArrayList<>());
        }
        this.activePlayer = activePlayer;
        notifyUI();
    }

    public void setAvailableActions(List<AvailableActionDTO> actions) {
        this.actions = actions;
        notifyUI();
    }

    public void setActivePlayer(String activePlayer) {
        this.activePlayer = activePlayer;
        notifyUI();
    }

    public void removeCard(int row, int col) {
        if (row == 0) upperRowCards.set(col, null);
        else lowerRowCards.set(col, null);
        notifyUI();
    }

    public void refillBoardRow(int row, List<Integer> newCardIds) {
        List<Integer> targetRow = (row == 0) ? upperRowCards : lowerRowCards;
        targetRow.clear();
        targetRow.addAll(newCardIds);
        notifyUI();
    }

    public void updateTotemPosition(String nickname, int positionIndex) {
        playerTotemPositions.put(nickname, positionIndex);
        notifyUI();
    }

    public void addCardToPlayerTribe(String nickname, Integer cardId) {
        playerTribes.computeIfAbsent(nickname, k -> new ArrayList<>()).add(cardId);
        notifyUI();
    }

    public void updateEra(int newEra) {
        this.currentEra = newEra;
        notifyUI();
    }

    public void updateRound(int newRound) {
        this.currentRound = newRound;
        this.playerReturnPositions.clear();
        notifyUI();
    }

    public void updatePlayerResources(String nickname, int newFood, int newPrestige, int newFoodDiscount) {
        LightPlayer player = players.get(nickname);
        if (player != null) {
            player.setFood(newFood);
            player.setPrestige(newPrestige);
            player.setFoodDiscount(newFoodDiscount);
            notifyUI();
        }
    }

    public void setGameOver(List<PlayerScoreDTO> leaderboard) {
        this.isGameOver = true;
        this.leaderboard = new ArrayList<>(leaderboard);
        notifyUI();
    }

    public void setWinners(List<String> winners) {
        this.winners = new ArrayList<>(winners);
        this.leaderboard = this.players.values().stream()
                .sorted(Comparator.comparingInt(LightPlayer::getPrestige).reversed())
                .map(p -> new PlayerScoreDTO(p.getNickname(), p.getPrestige(), p.getFood()))
                .toList();
        this.isGameOver = true;
        notifyUI();
    }

    public void updatePlayerTribe(String nickname, List<Integer> newTribeCards) {
        playerTribes.put(nickname, new ArrayList<>(newTribeCards));
        notifyUI();
    }


    private final List<String> gameLogs = new ArrayList<>();

    public void addGameLog(String log) {
        this.gameLogs.add(log);
        notifyUI();
    }

    public List<String> consumeGameLogs() {
        List<String> copy = new ArrayList<>(this.gameLogs);
        this.gameLogs.clear();
        return copy;
    }

    public void setAvailableGames(List<GameInfoDTO> games) {
        this.availableGames = games;
        notifyUI();
    }

    public List<GameInfoDTO> getAvailableGames() {
        return new ArrayList<>(this.availableGames);
    }

    public void setLobbyData(List<String> players, String notification) {
        this.lobbyPlayers = new ArrayList<>(players);
        this.lobbyNotification = notification;
        notifyUI();
    }

    public List<String> getLobbyPlayers() {
        return new ArrayList<>(this.lobbyPlayers);
    }

    public String getLobbyNotification() {
        return this.lobbyNotification;
    }

    public void setGlobalError(String error) {
        this.globalError = error;
        notifyUI();
    }

     // Ritorna l'errore corrente e lo svuota immediatamente,
     // per evitare che lo stesso errore venga stampato a ogni render successivo.

    public String consumeGlobalError() {
        String err = this.globalError;
        this.globalError = "";
        return err;
    }
    // Getter che la TUI userà per disegnare la schermata
    public List<Integer> getUpperRowCards() { return new ArrayList<>(upperRowCards); }
    public List<Integer> getLowerRowCards() { return new ArrayList<>(lowerRowCards); }
    public Map<String, LightPlayer> getPlayers() { return new HashMap<>(players); }
    public Map<String, Integer> getTotemPositions() { return new HashMap<>(playerTotemPositions); }
    public Map<String, List<Integer>> getTribes() { return new HashMap<>(playerTribes); }
    public int getCurrentEra() { return currentEra; }
    public int getCurrentRound() { return currentRound; }
    public List<AvailableActionDTO> getMyActions() { return new ArrayList<>(actions); }
    public boolean isGameOver() { return isGameOver; }
    public List<PlayerScoreDTO> getLeaderboard() { return new ArrayList<>(leaderboard); }
    public List<String> getWinners() {return new ArrayList<>(winners);}
    public String getActivePlayer() {return this.activePlayer;}
}