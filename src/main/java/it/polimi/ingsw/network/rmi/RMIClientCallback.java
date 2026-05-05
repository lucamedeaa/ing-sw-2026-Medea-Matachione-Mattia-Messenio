package it.polimi.ingsw.network.rmi;

import it.polimi.ingsw.network.dto.AvailableActionDTO;
import it.polimi.ingsw.network.dto.BoardDTO;
import it.polimi.ingsw.network.dto.GameEventDTO;
import it.polimi.ingsw.network.dto.PlayerDTO;
import it.polimi.ingsw.network.messages.GameInfoDTO;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/** Remote RMI callback interface used by the server to send messages to the client. */
public interface RMIClientCallback extends Remote {

    void onFullSync(BoardDTO board, List<PlayerDTO> players, String activePlayer, List<AvailableActionDTO> actions) throws RemoteException;

    void onDeltaEvent(List<GameEventDTO> events, List<AvailableActionDTO> nextActions, String activePlayer) throws RemoteException;

    void onError(String error) throws RemoteException;

    void onMatchmakingSuccess(String text) throws RemoteException;

    void onAvailableGames(List<GameInfoDTO> games) throws RemoteException;

    void onGameAborted(String reason) throws RemoteException;

    void onRoomUpdate(String notification, List<String> currentPlayers) throws RemoteException;

    void onGameLeftSuccess(String text) throws RemoteException;
}
