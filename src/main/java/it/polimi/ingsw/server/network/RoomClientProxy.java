package it.polimi.ingsw.server.network;

import it.polimi.ingsw.server.controller.GameController;
import it.polimi.ingsw.server.leaderboard.LeaderboardService;

public interface RoomClientProxy extends ClientProxy {

    void transitionToGameState(GameController gameController);

    void transitionToAfterGameState(int playerCount, LeaderboardService leaderboardService);

    void transitionToLobby();
}
