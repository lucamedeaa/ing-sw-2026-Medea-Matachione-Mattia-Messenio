package it.polimi.ingsw.network.server;

public interface ConnectionState {

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
