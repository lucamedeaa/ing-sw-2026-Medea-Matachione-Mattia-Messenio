package it.polimi.ingsw.client.network;

import it.polimi.ingsw.common.message.DisconnectionMessage;
import it.polimi.ingsw.common.message.client.PingMessage;
import it.polimi.ingsw.common.message.server.ServerDisconnectedMessage;
import it.polimi.ingsw.common.message.server.ServerMessage;
import it.polimi.ingsw.common.visitor.ClientMessageVisitor;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Socket-based connection that handles bidirectional communication with the server. */
public class SocketServerConnection implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(SocketServerConnection.class.getName());

    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final ClientMessageVisitor view;

    private final ScheduledExecutorService pinger;
    private final AtomicBoolean active = new AtomicBoolean(true);
    private final Object streamLock = new Object();

    /**
     * Initializes the socket, object streams, and heartbeat task.
     *
     * @param ip server host
     * @param port server port
     * @param view visitor that receives decoded server messages
     * @throws IOException if the socket or streams cannot be opened
     */
    public SocketServerConnection(String ip, int port, ClientMessageVisitor view) throws IOException {
        this.socket = new Socket(ip, port);
        this.socket.setSoTimeout(10000);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        this.view = view;

        this.pinger = Executors.newSingleThreadScheduledExecutor();
        this.pinger.scheduleAtFixedRate(() ->
            sendMessage(new PingMessage())
        , 5, 5, TimeUnit.SECONDS);
    }

    /**
     * Sends a serializable socket message to the server.
     *
     * @param message message to write to the socket
     */
    public void sendMessage(Serializable message) {
        if (active.get()) {
            try {
                synchronized (streamLock) {
                    out.writeObject(message);
                    out.reset();
                    out.flush();
                }
            } catch (IOException e) {
                LOGGER.log(Level.INFO, "I/O error while sending a message to the server.", e);
                handleServerDisconnection("An error occurred whilst sending a message to the server.");
            }
        }
    }

    /**
     * Background loop to receive messages from the server and forward them to the view.
     */
    @Override
    public void run() {
        String disconnectReason = "Unexpected disconnection from the server.";
        try {
            while (active.get()) {
                Object input = in.readObject();
                if (input instanceof ServerMessage message) {
                    message.accept(view);
                }
            }
        } catch (SocketTimeoutException e) {
            disconnectReason = "Timeout: The server is not responding.";
        } catch (EOFException e) {
            disconnectReason = "The server unexpectedly closed the connection.";
        } catch (SocketException e) {
            disconnectReason = "Connection to the server has been lost.";
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Deserialisation error. Incompatible client version?", e);
            disconnectReason = "Incompatible network protocol.";
        } catch (IOException e) {
            disconnectReason = "I/O error during reading: " + e.getMessage();
        } finally {
            handleServerDisconnection(disconnectReason);
        }
    }

    /**
     * Sends a disconnection message and closes local resources.
     */
    public void disconnect() {
        if (active.compareAndSet(true, false)) {
            try {
                synchronized (streamLock) {
                    socket.setSoTimeout(1000);
                    out.writeObject(new DisconnectionMessage());
                    out.flush();
                    out.reset();
                }
            } catch (IOException e) {
                LOGGER.log(Level.FINE, "Could not notify the server before closing the socket connection.", e);
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
            new ServerDisconnectedMessage(reason).accept(view);
        }
    }

    /**
     * Closes streams and socket safely.
     */
    private void closeConnection() {
        pinger.shutdownNow();
        closeResource(in, "input stream");
        closeResource(out, "output stream");
        closeResource(socket, "socket");
    }

    private void closeResource(Closeable resource, String description) {
        try {
            resource.close();
        } catch (IOException e) {
            LOGGER.log(Level.FINE, "Error while closing client " + description + ".", e);
        }
    }
}
