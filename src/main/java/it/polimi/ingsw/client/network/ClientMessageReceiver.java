package it.polimi.ingsw.client.network;

import it.polimi.ingsw.client.model.EventApplier;
import it.polimi.ingsw.client.model.LightGameModel;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.visitor.ClientMessageVisitor;

public class ClientMessageReceiver implements ClientMessageVisitor {
    private final LightGameModel model;
    private final EventApplier applier;

    public ClientMessageReceiver(LightGameModel model) {
        this.model = model;
        this.applier = new EventApplier(model);
    }

    @Override
    public void visit(FullSyncMessage msg) {
        model.setFullState(msg.board(), msg.players());
    }

    @Override
    public void visit(DeltaEventMessage msg) {
        msg.event().accept(applier);
        model.setAvailableActions(msg.nextActions());
    }

    @Override
    public void visit(ErrorMessage message) {
        System.err.println("[SERVER ERROR] " + message.error());
    }

    @Override
    public void visit(ErrorMessageDTO message) {
        System.err.println("[MATCHMAKING ERROR] " + message.error());
    }

    @Override
    public void visit(MatchmakingSuccessMessage message) {
        System.out.println("[MATCHMAKING SUCCESS] " + message.text());
    }

    @Override
    public void visit(AvailableGamesResponseMessage message) {
        System.out.println("[AVAILABLE GAMES] " + message.games());
    }

    @Override
    public void visit(GameAbortedMessage message) {
        System.err.println("[ERROR] Match Ended: " + message.reason());
        // Qui la logica per chiudere la schermata di gioco e tornare al main menu
        // Es: tui.showFatalErrorAndExit(message.reason());
    }
}