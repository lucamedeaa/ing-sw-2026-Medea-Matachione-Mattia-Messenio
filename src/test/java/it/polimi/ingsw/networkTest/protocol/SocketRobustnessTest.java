package it.polimi.ingsw.networkTest.protocol;

import it.polimi.ingsw.networkTest.NetworkTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SUMMARY:
 * Verifies that abrupt client disconnections do not compromise server availability or stability.
 *
 * EXPECTATION:
 * The underlying server loop survives unexpected TCP reset drops and continues to accept
 * incoming connection payloads from subsequent legitimate clients.
 */
public class SocketRobustnessTest extends NetworkTestBase {

    @Nested
    @DisplayName("Handling Sudden Disconnections")
    class Disconnections {

        @Test
        @DisplayName("The server does not crash if the socket is closed abruptly")
        void serverSurvivesSuddenClose() throws IOException, InterruptedException {
            Socket badClient = new Socket("localhost", serverPort);
            clientSockets.add(badClient);
            badClient.close();

            Socket goodClient = null;
            for (int attempt = 0; attempt < 10; attempt++) {
                try {
                    goodClient = new Socket("localhost", serverPort);
                    clientSockets.add(goodClient);
                    break;
                } catch (IOException e) {
                    Thread.sleep(100);
                }
            }

            assertNotNull(goodClient, "Server must accept new connections after a client crashes");
            assertTrue(goodClient.isConnected());
            assertFalse(serverSocket.isClosed(), "The ServerSocket must not close due to a client error");
        }
    }
}