package it.polimi.ingsw.client.network;

import it.polimi.ingsw.client.model.EventApplier;
import it.polimi.ingsw.client.model.LightGameModel;
import it.polimi.ingsw.network.messages.ClientMessageVisitor;
import it.polimi.ingsw.network.messages.DeltaEventMessage;
import it.polimi.ingsw.network.messages.FullSyncMessage;

public class ClientMessageReceiver implements ClientMessageVisitor {
    private final LightGameModel model;
    private final EventApplier applier;

    public ClientMessageReceiver(LightGameModel model) {
        this.model = model;
        this.applier = new EventApplier(model);
    }

    @Override
    public void visit(FullSyncMessage msg) {
        // Sincronizzazione totale (es. inizio partita)
        model.setFullState(msg.board(), msg.players());
    }

    @Override
    public void visit(DeltaEventMessage msg) {
        //  Applica l'evento allo stato locale
        msg.event().accept(applier);
        //  Aggiorna le azioni che l'utente può cliccare
        model.setAvailableActions(msg.nextActions());
    }
}