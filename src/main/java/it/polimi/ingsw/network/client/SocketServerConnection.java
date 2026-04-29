package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.messages.ClientMessage;
import it.polimi.ingsw.network.messages.DisconnectionMessage;
import it.polimi.ingsw.network.messages.PingMessage;
import it.polimi.ingsw.network.messages.ServerMessage;
import it.polimi.ingsw.network.visitor.ClientMessageVisitor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/** Socket-based implementation of VirtualServer that handles bidirectional communication with the server. */
public class SocketServerConnection implements Runnable, VirtualServer {

    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final ClientMessageVisitor view;

    private final ScheduledExecutorService pinger;
    private final AtomicBoolean active = new AtomicBoolean(true);
    private final Object streamLock = new Object();

    /**
     * Initializes the socket and streams.
     */
    public SocketServerConnection(String ip, int port, ClientMessageVisitor view) throws IOException {
        this.socket = new Socket(ip, port);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        this.view = view;
        this.socket.setSoTimeout(10000);

        this.pinger = Executors.newSingleThreadScheduledExecutor();
        this.pinger.scheduleAtFixedRate(() -> {
            sendMessage(new PingMessage());
        }, 5, 5, TimeUnit.SECONDS);
    }

    /**
     * Sends a generic client message to the server.
     */
    public void sendMessage(ClientMessage message) {
        if (active.get()) {
            try {
                synchronized (streamLock) {
                    out.writeObject(message);
                    out.reset();
                }
            } catch (IOException e) {
                handleServerDisconnection();
            }
        }
    }

    /**
     * Background loop to receive messages from the server and forward them to the view.
     */
    @Override
    public void run() {
        try {
            while (active.get()) {
                Object input = in.readObject();
                if (input instanceof ServerMessage message) {
                    message.accept(view);
                }
            }
        } catch (SocketTimeoutException e) {
            System.err.println("[CLIENT] Timeout: Il server non risponde (nessun Pong ricevuto).");
            handleServerDisconnection();
        } catch (Exception e) {
            handleServerDisconnection();
        }
    }

    public void disconnect() {
        if (active.compareAndSet(true, false)) {
            try {
                synchronized (streamLock) {
                    out.writeObject(new DisconnectionMessage());
                    out.reset();
                }
            } catch (IOException ignored) {
            }
            closeConnection();
            System.out.println("[CLIENT] Disconnessione volontaria effettuata.");
        }
    }

    /**
     * Handles unexpected disconnections.
     */
    private void handleServerDisconnection() {
        if (active.compareAndSet(true, false)) {
            closeConnection();
            System.err.println("[CLIENT] Disconnesso dal server inaspettatamente.");
            //TODO: notificare la view del crash del server
        }
    }

    /**
     * Closes streams and socket safely.
     */
    private void closeConnection() {
        try {
            if (pinger != null) pinger.shutdownNow();
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {
        }
    }
}