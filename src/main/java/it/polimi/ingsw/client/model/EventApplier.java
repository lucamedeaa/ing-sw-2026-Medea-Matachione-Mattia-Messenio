package it.polimi.ingsw.client.model;

import it.polimi.ingsw.network.dto.events.*;
import it.polimi.ingsw.network.visitor.EventVisitor;

/** Applies incoming event DTOs to update the client-side game model. */
public class EventApplier implements EventVisitor {

    private final LightGameModel model;

    /** Constructs the event applier. @param model */
    public EventApplier(LightGameModel model) {
        this.model = model;
    }

    /** @param event */
    @Override
    public void visit(CardTakenEventDTO event) {
        model.removeCard(event.row(), event.col());
    }

    /** @param event */
    @Override
    public void visit(BoardRefilledEventDTO event) {
        model.refillBoardRow(event.row(), event.newCardIds());
    }

    /** @param event */
    @Override
    public void visit(TotemPlacedEventDTO event) {
        model.updateTotemPosition(event.nickname(), event.positionIndex());
    }

    /** @param event */
    @Override
    public void visit(PlayerResourcesChangedEventDTO event) {
        model.updatePlayerResources(event.nickname(), event.newFood(), event.newPrestige());
    }

    /** @param event */
    @Override
    public void visit(CardAddedToTribeEventDTO event) {
        model.addCardToPlayerTribe(event.nickname(), event.cardId());
    }

    /** @param event */
    @Override
    public void visit(EraTransitionEventDTO event) {
        model.updateEra(event.newEraNumber());
    }

    /** @param event */
    @Override
    public void visit(RoundAdvancedEventDTO event) {
        model.updateRound(event.newRound());
    }
}