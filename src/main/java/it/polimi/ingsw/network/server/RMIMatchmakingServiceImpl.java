package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.messages.GameInfoDTO;
import it.polimi.ingsw.network.rmi.RMIClientCallback;
import it.polimi.ingsw.network.rmi.RMIMatchmakingService;
import it.polimi.ingsw.network.rmi.RMIServerSession;
import it.polimi.ingsw.server.GameManager;
import it.polimi.ingsw.server.GameRoom;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/** RMI implementation of the matchmaking service, managing game creation and joining via GameManager. */
public class RMIMatchmakingServiceImpl extends UnicastRemoteObject implements RMIMatchmakingService {

    private final GameManager gameManager;

    /** Constructs the matchmaking service. @param gameManager game manager instance @throws RemoteException if export fails */
    public RMIMatchmakingServiceImpl(GameManager gameManager) throws RemoteException {
        super();
        this.gameManager = gameManager;
    }

    /** Returns the list of available games. @return list of game info DTOs @throws RemoteException if communication fails */
    @Override
    public List<GameInfoDTO> getAvailableGames() throws RemoteException {
        return gameManager.getAvailableGames();
    }

    /** Creates a new game, registers the player, and returns the associated RMI session. @param nickname player nickname @param maxPlayers maximum number of players @param callback client callback @return server session @throws RemoteException if creation fails */
    @Override
    public RMIServerSession createGame(String nickname, int maxPlayers, RMIClientCallback callback) throws RemoteException {
        try {
            String newGameId = gameManager.createNewGame(nickname, maxPlayers);
            GameRoom room = gameManager.getGame(newGameId);
            RMIClientHandler handler = new RMIClientHandler(callback, nickname);
            room.addPlayer(nickname, handler);
            return handler;
        } catch (Exception e) {
            throw new RemoteException("Unable to create game: " + e.getMessage());
        }
    }

    /** Joins an existing game and returns the associated RMI session. @param gameId game identifier @param nickname player nickname @param callback client callback @return server session @throws RemoteException if validation fails or communication errors occur */
    @Override
    public RMIServerSession joinGame(String gameId, String nickname, RMIClientCallback callback) throws RemoteException {
        GameRoom room = gameManager.getGame(gameId);

        if (room == null) throw new RemoteException("Game not found.");
        if (room.isFull()) throw new RemoteException("Game is full.");
        if (room.isGameStarted()) throw new RemoteException("Game already started.");
        if (room.isNicknameTaken(nickname)) throw new RemoteException("Nickname already in use.");

        RMIClientHandler handler = new RMIClientHandler(callback, nickname);
        room.addPlayer(nickname, handler);

        return handler;
    }
}