package it.polimi.ingsw.server.model.state;

import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.board.OfferTile;
import it.polimi.ingsw.server.model.card.Card;
import it.polimi.ingsw.server.model.exception.InvalidGameActionException;
import it.polimi.ingsw.server.model.update.AvailableAction;
import it.polimi.ingsw.server.model.update.AvailableAction.*;
import it.polimi.ingsw.server.model.update.GameEvent.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Game state handling the main action phase where players take cards from the board based on their placed totems. */
public class ActionState extends GameState {
    private int currentColumnIndex;
    private Player currentPlayer;
    private OfferTile currentTile;
    private int remainingUpperPicks;
    private int remainingLowerPicks;

    /** Constructs the action state. @param game the game instance */
    public ActionState(Game game) {
        super(game);
    }

    /** Initializes the state and selects the first player based on the offer track. */
    @Override
    public void start() {
        this.currentColumnIndex = 0;
        //this.getAvailableActions(currentPlayer.getNickname());
        findNextPlayer();
    }

    /** Finds the next player with a placed totem and initializes their available actions. */
    private void findNextPlayer() {
        Board board = game.getBoard();
        List<OfferTile> track = board.getOfferTrack();

        while (currentColumnIndex < track.size()) {
            OfferTile tile = track.get(currentColumnIndex);
            if (!tile.isFree()) {
                this.currentPlayer = tile.getOccupyingPlayer()
                        .orElseThrow(() -> new IllegalStateException("The tile is marked as occupied, but the occupant is absent."));
                this.currentTile = tile;
                this.remainingUpperPicks = tile.getUpperRowPicks();
                this.remainingLowerPicks = tile.getLowerRowPicks();

                int bonus = tile.getFoodBonus();
                if (bonus != 0) {
                    this.currentPlayer.addFood(bonus);
                    game.pushEvent(new PlayerResourcesChangedEvent(
                            this.currentPlayer.getNickname(),
                            this.currentPlayer.getFood(),
                            this.currentPlayer.getPrestigePoints(),
                            this.currentPlayer.getFoodDiscount(),
                            this.currentPlayer.getSustenanceDiscount(),
                            "Card bonus offer: +" + bonus + " food"
                    ));
                }
                checkTurnConditions();
                return;
            }
            currentColumnIndex++;
        }
        endRound();
    }

    /** Allows the current player to take a card if valid, handling costs, pick limits, and card effects. @param player acting player @param rowIdx row index (0 upper, 1 lower) @param cardIdx column index */
    @Override
    public void takeCard(Player player, int rowIdx, int cardIdx) {
        if (!player.equals(this.currentPlayer)) {
            throw new InvalidGameActionException("Not your turn");
        }

        if (rowIdx == 0 && remainingUpperPicks <= 0) {
            throw new InvalidGameActionException("No upper picks left");
        }
        if (rowIdx == 1 && remainingLowerPicks <= 0) {
            throw new InvalidGameActionException("No lower picks left");
        }

        Board board = game.getBoard();

        Card targetCard = board.peekCard(rowIdx, cardIdx);
        if (!targetCard.isPickable()) {
            throw new InvalidGameActionException("Can't take event card");
        }
        int finalCost = Math.max(targetCard.getFoodCost() - player.getFoodDiscount(), 0);


        if (player.getFood() < finalCost) {
            throw new InvalidGameActionException("Unsufficient food");
        }

        player.addFood(-finalCost);
        Card purchasedCard = board.takeCard(rowIdx, cardIdx);

        player.addCard(purchasedCard);

        if (rowIdx == 0) remainingUpperPicks--;
        else remainingLowerPicks--;



        game.pushEvent(new CardTakenEvent(player.getNickname(), rowIdx, cardIdx));
        String reason = "";
        if (purchasedCard.isPersistent()) {
            reason = "Buildings Purchase (-" + finalCost + " food)";
        } else if (finalCost > 0) {
            reason = "Character Recruitment (-" + finalCost + " food)";
        }

        game.pushEvent(new PlayerResourcesChangedEvent(
                player.getNickname(),
                player.getFood(),
                player.getPrestigePoints(),
                player.getFoodDiscount(),
                player.getSustenanceDiscount(),
                reason
        ));
        game.pushEvent(new CardAddedToTribeEvent(player.getNickname(), purchasedCard.getIDcard()));

        checkTurnConditions();
    }

