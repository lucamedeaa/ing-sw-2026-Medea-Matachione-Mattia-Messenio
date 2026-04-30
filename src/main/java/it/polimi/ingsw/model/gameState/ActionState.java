package it.polimi.ingsw.model.gameState;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.OfferTile;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.network.dto.AvailableActionDTO;
import it.polimi.ingsw.network.dto.actions.SkipActionDTO;
import it.polimi.ingsw.network.dto.actions.TakeCardActionDTO;
import it.polimi.ingsw.network.dto.events.*;

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

    /** Allows the current player to take a card if valid, handling costs, pick limits, and card effects. @param player acting player @param rowIdx row index (0 upper, 1 lower) @param cardIdx column index */
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

        player.addCard(purchasedCard);

        if (rowIdx == 0) remainingUpperPicks--;
        else remainingLowerPicks--;

        checkTurnConditions();

        game.notifyObservers(new CardTakenEventDTO(player.getNickname(), rowIdx, cardIdx));
        game.notifyObservers(new PlayerResourcesChangedEventDTO(player.getNickname(), player.getFood(), player.getPrestigePoints()));
        game.notifyObservers(new CardAddedToTribeEventDTO(player.getNickname(), purchasedCard.getIDcard()));


    }

    /** Ends the current player's turn, returns their totem, and advances to the next player. */
    public void endPlayerTurn() {
        Board board = game.getBoard();
        currentTile.clearOccupyingPlayer();
        board.returnTotem(this.currentPlayer);
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
                .anyMatch(card -> !card.isPersistent()); // Personaggi
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

        // 1. Condizione base: ha finito i pick?
        boolean picksExhausted = (remainingUpperPicks <= 0 && remainingLowerPicks <= 0);

        // 2. Condizione di stallo: può ancora fare mosse legali?
        boolean canDoMandatory = existsCharacterToPick(0) || existsCharacterToPick(1);
        boolean canDoOptional = canAffordAnyBuildingInRow(0) || canAffordAnyBuildingInRow(1);

        // Se ha finito i pick O non può più fare nulla, il turno finisce
        if (picksExhausted || (!canDoMandatory && !canDoOptional)) {
            endPlayerTurn();
        }
    }




    @Override
    public List<AvailableActionDTO> getAvailableActions(String playerNickname) {
        if (!playerNickname.equals(getActivePlayerNickname())) return List.of();

        List<AvailableActionDTO> actions = new ArrayList<>();
        actions.add(new TakeCardActionDTO(remainingUpperPicks, remainingLowerPicks));

        // Lo SKIP è permesso solo se non ci sono più Personaggi obbligatori da raccogliere
        // nelle righe dove il giocatore ha ancora dei pick.
        boolean mustPickCharacter = existsCharacterToPick(0) || existsCharacterToPick(1);

        if (!mustPickCharacter) {
            actions.add(new SkipActionDTO());
        }

        return actions;
    }

}