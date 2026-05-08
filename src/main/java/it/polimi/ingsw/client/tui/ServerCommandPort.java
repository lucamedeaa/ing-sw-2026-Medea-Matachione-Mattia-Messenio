package it.polimi.ingsw.client.tui;

public interface ServerCommandPort {
    void createGame(String nickname, int maxPlayers);
    void joinGame(String nickname, String gameId);
    void getAvailableGames();
    void leaveGame();
    void placeTotem(int posIdx);
    void takeCard(int row, int col);
    void skipAction();
    void getLeaderboard();
    void disconnect(Runnable completionCallback);
}