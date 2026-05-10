package it.polimi.ingsw.networkTest.protocol;

import it.polimi.ingsw.networkTest.NetworkTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

/*The server allocates resources for each connection. If a client unplugs the network cable without sending a DisconnectionMessage,
the server would remain stuck indefinitely in the readObject() function.
The test checks that the setSoTimeout(10000) configured in the SocketClientHandler is triggered and forces the session to close.*/

public class TimeoutTest extends NetworkTestBase {

    @Test
    @DisplayName("Server disconnects idle clients after socket timeout")
    void disconnectsIdleClients() throws IOException, InterruptedException {
        Socket idleClient = new Socket("localhost", serverPort);
        clientSockets.add(idleClient);

        // The timeout in SocketClientHandler is 10000ms.
        // We wait slightly longer to ensure the timeout exception is triggered.
        Thread.sleep(11000);

        int readByte = idleClient.getInputStream().read();
        assertEquals(-1, readByte, "Server should have closed the socket due to read timeout.");
    }
}