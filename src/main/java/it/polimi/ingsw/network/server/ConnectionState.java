package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.messages.InGameMessage;
import it.polimi.ingsw.network.messages.MatchmakingMessage;
import it.polimi.ingsw.network.messages.AfterGameMessage;

public interface ConnectionState {

    void handle(MatchmakingMessage message);

    void handle(InGameMessage message);

    void handle(AfterGameMessage message);

    void createGame(String nickname, int maxPlayers);

    void joinGame(String nickname, String gameId);

    void getAvailableGames();

    void leaveGame();

    void placeTotem(int positionIndex);

    void takeCard(int row, int col);

    void skipAction();

    void getLeaderboard();

    void handleDisconnection();
}
