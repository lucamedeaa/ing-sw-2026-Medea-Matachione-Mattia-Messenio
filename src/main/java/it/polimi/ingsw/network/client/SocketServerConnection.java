package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.messages.ClientMessage;
import it.polimi.ingsw.network.messages.ServerMessage;
import it.polimi.ingsw.network.visitor.ClientMessageVisitor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/** Socket-based implementation of VirtualServer that handles bidirectional communication with the server. */
public class SocketServerConnection implements VirtualServer, Runnable {

    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private final ClientMessageVisitor view;
    private boolean active;

    /** Initializes the socket and streams. */
    public SocketServerConnection(String ip, int port, ClientMessageVisitor view) throws IOException {
        this.socket = new Socket(ip, port);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        this.view = view;
        this.active = true;
    }

    /** Sends a generic client message to the server. */
    @Override
    public synchronized void sendMessage(ClientMessage message) {
        try {
            if (active) {
                out.writeObject(message);
                out.reset();
            }
        } catch (IOException e) {
            handleDisconnection();
        }
    }

    /** Background loop to receive messages from the server and forward them to the view. */
    @Override
    public void run() {
        try {
            while (active) {
                Object input = in.readObject();
                if (input instanceof ServerMessage message) {
                    message.accept(view);
                }
            }
        } catch (Exception e) {
            handleDisconnection();
        } finally {
            closeConnection();
        }
    }

    /** Handles unexpected disconnections. */
    private void handleDisconnection() {
        this.active = false;
        closeConnection();
        // TODO: Notificare la UI della disconnessione (es. inviando un ErrorMessage locale o chamando un metodo apposito)
        System.err.println("[CLIENT] Disconnesso dal server.");
    }

    /** Closes streams and socket safely. */
    private void closeConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {}
    }

    @Override
    public void disconnect() {
    }
}