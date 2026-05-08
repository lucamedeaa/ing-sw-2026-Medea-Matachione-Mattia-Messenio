package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.server.exceptions.LobbyActionException;
import it.polimi.ingsw.server.leaderboard.LeaderboardService;

public interface ConnectionContext extends RoomClientProxy {

    @Override
    void transitionToGameState(GameController gameController);

    @Override
    void transitionToAfterGameState(int playerCount, LeaderboardService leaderboardService);

    void setNickname(String nickname);

    String getNickname();

    boolean isActive();

    @Override
    void transitionToLobby();

    void clearNickname();

    <T> T withConnectionLock(LockedConnectionOperation<T> operation) throws LobbyActionException;

    @FunctionalInterface
    interface LockedConnectionOperation<T> {
        T run() throws LobbyActionException;
    }

}
