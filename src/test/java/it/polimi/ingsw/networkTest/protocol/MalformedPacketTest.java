package it.polimi.ingsw.networkTest.protocol;

import it.polimi.ingsw.common.message.server.ErrorMessage;
import it.polimi.ingsw.networkTest.NetworkTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
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
    void dropsClientSendingUnknownObject() throws IOException, InterruptedException, ClassNotFoundException {
        Socket badClient = new Socket("localhost", serverPort);
        clientSockets.add(badClient);
        ObjectOutputStream out = new ObjectOutputStream(badClient.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(badClient.getInputStream());
        out.writeObject(new MaliciousPayload());
        out.flush();
        Object response = in.readObject();

        assertTrue(response instanceof ErrorMessage, "Il server non ha disconnesso il client, ma doveva rispondere con un ErrorMessage.");
        ErrorMessage errorMsg = (ErrorMessage) response;
        assertEquals("Unknown message type.", errorMsg.error(), "Il testo dell'errore non corrisponde.");
    }
}