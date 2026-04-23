package it.polimi.ingsw.client.network;

import it.polimi.ingsw.network.client.RMIServerConnection;
import it.polimi.ingsw.network.client.SocketServerConnection;
import it.polimi.ingsw.network.client.VirtualServer;
import it.polimi.ingsw.network.visitor.ClientMessageVisitor;
import it.polimi.ingsw.network.rmi.RMIClientCallback;
import it.polimi.ingsw.network.rmi.RMIMatchmakingService;
import it.polimi.ingsw.network.client.RMIClientCallbackImpl;
import it.polimi.ingsw.network.rmi.RMIServerSession;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class NetworkClientFactory {

    public enum NetworkType {
        SOCKET,
        RMI
    }

    public static VirtualServer createConnection(
            NetworkType type,
            String ip,
            int port,
            ClientMessageVisitor view,
            String nickname,
            int action,
            int maxPlayers,
            String gameId) throws Exception {

        if (type == NetworkType.SOCKET) {
            SocketServerConnection socketConn = new SocketServerConnection(ip, port, view);
            new Thread(socketConn).start();
            return socketConn;
        } else {
            Registry registry = LocateRegistry.getRegistry(ip, port);
            RMIMatchmakingService lobby = (RMIMatchmakingService) registry.lookup("MesosMatchmaking");

            RMIClientCallback callback = new RMIClientCallbackImpl(view);
            RMIServerSession session;

            if (action == 1) {
                session = lobby.createGame(nickname, maxPlayers, callback);
            } else {
                session = lobby.joinGame(gameId, nickname, callback);
            }

            return new RMIServerConnection(session);
        }
    }
}