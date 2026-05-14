package it.polimi.ingsw.server.network;

import it.polimi.ingsw.common.network.dto.*;
import it.polimi.ingsw.common.network.dto.action.ActionDto;
import it.polimi.ingsw.common.network.dto.event.GameEventDto;

import java.util.List;

/**
 * Server-side abstraction for delivering notifications to a connected client.
 */
public interface ClientProxy {

    /**
     * Sends a complete game snapshot.
     *
     * @param board         board state
     * @param players       player states
     * @param activePlayer  active player nickname, or null if no action is pending
     * @param actions       actions available to the active player
     * @param turnOrderTile
     */
    void fullSync(BoardDto board, List<PlayerDto> players, String activePlayer, List<ActionDto> actions, InitTurnOrderTileDto turnOrderTile);

    /**
     * Sends incremental game events and the next active-player state.
     *
     * @param events events to apply
     * @param nextActions actions available after applying events
     * @param activePlayer active player nickname after applying events
     */
    void deltaEvent(List<GameEventDto> events, List<ActionDto> nextActions, String activePlayer);

    /**
     * Sends an error message.
     *
     * @param error error text
     */
    void error(String error);

    /**
     * Sends matchmaking confirmation.
     *
     * @param text confirmation text
     */
    void matchmakingSuccess(String text);

    /**
     * Sends joinable game summaries.
     *
     * @param games available games
     */
    void availableGames(List<GameInfoDto> games);

    /**
     * Sends a game-aborted notification.
     *
     * @param reason abort reason
     */
    void gameAborted(String reason);

    /**
     * Sends a room roster update.
     *
     * @param notification room notification text
     * @param currentPlayers current room players
     */
    void roomUpdate(String notification, List<String> currentPlayers);

    /**
     * Sends confirmation that the client left the current game context.
     *
     * @param text confirmation text
     */
    void gameLeftSuccess(String text);

    /**
     * Sends the local completed-game result.
     *
     * @param completedGame completed-game result
     */
    void gameCompleted(PlayerGameCompletedDto completedGame);

    /**
     * Sends a leaderboard snapshot.
     *
     * @param leaderboard leaderboard data
     */
    void leaderboard(LeaderboardSnapshotDto leaderboard);
}
