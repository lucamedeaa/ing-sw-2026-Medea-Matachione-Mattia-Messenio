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

    private BoardSnapshot board = new BoardSnapshot();
    private RosterSnapshot roster = new RosterSnapshot();
    private TurnSnapshot turn = new TurnSnapshot();

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

            board.setCards(boardDTO.UpperRowCards(), boardDTO.LowerRowCards());
            board.setEra(boardDTO.currentEra());
            board.setRound(boardDTO.currentRound());
            board.setNextDeckEra(boardDTO.nextDeckEra());
            roster.initPlayers(playersList);
            turn.setActivePlayerSnap(activePlayer);

        notifyUI();
    }

    /**
     * Sets the available actions.
     *
     * @param actions available actions
     */
    public void setAvailableActions(List<ActionDto> actions) {

            turn.setActions(actions);

        notifyUI();

    }

    /**
     * Sets the active player.
     *
     * @param activePlayer active player nickname
     */
    public void setActivePlayer(String activePlayer) {

            turn.setActivePlayerSnap(activePlayer);

        notifyUI();
    }

    /**
     * Marks accumulated turn deltas to be cleared before the next totem placement is applied.
     */
    public void scheduleDeltaReset() {

            this.pendingDeltaReset = true;

    }

    /**
     * Clears accumulated turn deltas if a reset was previously scheduled.
     */
    public void executePendingDeltaReset() {

            if (pendingDeltaReset) {
                turnDeltas.clear();
                pendingDeltaReset = false;
            }

    }

    /**
     * Removes the card.
     *
     * @param row board row index
     * @param col card column index
     */
    public void removeCard(int row, int col) {

            board.removeCard(row, col);

        notifyUI();
    }

    /**
     * Replaces one board row with freshly drawn card identifiers.
     *
     * @param row board row index
     * @param newCardIds replacement card identifiers
     */
    public void refillBoardRow(int row, List<Integer> newCardIds) {

            board.refillRow(row, newCardIds);

        notifyUI();
    }

    /**
     * Updates the totem position.
     *
     * @param nickname player nickname
     * @param positionIndex offer tile position index
     */
    public void updateTotemPosition(String nickname, int positionIndex) {

            roster.updateTotemPosition(nickname, positionIndex);

        notifyUI();
    }

    /**
     * Initializes totem return-track positions from the turn-order tile.
     *
     * @param turnOrderTile initial turn-order tile
     */
    public void setInitTotemPosition(InitTurnOrderTileDto turnOrderTile) {
        List<String> players = turnOrderTile.nickPlayers();
        for(int i = 0; i < players.size(); i++) {
            roster.returnTotemToTrack(players.get(i), i);
        }
        notifyUI();
    }

    /**
     * Moves a player's totem back to the return track.
     *
     * @param nickname player nickname
     * @param returnIndex totem return-track index
     */
    public void returnTotemToTrack(String nickname, int returnIndex) {

            roster.returnTotemToTrack(nickname, returnIndex);

        notifyUI();
    }

    /**
     * Adds a card to a player's tribe.
     *
     * @param nickname player nickname
     * @param cardId card identifier
     */
    public void addCardToPlayerTribe(String nickname, Integer cardId) {

            roster.addCardToTribe(nickname, cardId);

        notifyUI();
    }

    /**
     * Updates the player tribe.
     *
     * @param nickname player nickname
     * @param newTribeCards replacement tribe card identifiers
     */
    public void updatePlayerTribe(String nickname, List<Integer> newTribeCards) {

            roster.updateTribe(nickname, newTribeCards);

        notifyUI();
    }

    /**
     * Updates the era.
     *
     * @param newEra new era
     */
    public void updateEra(int newEra) {

            board.setEra(newEra);

        notifyUI();
    }

    /**
     * Updates the round.
     *
     * @param newRound new round
     */
    public void updateRound(int newRound) {

            board.setRound(newRound);

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
                roster.updatePlayerResources(nickname, newFood, newPrestige, newFoodDiscount, newSustDiscount);
            }

        notifyUI();
    }
    /**
     * Returns resource deltas accumulated during the visible turn.
     *
     * @return copy of deltas by nickname
     */
    public Map<String, PlayerResources> getTurnDeltas() {

            return new HashMap<>(turnDeltas);

    }

    /**
     * Adds the game log.
     *
     * @param log log message
     */
    public void addGameLog(String log) {

            turn.addGameLog(log);

        notifyUI();
    }

    /**
     * Consumes the game logs.
     *
     * @return pending log lines, cleared from the model
     */
    public List<String> consumeGameLogs() {

            return turn.consumeGameLogs();

    }


    /**
     * Clears all cached game, turn, and end-game state.
     */
    public void reset() {

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

    }

    //  END GAME SETTERS

    /**
     * Sets the game aborted.
     *
     * @param reason reason text
     */
    public void setGameAborted(String reason) {

            this.abortReason = reason;

        notifyUI();

    }

    /**
     * Sets the game over.
     *
     * @param leaderboard leaderboard snapshot
     */
    public void setGameOver(List<PlayerScoreDto> leaderboard) {

            this.isGameOver = true;
            this.leaderboard = new ArrayList<>(leaderboard);

        notifyUI();

    }

    /**
     * Sets the winners.
     *
     * @param winners winners
     */
    public void setWinners(List<String> winners) {

            this.winners = new ArrayList<>(winners);
            this.leaderboard = roster.getPlayers().values().stream()
                    .sorted(Comparator.comparingInt(PlayerSnapshot::getPrestige).reversed())
                    .map(p -> new PlayerScoreDto(p.getNickname(), p.getPrestige(), p.getFood()))
                    .toList();
            this.isGameOver = true;

        notifyUI();

    }

    /**
     * Stores the local completed-game result.
     *
     * @param result completed game result for the local player
     */
    public void setGameCompleted(PlayerGameCompletedDto result) {

            this.localResult = result;

        notifyUI();

    }

    /**
     * Stores the latest global leaderboard snapshot.
     *
     * @param snapshot leaderboard snapshot
     */
    public void setGlobalLeaderboard(LeaderboardSnapshotDto snapshot) {

            this.globalLeaderboard = snapshot;

        notifyUI();
    }

    /**
     * Returns the upper row cards.
     *
     * @return the upper row cards
     */
    public List<Integer> getUpperRowCards() {

            return board.getUpperRowCards();

    }

    /**
     * Returns the lower row cards.
     *
     * @return the lower row cards
     */
    public List<Integer> getLowerRowCards() {

            return board.getLowerRowCards();

    }

    /**
     * Returns the current era.
     *
     * @return the current era
     */
    public int getCurrentEra() {

            return board.getCurrentEra();

    }

    /**
     * Returns the current round.
     *
     * @return the current round
     */
    public int getCurrentRound() {

            return board.getCurrentRound();

    }

    /**
     * Returns the next deck era.
     *
     * @return the next deck era
     */
    public Integer getNextDeckEra() {

            return board.getNextDeckEra();

    }

    /**
     * Sets the next deck era.
     *
     * @param era next deck era
     */
    public void setNextDeckEra(Integer era) {

            board.setNextDeckEra(era);

        notifyUI();
    }

    /**
     * Returns the players.
     *
     * @return the current player nicknames
     */
    public Map<String, PlayerSnapshot> getPlayers() {

            return roster.getPlayers();

    }

    /**
     * Returns the totem positions.
     *
     * @return the totem positions
     */
    public Map<String, Integer> getTotemPositions() {

            return roster.getTotemPositions();

    }

    /**
     * Returns the return positions.
     *
     * @return the return positions
     */
    public Map<String, Integer> getReturnPositions() {

            return roster.getReturnPositions();

    }

    /**
     * Returns the tribes.
     *
     * @return the tribes
     */
    public Map<String, List<Integer>> getTribes() {

            return roster.getTribes();

    }

    /**
     * Returns the active player.
     *
     * @return the active player
     */
    public String getActivePlayer() {

            return turn.getActivePlayer();

    }

    /**
     * Returns the my actions.
     *
     * @return the my actions
     */
    public List<ActionDto> getMyActions() {

            return turn.getActions();

    }

    /**
     * Returns the local result.
     *
     * @return the local result
     */
    public PlayerGameCompletedDto getLocalResult() {

            return localResult;

    }

    /**
     * Returns the global leaderboard.
     *
     * @return the global leaderboard
     */
    public LeaderboardSnapshotDto getGlobalLeaderboard() {

            return globalLeaderboard;

    }

    /**
     * Returns whether game over.
     *
     * @return true if game over; false otherwise
     */
    public boolean isGameOver() {

            return isGameOver;

    }

    /**
     * Returns the leaderboard.
     *
     * @return the leaderboard
     */
    public List<PlayerScoreDto> getLeaderboard() {

            return new ArrayList<>(leaderboard);

    }
}
