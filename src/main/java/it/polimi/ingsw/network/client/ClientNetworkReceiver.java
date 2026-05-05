package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.visitor.ClientMessageVisitor;

public interface ClientNetworkReceiver extends ClientMessageVisitor, ServerNotificationReceiver {
}
