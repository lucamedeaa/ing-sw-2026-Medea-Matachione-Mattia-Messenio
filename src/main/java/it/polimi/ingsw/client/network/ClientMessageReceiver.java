package it.polimi.ingsw.client.network;

import it.polimi.ingsw.client.lightGameModel.EventApplier;
import it.polimi.ingsw.client.lightGameModel.LightGameModel;
import it.polimi.ingsw.client.tui.UIState;
import it.polimi.ingsw.client.view.ClientUI;
import it.polimi.ingsw.network.dto.GameEventDTO;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.visitor.ClientMessageVisitor;

public class ClientMessageReceiver implements ClientMessageVisitor {
    private final LightGameModel model;
    private final EventApplier applier;
    private final ClientUI ui;

    public ClientMessageReceiver(LightGameModel model,ClientUI ui) {
        this.model = model;
        this.applier = new EventApplier(model);
        this.ui=ui;
    }

    @Override
    public void visit(FullSyncMessage msg) {
        model.setFullState(msg.board(), msg.players());
        model.setAvailableActions(msg.actions());
        ui.dispatch(UIState::onGameStarted);
    }

    @Override
    public void visit(DeltaEventMessage msg) {
        model.startBatch(); // Muta la TUI

        // Applica gli eventi in silenzio
        for (GameEventDTO event : msg.events()) {
            event.accept(applier);
        }
        model.setAvailableActions(msg.nextActions());

        model.endBatch();  // Riattiva la TUI e renderizza
    }

    @Override
    public void visit(ErrorMessage message) {
        System.err.println("[SERVER ERROR] " + message.error());
        ui.dispatch(state -> state.onError(message.error()));
    }

    @Override
    public void visit(ErrorMessageDTO message) {
        System.err.println("[MATCHMAKING ERROR] " + message.error());
        ui.dispatch(state -> state.onError(message.error()));
    }

    @Override
    public void visit(MatchmakingSuccessMessage message) {
        System.out.println("[MATCHMAKING SUCCESS] " + message.text());
        ui.dispatch(state -> state.onMatchmakingSuccess(message.text()));
    }

    @Override
    public void visit(AvailableGamesResponseMessage message) {
        System.out.println("[AVAILABLE GAMES] " + message.games());
        ui.dispatch(state -> state.onAvailableGames(message.games()));
    }

    @Override
    public void visit(GameAbortedMessage message) {
        System.err.println("[ERROR] Match Ended: " + message.reason());
        // Qui la logica per chiudere la schermata di gioco e tornare al main menu
        // Es: tui.showFatalErrorAndExit(message.reason());
        ui.dispatch(state -> state.onGameAborted(message.reason()));
    }

    @Override
    public void visit(RoomUpdateMessage message) {
        ui.dispatch(state -> state.onRoomUpdate(message.currentPlayers(), message.notification()));
        //TODO
    }
    @Override
    public void visit(GameLeftSuccessMessage message) {
        ui.dispatch(UIState::onGameLeft);
    }
}