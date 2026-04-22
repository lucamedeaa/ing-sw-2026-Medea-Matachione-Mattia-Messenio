package it.polimi.ingsw.network.rmi;

import it.polimi.ingsw.network.messages.GameInfoDTO;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RMIMatchmakingService extends Remote {

    List<GameInfoDTO> getAvailableGames() throws RemoteException;

    RMIServerSession createGame(String nickname, int maxPlayers, RMIClientCallback callback) throws RemoteException;

    RMIServerSession joinGame(String gameId, String nickname, RMIClientCallback callback) throws RemoteException;
}