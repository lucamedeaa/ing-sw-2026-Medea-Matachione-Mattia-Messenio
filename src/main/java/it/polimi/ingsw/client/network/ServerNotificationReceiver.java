package it.polimi.ingsw.client.network;

import it.polimi.ingsw.common.network.dto.action.ActionDto;
import it.polimi.ingsw.common.network.dto.BoardDto;
import it.polimi.ingsw.common.network.dto.event.GameEventDto;
import it.polimi.ingsw.common.network.dto.LeaderboardSnapshotDto;
import it.polimi.ingsw.common.network.dto.PlayerDto;
import it.polimi.ingsw.common.network.dto.PlayerGameCompletedDto;
import it.polimi.ingsw.common.network.dto.GameInfoDto;

import java.util.List;

/**
 * Receives server notifications after transport-specific decoding.
 */
public interface ServerNotificationReceiver {

    /**
     * Receives a full game-state snapshot.
     *
     * @param board board state
     * @param players player states
     * @param activePlayer active player nickname, or null when no action is pending
     * @param actions actions available to the active player
     */
    void fullSync(BoardDto board, List<PlayerDto> players, String activePlayer, List<ActionDto> actions);

    /**
     * Receives incremental game events and the next turn state.
     *
     * @param events ordered events to apply locally
     * @param nextActions actions available after the events
     * @param activePlayer active player nickname after the events
     */
    void deltaEvent(List<GameEventDto> events, List<ActionDto> nextActions, String activePlayer);

    /**
     * Receives a server-side error message.
     *
     * @param error error text
     */
    void error(String error);

    /**
     * Receives confirmation that matchmaking succeeded.
     *
     * @param text user-facing confirmation text
     */
    void matchmakingSuccess(String text);

    /**
     * Receives the list of available games.
     *
     * @param games joinable game summaries
     */
    void availableGames(List<GameInfoDto> games);

    /**
     * Receives a game-aborted notification.
     *
     * @param reason abort reason
     */
    void gameAborted(String reason);

    /**
     * Receives an update for the current room.
     *
     * @param notification user-facing room message
     * @param currentPlayers nicknames currently in the room
     */
    void roomUpdate(String notification, List<String> currentPlayers);

    /**
     * Receives confirmation that the client left the current game context.
     *
     * @param text user-facing confirmation text
     */
    void gameLeftSuccess(String text);

    /**
     * Receives the local player's completed-game result.
     *
     * @param completedGame completed-game result for the local player
     */
    void gameCompleted(PlayerGameCompletedDto completedGame);

    /**
     * Receives a leaderboard snapshot.
     *
     * @param leaderboard leaderboard data
     */
    void leaderboard(LeaderboardSnapshotDto leaderboard);

    /**
     * Receives a local transport disconnection notification.
     *
     * @param reason disconnection reason
     */
    void serverDisconnected(String reason);
}
