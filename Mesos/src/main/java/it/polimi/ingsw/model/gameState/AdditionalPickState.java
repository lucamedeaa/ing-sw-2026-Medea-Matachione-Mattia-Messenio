package it.polimi.ingsw.model.gameState;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;

import java.util.List;

public class AdditionalPickState extends GameState {
    private int currentPlayerIndex;
    private Player currentPlayer;
    private int remainingUpperPicks;

    public AdditionalPickState(Game game) {
        super(game);
    }

    @Override
    public void start() {
        this.currentPlayerIndex = 0;
        findNextPlayer();
    }

    private void findNextPlayer() {
        List<Player> players = game.getPlayers();

        while (currentPlayerIndex < players.size()) {
            Player p = players.get(currentPlayerIndex);
            int bonus = p.getTopRowBonus();

            if (bonus > 0) {
                this.currentPlayer = p;
                this.remainingUpperPicks = bonus;
                return;
            }
            currentPlayerIndex++;
        }
        endBonusPhase();
    }

    @Override
    public void takeCard(Player player, int rowIdx, int cardIdx) {
        if (!player.equals(this.currentPlayer)) {
            throw new IllegalStateException("Non è il tuo turno bonus");
        }

        if (rowIdx != 0) {
            throw new IllegalStateException("Il bonus permette di pescare solo dalla fila superiore");
        }

        if (remainingUpperPicks <= 0) {
            throw new IllegalStateException("Nessun pick bonus rimasto");
        }

        Board board = game.getBoard();
        Card targetCard = board.peekCard(rowIdx, cardIdx);

        if (!targetCard.isPickable()) {
            throw new IllegalStateException("Non puoi prendere carte evento con questo bonus");
        }

        int finalCost = Math.max(targetCard.getFoodCost() - player.getFoodDiscount(), 0);
        if (player.getFood() < finalCost) {
            throw new IllegalStateException("Cibo insufficiente per riscattare il bonus");
        }

        player.addFood(-finalCost);
        Card purchasedCard = board.takeCard(rowIdx, cardIdx);
        player.addCard((DrawableCard) purchasedCard);
        remainingUpperPicks--;

        if (remainingUpperPicks <= 0) {
            goToNextPlayer();
        }
    }

    @Override
    public void skipBonus(Player player) {
        if (!player.equals(this.currentPlayer)) {
            throw new IllegalStateException("Non è il tuo turno");
        }
        goToNextPlayer();
    }

    private void goToNextPlayer() {
        currentPlayerIndex++;
        findNextPlayer();
    }

    private void endBonusPhase() {
        this.transition(new RoundEndState(this.game));
    }
}