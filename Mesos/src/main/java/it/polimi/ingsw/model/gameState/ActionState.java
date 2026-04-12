package it.polimi.ingsw.model.gameState;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.OfferTile;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;

import java.util.List;

public class ActionState extends GameState {
    private int currentColumnIndex;
    private Player currentPlayer;
    private OfferTile currentTile;
    private int remainingUpperPicks;
    private int remainingLowerPicks;

    public ActionState(Game game) {
        super(game);
    }


    @Override
    public void start() {
        this.currentColumnIndex = 0;
        findNextPlayer();
    }

    private void findNextPlayer() {
        Board board = game.getBoard();
        List<OfferTile> track = board.getOfferTrack();

        while (currentColumnIndex < track.size()) {
            OfferTile tile = track.get(currentColumnIndex);
            if (!tile.isFree()) {
                this.currentPlayer = tile.getOccupyingPlayer().get();
                this.currentTile = tile;
                this.remainingUpperPicks = tile.getUpperRowPicks();
                this.remainingLowerPicks = tile.getLowerRowPicks();
                this.currentPlayer.addFood(tile.getFoodBonus());
                return;
            }
            currentColumnIndex++;
        }
        endRound();
    }


    @Override
    public void takeCard(Player player, int rowIdx, int cardIdx) {
        if (!player.equals(this.currentPlayer)) {
            throw new IllegalStateException("Not your turn");
        }

        if (rowIdx == 0 && remainingUpperPicks <= 0) {
            throw new IllegalStateException("No upper picks left");
        }
        if (rowIdx == 1 && remainingLowerPicks <= 0) {
            throw new IllegalStateException("No lower picks left");
        }

        Board board = game.getBoard();

        Card targetCard = board.peekCard(rowIdx, cardIdx);
        if (!targetCard.isPickable()) {
            throw new IllegalStateException("Can't take event card");
        }

        int finalCost = Math.max(targetCard.getFoodCost() - player.getFoodDiscount(), 0);

        if (player.getFood() < finalCost) {
            throw new IllegalStateException("Unsufficient food");
        }

        player.addFood(-finalCost);
        Card purchasedCard = board.takeCard(rowIdx, cardIdx);

        /*TODO sistemare qua che andrebbe fatto casting, imo avrebbe senso mettere il deck come deck di carte normali nel player tanto cambia poco
        *  pero forse alcuni metodi andrebbero definiti in card e drawable card non avrebbe senso di esister. gia ne ha poco dato che prima di pescare bisogna usare
        * il metodo isPickable sulla carta quindi boh, leverei quella classe e fine*/
        player.addCard(purchasedCard);
        if (rowIdx == 0) remainingUpperPicks--;
        else remainingLowerPicks--;
        if (remainingUpperPicks <= 0 && remainingLowerPicks <= 0) {
            endPlayerTurn();
        }
    }

    public void endPlayerTurn() {
        Board board = game.getBoard();
        currentTile.clearOccupyingPlayer();
        board.returnTotem(this.currentPlayer);
        currentColumnIndex++;
        findNextPlayer();
    }

    private void endRound() {
        this.transition(new AdditionalPickState(this.game));
    }
}
