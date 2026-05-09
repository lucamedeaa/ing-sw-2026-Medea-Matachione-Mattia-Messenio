package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.view.tui.render.ColorAnsi;
import it.polimi.ingsw.common.network.dto.TribeDto;
import it.polimi.ingsw.common.network.dto.event.*;
import it.polimi.ingsw.common.visitor.EventVisitor;

public class EventApplier implements EventVisitor {

    private final GameModel gameModel;

    public EventApplier(GameModel gameModel) {
        this.gameModel = gameModel;
    }

    @Override
    public void visit(CardTakenDto event) {
        gameModel.removeCard(event.row(), event.col());
    }

    @Override
    public void visit(BoardRefilledDto event) {
        gameModel.refillBoardRow(event.row(), event.newCardIds());
    }

    @Override
    public void visit(TotemPlacedDto event) {
        gameModel.updateTotemPosition(event.nickname(), event.positionIndex());
    }

    @Override
    public void visit(PlayerResourcesChangedDto event) {
        gameModel.updatePlayerResources(event.nickname(), event.newFood(), event.newPrestige(), event.foodDiscount(), event.sustenanceDiscount());
        if (event.reason() != null && !event.reason().isEmpty()) {
            gameModel.addGameLog(ColorAnsi.YELLOW + "[" + event.nickname() + "] " + event.reason() + ColorAnsi.RESET);
        }
    }

    @Override
    public void visit(CardAddedToTribeDto event) {
        gameModel.addCardToPlayerTribe(event.nickname(), event.cardId());
    }

    @Override
    public void visit(EraTransitionDto event) {
        gameModel.updateEra(event.newEraNumber());
        gameModel.addGameLog(ColorAnsi.CYAN_BOLD + "[!] THE ERA BEGINS " + event.newEraNumber() + "!" + ColorAnsi.RESET);
    }

    @Override
    public void visit(RoundAdvancedDto event) {
        //gameModel.clearTurnDeltas();
        gameModel.addGameLog(ColorAnsi.YELLOW_BOLD + "=== END OF ROUND " + (event.newRound() - 1) + " ===" + ColorAnsi.RESET);
        gameModel.updateRound(event.newRound());
    }

    @Override
    public void visit(TribeDto tribe) {
        gameModel.updatePlayerTribe(tribe.nickname(), tribe.tribe());
    }

    @Override
    public void visit(WinnersAnnouncedDto event) {
        gameModel.setWinners(event.winnersNicknames());
    }

    @Override
    public void visit(PlayerLeftGameDto event) {
        gameModel.setGameAborted("The player " + event.nickname() + " disconnected. The match has been cancelled");
    }

    @Override
    public void visit(GameOverDto event) {
        gameModel.setGameOver(event.leaderboard());
    }
    @Override
    public void visit(TotemReturnedDto event) {
        gameModel.returnTotemToTrack(event.nickname(), event.returnIndex());
    }


}