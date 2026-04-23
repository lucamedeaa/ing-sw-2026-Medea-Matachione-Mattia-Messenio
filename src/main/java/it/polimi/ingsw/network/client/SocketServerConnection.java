package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.messages.ClientMessage;
import it.polimi.ingsw.network.messages.ClientMessageVisitor;
import it.polimi.ingsw.network.messages.ServerMessage;

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

    /** Constructs a socket connection to the server. @param ip server IP address @param port server port @param view visitor handling incoming messages @throws IOException if connection fails */
    public SocketServerConnection(String ip, int port, ClientMessageVisitor view) throws IOException {
        this.socket = new Socket(ip, port);
        this.view = view;
        this.active = true;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    /** Sends a message to the server if the connection is active. @param message message to send */
    @Override
    public synchronized void sendMessage(ClientMessage message) {
        try {
            if (active) {
                out.writeObject(message);
                out.reset();
            }
        } catch (IOException e) {
            disconnect();
        }
    }

    /** Continuously listens for incoming messages from the server and dispatches them to the visitor. */
    @Override
    public void run() {
        try {
            while (active) {
                Object input = in.readObject();
                if (input instanceof ServerMessage message) {
                    message.accept(view);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            disconnect();
        }
    }

    /** Closes the connection and releases resources, marking the connection as inactive. */
    @Override
    public void disconnect() {
        if (!active) return;
        active = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {}
        //TODO: notifica disconnessione view
        // view.showConnectionError();
    }
}