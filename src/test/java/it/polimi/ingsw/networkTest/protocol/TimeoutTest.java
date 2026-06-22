package it.polimi.ingsw.networkTest.protocol;

import it.polimi.ingsw.networkTest.NetworkTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SUMMARY:
 * Verifies that idle clients are automatically disconnected after the configured socket timeout expires.
 *
 * EXPECTATION:
 * The server automatically detects inactive TCP links, terminates the socket session, and yields
 * an EOF (-1) signal back to the client read stream without leaking server resources.
 */
@Timeout(20)
public class TimeoutTest extends NetworkTestBase {

    @Test
    @DisplayName("Server disconnects idle clients after socket timeout")
    void disconnectsIdleClients() throws IOException, InterruptedException {
        Socket idleClient = new Socket("localhost", serverPort);
        clientSockets.add(idleClient);

        ObjectOutputStream out = new ObjectOutputStream(idleClient.getOutputStream());
        out.flush();
        ObjectInputStream in = new ObjectInputStream(idleClient.getInputStream());

        int readByte = idleClient.getInputStream().read();
        assertEquals(-1, readByte, "Server should have closed the socket due to read timeout.");
    }
}