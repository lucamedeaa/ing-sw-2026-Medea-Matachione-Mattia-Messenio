package it.polimi.ingsw.network.rmi;

import it.polimi.ingsw.network.messages.GameInfoDTO;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/** Remote RMI service for matchmaking, allowing clients to list, create, and join games. */
public interface RMIMatchmakingService extends Remote {

    /** Retrieves the list of available games. @return list of game info DTOs @throws RemoteException if communication fails */
    List<GameInfoDTO> getAvailableGames() throws RemoteException;

    /** Creates a new game and returns a server session. @param nickname player nickname @param maxPlayers maximum number of players @param callback client callback for updates @return server session @throws RemoteException if communication fails */
    RMIServerSession createGame(String nickname, int maxPlayers, RMIClientCallback callback) throws RemoteException;

    /** Joins an existing game and returns a server session. @param gameId game identifier @param nickname player nickname @param callback client callback for updates @return server session @throws RemoteException if communication fails */
    RMIServerSession joinGame(String gameId, String nickname, RMIClientCallback callback) throws RemoteException;
}