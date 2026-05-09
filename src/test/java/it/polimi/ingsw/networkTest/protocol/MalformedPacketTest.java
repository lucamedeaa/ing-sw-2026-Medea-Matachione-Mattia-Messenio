package it.polimi.ingsw.networkTest.protocol;

import it.polimi.ingsw.networkTest.NetworkTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

/*Ensure that the server does not crash or corrupt the stream if an outdated or malicious client sends a serialised class
that the server does not recognise or expect (resulting in a ClassNotFoundException or ClassCastException).
The server must isolate the error to the individual SocketClientHandler and disconnect the faulty client, whilst keeping the others operational.*/

public class MalformedPacketTest extends NetworkTestBase {

    private static class MaliciousPayload implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String payload = "Exploit attempt";
    }

    @Test
    @DisplayName("Server drops client sending unknown serialized objects without crashing")
    void dropsClientSendingUnknownObject() throws IOException, InterruptedException {
        Socket badClient = new Socket("localhost", serverPort);
        clientSockets.add(badClient);

        ObjectOutputStream out = new ObjectOutputStream(badClient.getOutputStream());
        out.writeObject(new MaliciousPayload());
        out.flush();

        Thread.sleep(500);

        Socket goodClient = new Socket("localhost", serverPort);
        clientSockets.add(goodClient);

        assertTrue(goodClient.isConnected(), "Server must still accept new connections.");

        int readByte = badClient.getInputStream().read();
        assertEquals(-1, readByte, "Server should have closed the connection with the bad client.");
    }
}