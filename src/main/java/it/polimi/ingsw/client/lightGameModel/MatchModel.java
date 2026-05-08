package it.polimi.ingsw.client.lightGameModel;

import it.polimi.ingsw.network.dto.*;
import java.util.*;

public class MatchModel extends ObservableModel {

    // Composizione dei sotto-stati
    private BoardState board = new BoardState();
    private RosterState roster = new RosterState();
    private TurnState turn = new TurnState();

    // End Game State (rimane qui perché è statico e terminale)
    private boolean isGameOver = false;
    private List<PlayerScoreDTO> leaderboard = new ArrayList<>();
    private List<String> winners = new ArrayList<>();
    private String abortReason = null;
    private PlayerGameCompletedDTO localResult = null;
    private LeaderboardSnapshot globalLeaderboard = null;

    public void setFullState(BoardDTO boardDTO, List<PlayerDTO> playersList, String activePlayer) {
        board.setCards(boardDTO.UpperRowCards(), boardDTO.LowerRowCards());
        board.setEra(boardDTO.currentEra());
        board.setRound(boardDTO.currentRound());
        roster.initPlayers(playersList);
        turn.setActivePlayer(activePlayer);
        notifyUI();
    }

    public void setAvailableActions(List<AvailableActionDTO> actions) {
        turn.setActions(actions);
        notifyUI();
    }

    public void setActivePlayer(String activePlayer) {
        turn.setActivePlayer(activePlayer);
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
        roster.updatePlayerResources(nickname, newFood, newPrestige, newFoodDiscount);
        notifyUI();
    }

    public void addGameLog(String log) {
        turn.addGameLog(log);
        notifyUI();
    }

    public List<String> consumeGameLogs() {

        return turn.consumeGameLogs();
    }


    public void reset() {
        this.board = new BoardState();
        this.roster = new RosterState();
        this.turn = new TurnState();

        this.isGameOver = false;
        this.leaderboard = new ArrayList<>();
        this.winners = new ArrayList<>();
        this.abortReason = null;
        this.localResult = null;
        this.globalLeaderboard = null;
    }

    //  END GAME SETTERS

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
        this.leaderboard = roster.getPlayers().values().stream()
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
        endBatch(); // Chiude il batch iniziato in GameEndedState
    }

    public List<Integer> getUpperRowCards() { return board.getUpperRowCards(); }
    public List<Integer> getLowerRowCards() { return board.getLowerRowCards(); }
    public int getCurrentEra() { return board.getCurrentEra(); }
    public int getCurrentRound() { return board.getCurrentRound(); }

    public Map<String, LightPlayer> getPlayers() { return roster.getPlayers(); }
    public Map<String, Integer> getTotemPositions() { return roster.getTotemPositions(); }
    public Map<String, Integer> getReturnPositions() { return roster.getReturnPositions(); }
    public Map<String, List<Integer>> getTribes() { return roster.getTribes(); }

    public String getActivePlayer() { return turn.getActivePlayer(); }
    public List<AvailableActionDTO> getMyActions() { return turn.getActions(); }

    public PlayerGameCompletedDTO getLocalResult() { return localResult; }
    public LeaderboardSnapshot getGlobalLeaderboard() { return globalLeaderboard; }
    public String getAbortReason() { return abortReason; }
    public boolean isGameOver() { return isGameOver; }
    public List<PlayerScoreDTO> getLeaderboard() { return new ArrayList<>(leaderboard); }
    public List<String> getWinners() { return new ArrayList<>(winners); }
}