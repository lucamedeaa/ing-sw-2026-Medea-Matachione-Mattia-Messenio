package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.server.GameManager;
import it.polimi.ingsw.server.GameRoom;
import it.polimi.ingsw.view.VirtualView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketClientHandler implements ClientConnection, Runnable {

    private final Socket socket;
    private final GameManager gameManager;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private VirtualView virtualView;
    private boolean active;
    private String nickname;

    public SocketClientHandler(Socket socket, GameManager gameManager) {
        this.socket = socket;
        this.gameManager = gameManager;
        this.active = true;
        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            this.active = false;
        }
    }

    @Override
    public void setVirtualView(VirtualView virtualView) {
        this.virtualView = virtualView;
    }

    @Override
    public synchronized void send(ServerMessage message) {
        try {
            if (active) {
                out.writeObject(message);
                out.reset(); // Fondamentale per evitare che Java invii cache di oggetti vecchi
            }
        } catch (IOException e) {
            System.err.println("[SOCKET] Disconnessione rilevata in scrittura per: " + nickname);
            handleDisconnection();
        }
    }

    @Override
    public void run() {
        try {
            while (active) {
                Object input = in.readObject();

                if (input instanceof ClientMessage message) {
                    if (virtualView != null) {
                        // Il giocatore è in partita: il pacchetto va spacchettato tramite Visitor
                        virtualView.onMessageReceived(message);
                    } else {
                        // Il giocatore è ancora nella schermata di avvio
                        handleMatchmakingMessage(message);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[SOCKET] Disconnessione rilevata in lettura per: " + nickname);
            handleDisconnection();
        } finally {
            closeConnection();
        }
    }

    private void handleMatchmakingMessage(ClientMessage message) {
        try {
            if (message instanceof GetAvailableGamesMessage) {
                var availableGames = gameManager.getAvailableGames();
                send(new AvailableGamesResponseMessage(availableGames));

            } else if (message instanceof CreateGameMessage createMsg) {
                this.nickname = createMsg.getNickname();
                String gameId = gameManager.createNewGame(this.nickname, createMsg.getMaxPlayers());
                GameRoom room = gameManager.getGame(gameId);

                room.addPlayer(this.nickname, this);
                send(new MatchmakingSuccessMessage("Partita creata! Sei in attesa di altri giocatori..."));

            } else if (message instanceof JoinGameMessage joinMsg) {
                this.nickname = joinMsg.getNickname();
                GameRoom room = gameManager.getGame(joinMsg.getGameId());

                if (room == null) {
                    send(new ErrorMessageDTO("La partita richiesta non esiste."));
                    return;
                }

                room.addPlayer(this.nickname, this);
                send(new MatchmakingSuccessMessage("Unito alla partita con successo! In attesa di iniziare..."));

            } else {
                send(new ErrorMessageDTO("Errore: non sei ancora in una partita."));
            }
        } catch (Exception e) {
            send(new ErrorMessageDTO("Errore durante l'accesso: " + e.getMessage()));
        }
    }

    private void handleDisconnection() {
        if (!active) return;
        this.active = false;

        closeConnection();

        if (virtualView != null) {
            // Se era in partita, avvisa la logica di gioco
            virtualView.handleDisconnection(nickname);
        } else if (nickname != null) {
            // Se era nel matchmaking, ripulisci la sua presenza eventuale nelle lobby
            GameRoom room = gameManager.getGameRoomByPlayer(nickname);
            if (room != null) {
                room.removePlayer(nickname);
            }
        }
    }

    private void closeConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {}
    }
}