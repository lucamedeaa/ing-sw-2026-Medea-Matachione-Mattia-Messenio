package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.messages.ClientMessage;
import it.polimi.ingsw.network.messages.ClientMessageVisitor;
import it.polimi.ingsw.network.messages.ServerMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketServerConnection implements VirtualServer, Runnable {

    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private final ClientMessageVisitor view;
    private boolean active;

    public SocketServerConnection(String ip, int port, ClientMessageVisitor view) throws IOException {
        this.socket = new Socket(ip, port);
        this.view = view;
        this.active = true;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

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