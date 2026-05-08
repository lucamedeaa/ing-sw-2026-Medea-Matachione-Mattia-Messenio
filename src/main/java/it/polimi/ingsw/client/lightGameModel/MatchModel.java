package it.polimi.ingsw.client.lightGameModel;

import it.polimi.ingsw.network.dto.*;
import java.util.*;

public class MatchModel extends ObservableModel {

    //  BOARD STATE
    private final List<Integer> upperRowCards = new ArrayList<>();
    private final List<Integer> lowerRowCards = new ArrayList<>();
    private int currentEra = 1;
    private int currentRound = 1;

    // PLAYER STATE
    private final Map<String, LightPlayer> players = new HashMap<>();
    private final Map<String, Integer> playerTotemPositions = new HashMap<>();
    private final Map<String, List<Integer>> playerTribes = new HashMap<>();
    private final Map<String, Integer> playerReturnPositions = new HashMap<>();

    //  TURN STATE
    private String activePlayer = "";
    private List<AvailableActionDTO> actions = new ArrayList<>();

    //  END GAME STATE
    private boolean isGameOver = false;
    private List<PlayerScoreDTO> leaderboard = new ArrayList<>();
    private List<String> winners = new ArrayList<>();
    private String abortReason = null;
    private PlayerGameCompletedDTO localResult = null; // Risultati personali
    private LeaderboardSnapshot globalLeaderboard = null; // Classifica globale


    private final List<String> gameLogs = new ArrayList<>();


    //  SETTERS

    public void setFullState(BoardDTO board, List<PlayerDTO> playersList, String activePlayer) {
        this.upperRowCards.clear();
        this.upperRowCards.addAll(board.UpperRowCards());
        this.lowerRowCards.clear();
        this.lowerRowCards.addAll(board.LowerRowCards());
        this.currentEra = board.currentEra();
        this.currentRound = board.currentRound();

        this.players.clear();
        this.playerReturnPositions.clear();
        this.playerTotemPositions.clear();

        for (PlayerDTO p : playersList) {
            this.players.put(p.nickname(), new LightPlayer(p));
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

    public void returnTotemToTrack(String nickname, int returnIndex) {
        playerTotemPositions.remove(nickname);
        playerReturnPositions.put(nickname, returnIndex);
        notifyUI();
    }

    public void addCardToPlayerTribe(String nickname, Integer cardId) {
        playerTribes.computeIfAbsent(nickname, k -> new ArrayList<>()).add(cardId);
        notifyUI();
    }

    public void updatePlayerTribe(String nickname, List<Integer> newTribeCards) {
        playerTribes.put(nickname, new ArrayList<>(newTribeCards));
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

    public void addGameLog(String log) {
        this.gameLogs.add(log);
        notifyUI();
    }

    public List<String> consumeGameLogs() {
        List<String> copy = new ArrayList<>(this.gameLogs);
        this.gameLogs.clear();
        return copy;
    }

    public void setGameAborted(String reason) {
        this.abortReason = reason;
        notifyUI();
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

    public void setGameCompleted(PlayerGameCompletedDTO result) {
        this.localResult = result;
        notifyUI();
    }

    public void setGlobalLeaderboard(LeaderboardSnapshot snapshot) {
        this.globalLeaderboard = snapshot;
        endBatch();
    }

    public void reset() {
        this.upperRowCards.clear();
        this.lowerRowCards.clear();
        this.players.clear();
        this.playerTotemPositions.clear();
        this.playerTribes.clear();
        this.playerReturnPositions.clear();
        this.gameLogs.clear();
        this.currentEra = 1;
        this.currentRound = 1;
        this.activePlayer = "";
        this.actions = new ArrayList<>();

        // RESET DEI FLAG DI FINE PARTITA
        this.isGameOver = false;
        this.leaderboard = new ArrayList<>();
        this.winners = new ArrayList<>();
        this.abortReason = null;
        this.localResult = null;
        this.globalLeaderboard = null;
    }

    // GETTERS
    public PlayerGameCompletedDTO getLocalResult() { return localResult; }
    public LeaderboardSnapshot getGlobalLeaderboard() { return globalLeaderboard; }
    public List<Integer> getUpperRowCards() { return new ArrayList<>(upperRowCards); }
    public List<Integer> getLowerRowCards() { return new ArrayList<>(lowerRowCards); }
    public Map<String, LightPlayer> getPlayers() { return new HashMap<>(players); }
    public Map<String, Integer> getTotemPositions() { return new HashMap<>(playerTotemPositions); }
    public Map<String, Integer> getReturnPositions() { return new HashMap<>(playerReturnPositions); }
    public Map<String, List<Integer>> getTribes() { return new HashMap<>(playerTribes); }
    public int getCurrentEra() { return currentEra; }
    public int getCurrentRound() { return currentRound; }
    public List<AvailableActionDTO> getMyActions() { return new ArrayList<>(actions); }
    public String getActivePlayer() { return activePlayer; }
    public String getAbortReason() { return abortReason; }
    public boolean isGameOver() { return isGameOver; }
    public List<PlayerScoreDTO> getLeaderboard() { return new ArrayList<>(leaderboard); }
    public List<String> getWinners() { return new ArrayList<>(winners); }
}