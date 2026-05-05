package it.polimi.ingsw.network.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/** Remote RMI session interface for communication between client and server. */
public interface RMIServerSession extends Remote {

    void ping() throws RemoteException;

    void disconnect() throws RemoteException;

    void createGame(String nickname, int maxPlayers) throws RemoteException;

    void joinGame(String nickname, String gameId) throws RemoteException;

    void getAvailableGames() throws RemoteException;

    void leaveGame() throws RemoteException;

    void placeTotem(int positionIndex) throws RemoteException;

    void takeCard(int row, int col) throws RemoteException;

    void skipAction() throws RemoteException;
}