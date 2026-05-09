package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.model.snapshot.BoardSnapshot;
import it.polimi.ingsw.client.model.snapshot.PlayerSnapshot;
import it.polimi.ingsw.client.model.snapshot.RosterSnapshot;
import it.polimi.ingsw.client.model.snapshot.TurnSnapshot;
import it.polimi.ingsw.common.network.dto.*;
import it.polimi.ingsw.common.network.dto.action.ActionDto;
import it.polimi.ingsw.client.view.tui.state.InGameUiState.PlayerResources;

import java.util.*;

public class GameModel extends ObservableModel {

    // Composizione dei sotto-stati
    private BoardSnapshot board = new BoardSnapshot();
    private RosterSnapshot roster = new RosterSnapshot();
    private TurnSnapshot turn = new TurnSnapshot();

    // End Game State (rimane qui perché è statico e terminale)
    private boolean isGameOver = false;
    private List<PlayerScoreDto> leaderboard = new ArrayList<>();
    private List<String> winners = new ArrayList<>();
    private String abortReason = null;
    private PlayerGameCompletedDto localResult = null;
    private LeaderboardSnapshotDto globalLeaderboard = null;
    private final Map<String, PlayerResources> turnDeltas = new HashMap<>();

    public void setFullState(BoardDto boardDTO, List<PlayerDto> playersList, String activePlayer) {
        board.setCards(boardDTO.UpperRowCards(), boardDTO.LowerRowCards());
        board.setEra(boardDTO.currentEra());
        board.setRound(boardDTO.currentRound());
        roster.initPlayers(playersList);
        turn.setActivePlayer(activePlayer);
        notifyUI();
    }

    public void setAvailableActions(List<ActionDto> actions) {
        lock.writeLock().lock();
        try {
            turn.setActions(actions);
        } finally {
            lock.writeLock().unlock();
        }
        notifyUI();

    }

    public void setActivePlayer(String activePlayer) {
        lock.writeLock().lock();
        try {
            if (activePlayer != null && !activePlayer.equals(turn.getActivePlayer())) {
                turnDeltas.remove(activePlayer);
            }
        }finally {
            lock.writeLock().unlock();
        }
        notifyUI();

    }

    public void removeCard(int row, int col) {
        board.removeCard(row, col);
        notifyUI();
    }

    public void refillBoardRow(int row, List<Integer> newCardIds) {
        board.refillRow(row, newCardIds);
        notifyUI();
    }

    public void updateTotemPosition(String nickname, int positionIndex) {
        roster.updateTotemPosition(nickname, positionIndex);
        notifyUI();
    }

    public void returnTotemToTrack(String nickname, int returnIndex) {
        roster.returnTotemToTrack(nickname, returnIndex);
        notifyUI();
    }

    public void addCardToPlayerTribe(String nickname, Integer cardId) {
        roster.addCardToTribe(nickname, cardId);
        notifyUI();
    }

    public void updatePlayerTribe(String nickname, List<Integer> newTribeCards) {
        roster.updateTribe(nickname, newTribeCards);
        notifyUI();
    }

    public void updateEra(int newEra) {
        board.setEra(newEra);
        notifyUI();
    }

    public void updateRound(int newRound) {
        board.setRound(newRound);
        roster.clearReturnPositions();
        notifyUI();
    }

