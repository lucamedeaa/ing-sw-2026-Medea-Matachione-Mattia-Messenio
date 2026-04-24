package it.polimi.ingsw.client.model;

import it.polimi.ingsw.network.dto.AvailableActionDTO;
import it.polimi.ingsw.network.dto.BoardDTO;
import it.polimi.ingsw.network.dto.PlayerDTO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class LightGameModel {
    private final List<String> upperRowCards = new ArrayList<>();
    private final List<String> lowerRowCards = new ArrayList<>();
    private final Map<String, PlayerDTO> players = new HashMap<>();
    private final Map<String, Integer> playerTotemPositions = new HashMap<>();
    private final Map<String, List<String>> playerTribes = new HashMap<>();

    private List<AvailableActionDTO> actions = new ArrayList<>();
    private int currentEra = 1;
    private int currentRound = 1;

    private final List<UIObserver> observers = new ArrayList<>();

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
            this.players.put(p.nickname(), p);
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

    public void refillBoardRow(int row, List<String> newCardIds) {
        List<String> targetRow = (row == 0) ? upperRowCards : lowerRowCards;
        targetRow.clear();
        targetRow.addAll(newCardIds);
        notifyUI();
    }

    public void updateTotemPosition(String nickname, int positionIndex) {
        playerTotemPositions.put(nickname, positionIndex);
        notifyUI();
    }

    public void addCardToPlayerTribe(String nickname, String cardId) {
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



    // Getter che la TUI userà per disegnare la schermata
    public List<String> getUpperRowCards() { return new ArrayList<>(upperRowCards); }
    public List<String> getLowerRowCards() { return new ArrayList<>(lowerRowCards); }
    public Map<String, PlayerDTO> getPlayers() { return new HashMap<>(players); }
    public Map<String, Integer> getTotemPositions() { return new HashMap<>(playerTotemPositions); }
    public Map<String, List<String>> getTribes() { return new HashMap<>(playerTribes); }
    public int getCurrentEra() { return currentEra; }
    public int getCurrentRound() { return currentRound; }
    public List<AvailableActionDTO> getMyActions() { return new ArrayList<>(actions); }
}