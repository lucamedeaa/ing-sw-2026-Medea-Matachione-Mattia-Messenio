package it.polimi.ingsw.server.network;

import it.polimi.ingsw.server.controller.GameController;
import it.polimi.ingsw.server.model.exception.LobbyActionException;
import it.polimi.ingsw.server.leaderboard.LeaderboardService;

/**
 * Mutable connection context shared by server connection states.
 */
public interface ConnectionContext extends RoomClientProxy {

    @Override
    void transitionToGameState(GameController gameController);

    @Override
    void transitionToAfterGameState(int playerCount, LeaderboardService leaderboardService);

    /**
     * Stores the nickname currently associated with this connection.
     *
     * @param nickname nickname to store, or null to clear it without unregistering
     */
    void setNickname(String nickname);

    /**
     * Returns the nickname associated with this connection.
     *
     * @return current nickname, or null before matchmaking
     */
    String getNickname();

    /**
     * Indicates whether this connection is still active.
     *
     * @return true if the connection has not been closed
     */
    boolean isActive();

    @Override
    void transitionToLobby();

    /**
     * Clears and unregisters the current nickname, if present.
     */
    void clearNickname();

    /**
     * Executes an operation while holding the connection lifecycle lock.
     *
     * @param operation operation to execute
     * @param <T> result type
     * @return operation result
     * @throws LobbyActionException if the operation fails in lobby logic
     */
    <T> T withConnectionLock(LockedConnectionOperation<T> operation) throws LobbyActionException;

    /**
     * Operation that must run under the connection lifecycle lock.
     *
     * @param <T> operation result type
     */
    @FunctionalInterface
    interface LockedConnectionOperation<T> {
        /**
         * Runs the locked operation.
         *
         * @return operation result
         * @throws LobbyActionException if lobby logic rejects the operation
         */
        T run() throws LobbyActionException;
    }

}
