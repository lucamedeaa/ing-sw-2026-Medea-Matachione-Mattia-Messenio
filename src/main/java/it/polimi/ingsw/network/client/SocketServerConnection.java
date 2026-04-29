package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.messages.ClientMessage;
import it.polimi.ingsw.network.messages.DisconnectionMessage;
import it.polimi.ingsw.network.messages.PingMessage;
import it.polimi.ingsw.network.messages.ServerMessage;
import it.polimi.ingsw.network.visitor.ClientMessageVisitor;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
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
        this.socket.setSoTimeout(10000);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        this.view = view;

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
                handleServerDisconnection("Errore durante l'invio di un messaggio al server.");
            }
        }
    }

    /**
     * Background loop to receive messages from the server and forward them to the view.
     */
    @Override
    public void run() {
        String disconnectReason = "Disconnessione dal server inaspettata.";
        try {
            while (active.get()) {
                Object input = in.readObject();
                if (input instanceof ServerMessage message) {
                    message.accept(view);
                }
            }
        } catch (SocketTimeoutException e) {
            disconnectReason = "Timeout: Il server non risponde (crash o rete assente).";
        } catch (EOFException e) {
            disconnectReason = "Il server ha chiuso la connessione in modo imprevisto.";
        } catch (SocketException e) {
            disconnectReason = "Connessione al server interrotta (SocketException).";
        } catch (Exception e) {
            disconnectReason = "Errore imprevisto durante la comunicazione: " + e.getMessage();
        } finally {
            handleServerDisconnection(disconnectReason);
        }
    }

    public void disconnect() {
        if (active.compareAndSet(true, false)) {
            try {
                synchronized (streamLock) {
                    socket.setSoTimeout(1000);
                    out.writeObject(new DisconnectionMessage());
                    out.flush();
                    out.reset();
                }
            } catch (IOException ignored) {
            } finally {
                closeConnection();
            }
        }
    }

    /**
     * Handles unexpected disconnections.
     */
    private void handleServerDisconnection(String reason) {
        if (active.compareAndSet(true, false)) {
            closeConnection();
            //TODO: notificare la view del crash del server
            if (view != null) {
                //view.showNetworkError(reason);
            }
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