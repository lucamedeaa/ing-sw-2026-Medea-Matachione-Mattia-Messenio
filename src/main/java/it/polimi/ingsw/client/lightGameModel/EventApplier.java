package it.polimi.ingsw.client.lightGameModel;

import it.polimi.ingsw.network.dto.TribeDTO;
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
        if (event.reason() != null && !event.reason().isEmpty()) {
            model.addGameLog("\033[33m[" + event.nickname() + "] " + event.reason() + "\033[0m");
        }
    }

    @Override
    public void visit(CardAddedToTribeEventDTO event) {
        model.addCardToPlayerTribe(event.nickname(), event.cardId());
    }

    @Override
    public void visit(EraTransitionEventDTO event) {
        model.updateEra(event.newEraNumber());
        model.addGameLog("\033[1;36m[!] INIZIA L'ERA " + event.newEraNumber() + "!\033[0m");
    }

    @Override
    public void visit(RoundAdvancedEventDTO event) {
        model.updateRound(event.newRound());
    }

    @Override
    public void visit(TribeDTO tribe) {
        model.updatePlayerTribe(tribe.nickname(), tribe.tribe());
    }

    @Override
    public void visit(WinnersAnnouncedEventDTO event) {
        model.setWinners(event.winnersNicknames());
    }

    @Override
    public void visit(PlayerLeftGameDTO event) {
        //TODO disconnessione player
    }

    @Override
    public void visit(GameOverEventDTO event) {
        // mostrare i punteggi finali sulla TUI
    }
    @Override
    public void visit(TotemReturnedEventDTO event) {
        model.returnTotemToTrack(event.nickname(), event.returnIndex());
    }
}