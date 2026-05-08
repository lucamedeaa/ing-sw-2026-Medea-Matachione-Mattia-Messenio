package it.polimi.ingsw.server.network;

import it.polimi.ingsw.server.controller.GameController;
import it.polimi.ingsw.server.model.exception.LobbyActionException;
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
