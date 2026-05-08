package it.polimi.ingsw.common.rmi;

import it.polimi.ingsw.common.network.dto.action.ActionDto;
import it.polimi.ingsw.common.network.dto.BoardDto;
import it.polimi.ingsw.common.network.dto.event.GameEventDto;
import it.polimi.ingsw.common.network.dto.LeaderboardSnapshotDto;
import it.polimi.ingsw.common.network.dto.PlayerDto;
import it.polimi.ingsw.common.network.dto.PlayerGameCompletedDto;
import it.polimi.ingsw.common.network.dto.GameInfoDto;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/** Remote RMI callback interface used by the server to send messages to the client. */
public interface RMIClientCallback extends Remote {

    void onFullSync(BoardDto board, List<PlayerDto> players, String activePlayer, List<ActionDto> actions) throws RemoteException;

    void onDeltaEvent(List<GameEventDto> events, List<ActionDto> nextActions, String activePlayer) throws RemoteException;

    void onError(String error) throws RemoteException;

    void onMatchmakingSuccess(String text) throws RemoteException;

    void onAvailableGames(List<GameInfoDto> games) throws RemoteException;

    void onGameAborted(String reason) throws RemoteException;

    void onRoomUpdate(String notification, List<String> currentPlayers) throws RemoteException;

    void onGameLeftSuccess(String text) throws RemoteException;

    void onGameCompleted(PlayerGameCompletedDto completedGame) throws RemoteException;

    void onLeaderboard(LeaderboardSnapshotDto leaderboard) throws RemoteException;
}
