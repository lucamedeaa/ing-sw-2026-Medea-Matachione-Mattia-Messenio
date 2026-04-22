package it.polimi.ingsw.client.model;

import it.polimi.ingsw.network.dto.events.*;
import it.polimi.ingsw.network.visitor.EventVisitor;

public class EventApplier implements EventVisitor {
    private final LightGameModel model;

    public EventApplier(LightGameModel model) {
        this.model = model;
    }

    @Override
    public void visit(CardTakenEventDTO event) {
        model.removeCard(event.row(), event.col());
    }

    @Override
    public void visit(BoardRefilledEventDTO event) {
        model.refillBoardRow(event.row(), event.newCardIds());
    }

    @Override
    public void visit(TotemPlacedEventDTO event) {
        model.updateTotemPosition(event.nickname(), event.positionIndex());
    }

    @Override
    public void visit(PlayerResourcesChangedEventDTO event) {
        model.updatePlayerResources(event.nickname(), event.newFood(), event.newPrestige());
    }

    @Override
    public void visit(CardAddedToTribeEventDTO event) {
        model.addCardToPlayerTribe(event.nickname(), event.cardId());
    }

    @Override
    public void visit(EraTransitionEventDTO event) {
        model.updateEra(event.newEraNumber());
    }

    @Override
    public void visit(RoundAdvancedEventDTO event) {
        model.updateRound(event.newRound());
    }
}