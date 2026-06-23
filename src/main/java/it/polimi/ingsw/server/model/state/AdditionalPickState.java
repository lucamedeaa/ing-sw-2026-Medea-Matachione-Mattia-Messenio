package it.polimi.ingsw.server.model.state;

import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.card.Card;
import it.polimi.ingsw.server.model.exception.InvalidGameActionException;
import it.polimi.ingsw.server.model.update.AvailableAction;
import it.polimi.ingsw.server.model.update.AvailableAction.*;
import it.polimi.ingsw.server.model.update.GameEvent.*;

import java.util.List;
import java.util.Optional;

/** Game state handling additional picks from the upper row granted by bonuses after the main action phase. */
public class AdditionalPickState extends GameState {
    private int currentPlayerIndex;
    private Player currentPlayer;
    private int remainingUpperPicks;

    /** Constructs the additional pick state. @param game the game instance */
    public AdditionalPickState(Game game) {
        super(game);
    }

    /** Initializes the state and selects the first player eligible for bonus picks. */
    @Override
    public void start() {
        this.currentPlayerIndex = 0;
        findNextPlayer();
    }

    /** Finds the next player with available top-row bonus picks. */
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

    /** Allows the current player to take a card from the upper row using bonus picks. @param player acting player @param rowIdx must be 0 (upper row) @param cardIdx column index */
    @Override
    public void takeCard(Player player, int rowIdx, int cardIdx) {
        if (!player.equals(this.currentPlayer)) {
            throw new InvalidGameActionException("Not your bonus turn");
        }

        if (rowIdx != 0) {
            throw new InvalidGameActionException("Bonus picks are allowed only from the upper row");
        }

        if (remainingUpperPicks <= 0) {
            throw new InvalidGameActionException("No bonus picks left");
        }

        Board board = game.getBoard();
        Card targetCard = board.peekCard(rowIdx, cardIdx);

        if (!targetCard.isPickable()) {
            throw new InvalidGameActionException("Cannot take event cards with bonus picks");
        }

        int finalCost = Math.max(targetCard.getFoodCost() - player.getFoodDiscount(), 0);


        if (player.getFood() < finalCost) {
            throw new InvalidGameActionException("Insufficient food");
        }

        player.addFood(-finalCost);
        Card purchasedCard = board.takeCard(rowIdx, cardIdx);
        player.addCard(purchasedCard);
        remainingUpperPicks--;

        game.pushEvent(new CardTakenEvent(player.getNickname(), rowIdx, cardIdx));


        game.pushEvent(new PlayerResourcesChangedEvent(
                player.getNickname(),
                player.getFood(),
                player.getPrestigePoints(),
                player.getFoodDiscount(),
                player.getSustenanceDiscount(),
                "Buildings Purchase (-" + finalCost + " food)"
        ));

        game.pushEvent(new CardAddedToTribeEvent(player.getNickname(), purchasedCard.getIDcard()));

        if (remainingUpperPicks <= 0 || !canAffordAnyPickableCardInUpperRow()) {
            if (remainingUpperPicks > 0) {
                game.pushEvent(new PlayerResourcesChangedEvent(
                        player.getNickname(),
                        player.getFood(),
                        player.getPrestigePoints(),
                        player.getFoodDiscount(),
                        player.getSustenanceDiscount(),
                        "Automatic Skip: no affordable cards left for bonus picks"
                ));
            }
            goToNextPlayer();
        }
    }

    private boolean canAffordAnyPickableCardInUpperRow() {
        if (remainingUpperPicks <= 0) return false;
        return game.getBoard().getRow(0).stream()
                .flatMap(Optional::stream)
                .filter(Card::isPickable)
                .anyMatch(card -> {
                    int cost = Math.max(card.getFoodCost() - currentPlayer.getFoodDiscount(), 0);
                    return currentPlayer.getFood() >= cost;
                });
    }

    /** Allows the current player to skip their remaining bonus picks. @param player acting player */
    @Override
    public void skipBonus(Player player) {
        if (!player.equals(this.currentPlayer)) {
            throw new InvalidGameActionException("Not your turn");
        }

        this.remainingUpperPicks = 0;
        game.pushEvent(new PlayerResourcesChangedEvent(
                player.getNickname(),
                player.getFood(),
                player.getPrestigePoints(),
                player.getFoodDiscount(),
                player.getSustenanceDiscount(),
                "Voluntary Skip: bonus picks skipped"
        ));
        goToNextPlayer();
    }

    /** Advances to the next eligible player. */
    private void goToNextPlayer() {
        currentPlayerIndex++;
        findNextPlayer();
    }

    /** Ends the bonus phase and transitions to the round end state. */
    private void endBonusPhase() {
        this.transition(new RoundEndState(this.game));
    }

    /** {@inheritDoc} */
    @Override
    public List<AvailableAction> getAvailableActions(String playerNickname) {
        if (playerNickname.equals(getActivePlayerNickname())) {
            return List.of(new TakeCardAction(1,0), new SkipAction());
        }
        return List.of();
    }

    /** {@inheritDoc} */
    @Override
    public String getActivePlayerNickname() {
        return this.currentPlayer != null ? this.currentPlayer.getNickname() : null;
    }
}
