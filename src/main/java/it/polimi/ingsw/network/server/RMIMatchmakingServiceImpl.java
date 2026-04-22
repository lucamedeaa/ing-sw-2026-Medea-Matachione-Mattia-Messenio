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

public class RMIMatchmakingServiceImpl extends UnicastRemoteObject implements RMIMatchmakingService {

    private final GameManager gameManager;

    public RMIMatchmakingServiceImpl(GameManager gameManager) throws RemoteException {
        super();
        this.gameManager = gameManager;
    }

    @Override
    public List<GameInfoDTO> getAvailableGames() throws RemoteException {
        return gameManager.getAvailableGames();
    }

    @Override
    public RMIServerSession createGame(String nickname, int maxPlayers, RMIClientCallback callback) throws RemoteException {
        try {
            String newGameId = gameManager.createNewGame(nickname, maxPlayers);
            GameRoom room = gameManager.getGame(newGameId);
            RMIClientHandler handler = new RMIClientHandler(callback, nickname);
            room.addPlayer(nickname, handler);

            return handler;
        } catch (Exception e) {
            throw new RemoteException("Impossibile creare la partita: " + e.getMessage());
        }
    }

    @Override
    public RMIServerSession joinGame(String gameId, String nickname, RMIClientCallback callback) throws RemoteException {
        GameRoom room = gameManager.getGame(gameId);

        if (room == null) throw new RemoteException("Partita non trovata.");
        if (room.isFull()) throw new RemoteException("La partita è già al completo.");
        if (room.isGameStarted()) throw new RemoteException("La partita è già iniziata.");
        if (room.isNicknameTaken(nickname)) throw new RemoteException("Nickname già in uso in questa partita.");

        RMIClientHandler handler = new RMIClientHandler(callback, nickname);
        room.addPlayer(nickname, handler);

        return handler;
    }
}
