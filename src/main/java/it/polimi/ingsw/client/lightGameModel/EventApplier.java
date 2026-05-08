package it.polimi.ingsw.client.lightGameModel;

import it.polimi.ingsw.client.tui.render.AnsiColors;
import it.polimi.ingsw.network.dto.TribeDTO;
import it.polimi.ingsw.network.dto.events.*;
import it.polimi.ingsw.network.visitor.EventVisitor;

public class EventApplier implements EventVisitor {

    private final MatchModel matchModel;

    public EventApplier(MatchModel matchModel) {
        this.matchModel = matchModel;
    }

    @Override
    public void visit(CardTakenEventDTO event) {
        matchModel.removeCard(event.row(), event.col());
    }

    @Override
    public void visit(BoardRefilledEventDTO event) {
        matchModel.refillBoardRow(event.row(), event.newCardIds());
    }

    @Override
    public void visit(TotemPlacedEventDTO event) {
        matchModel.updateTotemPosition(event.nickname(), event.positionIndex());
    }

    @Override
    public void visit(PlayerResourcesChangedEventDTO event) {
        matchModel.updatePlayerResources(event.nickname(), event.newFood(), event.newPrestige(), event.foodDiscount());
        if (event.reason() != null && !event.reason().isEmpty()) {
            matchModel.addGameLog(AnsiColors.YELLOW + "[" + event.nickname() + "] " + event.reason() + AnsiColors.RESET);
        }
    }

    @Override
    public void visit(CardAddedToTribeEventDTO event) {
        matchModel.addCardToPlayerTribe(event.nickname(), event.cardId());
    }

    @Override
    public void visit(EraTransitionEventDTO event) {
        matchModel.updateEra(event.newEraNumber());
        matchModel.addGameLog(AnsiColors.CYAN_BOLD + "[!] INIZIA L'ERA " + event.newEraNumber() + "!" + AnsiColors.RESET);
    }

    @Override
    public void visit(RoundAdvancedEventDTO event) {
        matchModel.updateRound(event.newRound());
    }

    @Override
    public void visit(TribeDTO tribe) {
        matchModel.updatePlayerTribe(tribe.nickname(), tribe.tribe());
    }

    @Override
    public void visit(WinnersAnnouncedEventDTO event) {
        matchModel.setWinners(event.winnersNicknames());
    }

    @Override
    public void visit(PlayerLeftGameDTO event) {
        matchModel.setGameAborted("Il giocatore " + event.nickname() + " si è disconnesso. La partita è annullata.");
    }

    @Override
    public void visit(GameOverEventDTO event) {
        matchModel.setGameOver(event.leaderboard());
    }
    @Override
    public void visit(TotemReturnedEventDTO event) {
        matchModel.returnTotemToTrack(event.nickname(), event.returnIndex());
    }
}