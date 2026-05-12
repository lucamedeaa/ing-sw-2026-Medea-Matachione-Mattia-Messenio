package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.model.snapshot.BoardSnapshot;
import it.polimi.ingsw.client.model.snapshot.PlayerSnapshot;
import it.polimi.ingsw.client.model.snapshot.RosterSnapshot;
import it.polimi.ingsw.client.model.snapshot.TurnSnapshot;
import it.polimi.ingsw.common.network.dto.*;
import it.polimi.ingsw.common.network.dto.action.ActionDto;
import it.polimi.ingsw.client.model.snapshot.PlayerResources;
import java.util.*;

/**
 * Client-side projection of the game state received from the server.
 */
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

    private boolean pendingDeltaReset = false;

    /**
     * Replaces the local state with a full server snapshot.
     *
     * @param boardDTO board snapshot
     * @param playersList player snapshots
     * @param activePlayer nickname of the active player
     */
    public void setFullState(BoardDto boardDTO, List<PlayerDto> playersList, String activePlayer) {
        lock.writeLock().lock();
        try {
            board.setCards(boardDTO.UpperRowCards(), boardDTO.LowerRowCards());
            board.setEra(boardDTO.currentEra());
            board.setRound(boardDTO.currentRound());
            roster.initPlayers(playersList);
            turn.setActivePlayer(activePlayer);
        } finally {
            lock.writeLock().unlock();
        }
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
            turn.setActivePlayer(activePlayer);
        } finally {
            lock.writeLock().unlock();
        }
        notifyUI();
    }

    /**
     * Marks accumulated turn deltas to be cleared before the next totem placement is applied.
     */
    public void scheduleDeltaReset() {
        lock.writeLock().lock();
        try {
            this.pendingDeltaReset = true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Clears accumulated turn deltas if a reset was previously scheduled.
     */
    public void executePendingDeltaReset() {
        lock.writeLock().lock();
        try {
            if (pendingDeltaReset) {
                turnDeltas.clear();
                pendingDeltaReset = false;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removeCard(int row, int col) {
        lock.writeLock().lock();
        try {
            board.removeCard(row, col);
        } finally {
            lock.writeLock().unlock();
        }
        notifyUI();
    }

    public void refillBoardRow(int row, List<Integer> newCardIds) {
        lock.writeLock().lock();
        try {
            board.refillRow(row, newCardIds);
        }finally {
            lock.writeLock().unlock();
        }
        notifyUI();
    }

    public void updateTotemPosition(String nickname, int positionIndex) {
        lock.writeLock().lock();
        try{
            roster.updateTotemPosition(nickname, positionIndex);
        }finally {
            lock.writeLock().unlock();
        }
        notifyUI();
    }

    public void returnTotemToTrack(String nickname, int returnIndex) {
        lock.writeLock().lock();
        try {
            roster.returnTotemToTrack(nickname, returnIndex);
        }finally {
            lock.writeLock().unlock();
        }
        notifyUI();
    }

    public void addCardToPlayerTribe(String nickname, Integer cardId) {
        lock.writeLock().lock();
        try{
            roster.addCardToTribe(nickname, cardId);
        }finally {
            lock.writeLock().unlock();
        }
        notifyUI();
    }

    public void updatePlayerTribe(String nickname, List<Integer> newTribeCards) {
        lock.writeLock().lock();
        try {
            roster.updateTribe(nickname, newTribeCards);
        }finally {
            lock.writeLock().unlock();
        }
        notifyUI();
    }

    public void updateEra(int newEra) {
        lock.writeLock().lock();
        try{
            board.setEra(newEra);
        }finally {
            lock.writeLock().unlock();
        }
        notifyUI();
    }

    public void updateRound(int newRound) {
        lock.writeLock().lock();
        try{
            board.setRound(newRound);
            roster.clearReturnPositions();
        }finally {
            lock.writeLock().unlock();
        }
        notifyUI();
    }

    /**
     * Updates a player's resources and accumulates the change for turn-delta rendering.
     *
     * @param nickname player to update
     * @param newFood updated food amount
     * @param newPrestige updated prestige amount
     * @param newFoodDiscount updated permanent food discount
     * @param newSustDiscount updated Sustenance discount
     */
    public void updatePlayerResources(String nickname, int newFood, int newPrestige, int newFoodDiscount, int newSustDiscount) {
        lock.writeLock().lock();
        try {
            PlayerSnapshot old = roster.getPlayers().get(nickname);
            if (old != null) {
                PlayerResources currentDelta = turnDeltas.getOrDefault(nickname, new PlayerResources(0, 0, 0, 0));
                turnDeltas.put(nickname, new PlayerResources(
                        currentDelta.food() + (newFood - old.getFood()),
                        currentDelta.prestige() + (newPrestige - old.getPrestige()),
                        currentDelta.discount() + (newFoodDiscount - old.getFoodDiscount()),
                        currentDelta.sustenanceDiscount() + (newSustDiscount - old.getSustenanceDiscount())
                ));
                old.setFood(newFood);
                old.setPrestige(newPrestige);
                old.setFoodDiscount(newFoodDiscount);
                old.setSustenanceDiscount(newSustDiscount);
            }
        } finally {
            lock.writeLock().unlock();
        }
        notifyUI();
    }
    /**
     * Returns resource deltas accumulated during the visible turn.
     *
     * @return copy of deltas by nickname
     */
    public Map<String, PlayerResources> getTurnDeltas() {
        lock.readLock().lock();
        try {
            return new HashMap<>(turnDeltas);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void addGameLog(String log) {
        lock.writeLock().lock();
        try {
            turn.addGameLog(log);
        } finally {
            lock.writeLock().unlock();
        }
        notifyUI();
    }

    public List<String> consumeGameLogs() {
        lock.writeLock().lock();
        try {
            return turn.consumeGameLogs();
        } finally {
            lock.writeLock().unlock();
        }
    }


    /**
     * Clears all cached game, turn, and end-game state.
     */
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

    public List<Integer> getUpperRowCards() {
        lock.readLock().lock();
        try {
            return board.getUpperRowCards();
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Integer> getLowerRowCards() {
        lock.readLock().lock();
        try {
            return board.getLowerRowCards();
        } finally {
            lock.readLock().unlock();
        }
    }

    public int getCurrentEra() {
        lock.readLock().lock();
        try {
            return board.getCurrentEra();
        } finally {
            lock.readLock().unlock();
        }
    }

    public int getCurrentRound() {
        lock.readLock().lock();
        try {
            return board.getCurrentRound();
        } finally {
            lock.readLock().unlock();
        }
    }

    public Map<String, PlayerSnapshot> getPlayers() {
        lock.readLock().lock();
        try {
            return roster.getPlayers();
        } finally {
            lock.readLock().unlock();
        }
    }

    public Map<String, Integer> getTotemPositions() {
        lock.readLock().lock();
        try {
            return roster.getTotemPositions();
        } finally {
            lock.readLock().unlock();
        }
    }

    public Map<String, Integer> getReturnPositions() {
        lock.readLock().lock();
        try {
            return roster.getReturnPositions();
        } finally {
            lock.readLock().unlock();
        }
    }

    public Map<String, List<Integer>> getTribes() {
        lock.readLock().lock();
        try {
            return roster.getTribes();
        } finally {
            lock.readLock().unlock();
        }
    }

    public String getActivePlayer() {
        lock.readLock().lock();
        try {
            return turn.getActivePlayer();
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<ActionDto> getMyActions() {
        lock.readLock().lock();
        try {
            return turn.getActions();
        } finally {
            lock.readLock().unlock();
        }
    }

    public PlayerGameCompletedDto getLocalResult() {
        lock.readLock().lock();
        try {
            return localResult;
        } finally {
            lock.readLock().unlock();
        }
    }

    public LeaderboardSnapshotDto getGlobalLeaderboard() {
        lock.readLock().lock();
        try {
            return globalLeaderboard;
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean isGameOver() {
        lock.readLock().lock();
        try {
            return isGameOver;
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<PlayerScoreDto> getLeaderboard() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(leaderboard);
        } finally {
            lock.readLock().unlock();
        }
    }
}