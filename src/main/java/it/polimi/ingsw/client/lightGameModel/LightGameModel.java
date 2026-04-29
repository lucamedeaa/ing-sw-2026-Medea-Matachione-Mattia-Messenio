package it.polimi.ingsw.client.lightGameModel;

import it.polimi.ingsw.network.dto.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



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

    public void addObserver(UIObserver observer) {
        this.observers.add(observer);
    }

    private void notifyUI() {
        for (UIObserver obs : observers) {
            obs.onStateChanged();
        }
    }

    public void setFullState(BoardDTO board, List<PlayerDTO> playersList) {
        this.upperRowCards.clear();
        this.upperRowCards.addAll(board.UpperRowCards());
        this.lowerRowCards.clear();
        this.lowerRowCards.addAll(board.LowerRowCards());
        this.currentEra = board.currentEra();
        this.currentRound = board.currentRound();

        this.players.clear();
        for (PlayerDTO p : playersList) {
            this.players.put(p.nickname(), new  LightPlayer(p));
            this.playerTribes.putIfAbsent(p.nickname(), new ArrayList<>());
        }
        notifyUI();
    }

    public void setAvailableActions(List<AvailableActionDTO> actions) {
        this.actions = actions;
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
        notifyUI();
    }

    public void updatePlayerResources(String nickname, int newFood, int newPrestige) {
        LightPlayer player = players.get(nickname);
        if (player != null) {
            player.setFood(newFood);
            player.setPrestige(newPrestige);
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
        notifyUI();
    }

    public void updatePlayerTribe(String nickname, List<Integer> newTribeCards) {
        playerTribes.put(nickname, new ArrayList<>(newTribeCards));
        notifyUI();
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
}