    /** Ends the current player's turn, returns their totem, and advances to the next player. */
    public void endPlayerTurn() {
        Board board = game.getBoard();
        currentTile.clearOccupyingPlayer();

        // salvo le risorse PRIMA di muovere il totem
        int foodBefore = this.currentPlayer.getFood();
        int ppBefore = this.currentPlayer.getPrestigePoints();

        //  Muovo il totem (questo applica il +/- cibo in background)
        board.returnTotem(this.currentPlayer);

        // invio l'evento grafico di movimento del totem
        int returnIdx = board.getNextTotemOrderSize() - 1;
        game.pushEvent(new TotemReturnedEvent(this.currentPlayer.getNickname(), returnIdx));

        //  se le risorse sono cambiatemando SUBITO la notifica al client
        int foodDiff = this.currentPlayer.getFood() - foodBefore;
        int prestigeDiff = this.currentPlayer.getPrestigePoints() - ppBefore;

        if (foodDiff != 0 || prestigeDiff != 0) {
            String msg = (foodDiff < 0 || prestigeDiff < 0) ? "Order Tile Penalty" : "Order Tile Bonus";
            game.pushEvent(new PlayerResourcesChangedEvent(
                    this.currentPlayer.getNickname(),
                    this.currentPlayer.getFood(),
                    this.currentPlayer.getPrestigePoints(),
                    this.currentPlayer.getFoodDiscount(),
                    this.currentPlayer.getSustenanceDiscount(),
                    msg
            ));
        }

        currentColumnIndex++;
        findNextPlayer();
    }

    /** Ends the action phase and transitions to the next game state. */
    private void endRound() {
        this.transition(new AdditionalPickState(this.game));
    }



    @Override
    public String getActivePlayerNickname() {
        return this.currentPlayer != null ? this.currentPlayer.getNickname() : null;
    }


    private boolean existsCharacterToPick(int rowIdx) {
        int remaining = (rowIdx == 0) ? remainingUpperPicks : remainingLowerPicks;
        if (remaining <= 0) return false;

        return game.getBoard().getRow(rowIdx).stream()
                .flatMap(Optional::stream)
                .anyMatch(card -> !card.isPersistent() && card.isPickable());
    }

    private boolean canAffordAnyBuildingInRow(int rowIdx) {
        int remaining = (rowIdx == 0) ? remainingUpperPicks : remainingLowerPicks;
        if (remaining <= 0) return false;

        return game.getBoard().getRow(rowIdx).stream()
                .flatMap(Optional::stream)
                .filter(Card::isPersistent) // Solo Edifici
                .anyMatch(card -> {
                    int cost = Math.max(card.getFoodCost() - currentPlayer.getFoodDiscount(), 0);
                    return currentPlayer.getFood() >= cost;
                });
    }


    private void checkTurnConditions() {
        if (currentPlayer == null) return;

        // Condizione base: ha finito i pick?
        boolean picksExhausted = (remainingUpperPicks <= 0 && remainingLowerPicks <= 0);

        // Condizione di stallo: può ancora fare mosse legali?
        boolean canDoMandatory = existsCharacterToPick(0) || existsCharacterToPick(1);
        boolean canDoOptional = canAffordAnyBuildingInRow(0) || canAffordAnyBuildingInRow(1);

        // Se ha finito i pick O non può più fare nulla, il turno finisce
        if (picksExhausted || (!canDoMandatory && !canDoOptional)) {
            endPlayerTurn();
        }
    }




    @Override
    public List<AvailableAction> getAvailableActions(String playerNickname) {
        if (!playerNickname.equals(getActivePlayerNickname())) return List.of();

        List<AvailableAction> actions = new ArrayList<>();
        actions.add(new TakeCardAction(remainingUpperPicks, remainingLowerPicks));

        // Lo SKIP è permesso solo se non ci sono più Personaggi obbligatori da raccogliere
        // nelle righe dove il giocatore ha ancora dei pick.
        boolean mustPickCharacter = existsCharacterToPick(0) || existsCharacterToPick(1);

        if (!mustPickCharacter) {
            actions.add(new SkipAction());
        }

        return actions;
    }

    @Override
    public void skipBonus(Player player) {
        if (!player.equals(this.currentPlayer)) {
            throw new InvalidGameActionException("Not your turn");
        }

        boolean mustPickCharacter = existsCharacterToPick(0) || existsCharacterToPick(1);
        if (mustPickCharacter) {
            throw new InvalidGameActionException("You cannot skip, you must pick a character.");
        }

        // Azzera i pick rimanenti per forzare la fine del turno
        this.remainingUpperPicks = 0;
        this.remainingLowerPicks = 0;

        endPlayerTurn();

        // Notifica l'accredito/addebito del cibo per aver riposizionato il totem
       /* game.pushEvent(new PlayerResourcesChangedEvent(
                player.getNickname(),
                player.getFood(),
                player.getPrestigePoints(),
                player.getFoodDiscount(),
                "Ritorno Totem (Skip)"
        ));

        */
    }

}
