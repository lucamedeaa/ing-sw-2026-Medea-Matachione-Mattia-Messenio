package it.polimi.ingsw.networkTest.protocol;

import it.polimi.ingsw.networkTest.NetworkTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

public class SocketRobustnessTest extends NetworkTestBase {

    @Nested
    @DisplayName("Handling Sudden Disconnections")
    class Disconnections {

        @Test
        @DisplayName("The server does not crash if the socket is closed abruptly")
        void serverSurvivesSuddenClose() throws IOException, InterruptedException {
            // Connessione ad un client finto
            Socket badClient = new Socket("localhost", serverPort);
            clientSockets.add(badClient);

            // Chiusura del socket malamente per generare un EOFException nel Server
            badClient.close();

            // Retry health-check connection instead of fixed sleep
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