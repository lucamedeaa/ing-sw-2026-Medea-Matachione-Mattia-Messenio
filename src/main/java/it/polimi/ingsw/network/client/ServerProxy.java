package it.polimi.ingsw.network.client;

public interface ServerProxy {

    void createGame(String nickname, int maxPlayers);

    void joinGame(String nickname, String gameId);

    void getAvailableGames();

    void leaveGame();

    void placeTotem(int positionIndex);

    void takeCard(int row, int col);

    void skipAction();

    void getLeaderboard();

    void disconnect();
}