    public void updatePlayerResources(String nickname, int newFood, int newPrestige, int newFoodDiscount) {
        lock.writeLock().lock();
        try {
            PlayerSnapshot old = roster.getPlayers().get(nickname);
            if (old != null) {
                PlayerResources currentDelta = turnDeltas.getOrDefault(nickname, new PlayerResources(0, 0, 0));
                turnDeltas.put(nickname, new PlayerResources(
                        currentDelta.food() + (newFood - old.getFood()),
                        currentDelta.prestige() + (newPrestige - old.getPrestige()),
                        currentDelta.discount() + (newFoodDiscount - old.getFoodDiscount())
                ));
            }
            roster.updatePlayerResources(nickname, newFood, newPrestige, newFoodDiscount);
        } finally {
            lock.writeLock().unlock();
        }
        notifyUI();
    }
    public Map<String, PlayerResources> getTurnDeltas() {
        return new HashMap<>(turnDeltas);
    }
    public void clearTurnDeltas() {
        lock.writeLock().lock();
        try {
            turnDeltas.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }


    public void addGameLog(String log) {
        turn.addGameLog(log);
        notifyUI();
    }

    public List<String> consumeGameLogs() {

        return turn.consumeGameLogs();
    }


    public void reset() {
        lock.writeLock().lock();
        try {
            this.board = new BoardSnapshot();
            this.roster = new RosterSnapshot();
            this.turn = new TurnSnapshot();

            this.turnDeltas.clear();

            this.isGameOver = false;
            this.leaderboard = new ArrayList<>();
            this.winners = new ArrayList<>();
            this.abortReason = null;
            this.localResult = null;
            this.globalLeaderboard = null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    //  END GAME SETTERS

    public void setGameAborted(String reason) {
        lock.writeLock().lock();
        try {
            this.abortReason = reason;
        }finally {
            lock.writeLock().unlock();
        }
        notifyUI();

    }

    public void setGameOver(List<PlayerScoreDto> leaderboard) {
        lock.writeLock().lock();
        try {
            this.isGameOver = true;
            this.leaderboard = new ArrayList<>(leaderboard);
        }finally {
            lock.writeLock().unlock();
        }
        notifyUI();

    }

    public void setWinners(List<String> winners) {
        lock.writeLock().lock();
        try {
            this.winners = new ArrayList<>(winners);
            this.leaderboard = roster.getPlayers().values().stream()
                    .sorted(Comparator.comparingInt(PlayerSnapshot::getPrestige).reversed())
                    .map(p -> new PlayerScoreDto(p.getNickname(), p.getPrestige(), p.getFood()))
                    .toList();
            this.isGameOver = true;
        }finally {
            lock.writeLock().unlock();
        }
        notifyUI();

    }

    public void setGameCompleted(PlayerGameCompletedDto result) {
        lock.writeLock().lock();
        try {
            this.localResult = result;
        }finally {
            lock.writeLock().unlock();
        }
        notifyUI();

    }

    public void setGlobalLeaderboard(LeaderboardSnapshotDto snapshot) {
        lock.writeLock().lock();
        try {
            this.globalLeaderboard = snapshot;
        }finally {
            lock.writeLock().unlock();
        }
        notifyUI();
    }

    public List<Integer> getUpperRowCards() { return board.getUpperRowCards(); }
    public List<Integer> getLowerRowCards() { return board.getLowerRowCards(); }
    public int getCurrentEra() { return board.getCurrentEra(); }
    public int getCurrentRound() { return board.getCurrentRound(); }

    public Map<String, PlayerSnapshot> getPlayers() { return roster.getPlayers(); }
    public Map<String, Integer> getTotemPositions() { return roster.getTotemPositions(); }
    public Map<String, Integer> getReturnPositions() { return roster.getReturnPositions(); }
    public Map<String, List<Integer>> getTribes() { return roster.getTribes(); }

    public String getActivePlayer() { return turn.getActivePlayer(); }
    public List<ActionDto> getMyActions() { return turn.getActions(); }

    public PlayerGameCompletedDto getLocalResult() { return localResult; }
    public LeaderboardSnapshotDto getGlobalLeaderboard() { return globalLeaderboard; }
    public String getAbortReason() { return abortReason; }
    public boolean isGameOver() { return isGameOver; }
    public List<PlayerScoreDto> getLeaderboard() { return new ArrayList<>(leaderboard); }
    public List<String> getWinners() { return new ArrayList<>(winners); }
}