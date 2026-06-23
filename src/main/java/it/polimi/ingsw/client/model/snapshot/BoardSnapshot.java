package it.polimi.ingsw.client.model.snapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Mutable client-side snapshot of visible board cards and round metadata.
 */
public class BoardSnapshot {
    private final List<Integer> upperRowCards = new CopyOnWriteArrayList<>();
    private final List<Integer> lowerRowCards = new CopyOnWriteArrayList<>();
    private int currentEra = 1;
    private int currentRound = 1;
    private Integer nextDeckEra;

    /**
     * Replaces both board rows with the received card identifiers.
     *
     * @param upper upper-row card identifiers
     * @param lower lower-row card identifiers
     */
    public void setCards(List<Integer> upper, List<Integer> lower) {
        upperRowCards.clear();
        upperRowCards.addAll(upper);
        lowerRowCards.clear();
        lowerRowCards.addAll(lower);
    }

    /**
     * Removes the card.
     *
     * @param row board row index
     * @param col card column index
     */
    public void removeCard(int row, int col) {
        if (row == 0 && col >= 0 && col < upperRowCards.size()) {
            upperRowCards.set(col, null);
        } else if (row == 1 && col >= 0 && col < lowerRowCards.size()) {
            lowerRowCards.set(col, null);
        }
    }

    /**
     * Replaces one board row with freshly drawn card identifiers.
     *
     * @param row board row index
     * @param newCardIds replacement card identifiers
     */
    public void refillRow(int row, List<Integer> newCardIds) {
        List<Integer> targetRow = (row == 0) ? upperRowCards : lowerRowCards;
        targetRow.clear();
        targetRow.addAll(newCardIds);
    }

    /**
     * Sets the era.
     *
     * @param era era number
     */
    public void setEra(int era) { this.currentEra = era; }
    /**
     * Sets the round.
     *
     * @param round round number
     */
    public void setRound(int round) { this.currentRound = round; }
    /**
     * Sets the next deck era.
     *
     * @param nextDeckEra next deck era
     */
    public void setNextDeckEra(Integer nextDeckEra) { this.nextDeckEra = nextDeckEra; }

    /**
     * Returns the current era.
     *
     * @return the current era
     */
    public int getCurrentEra() { return currentEra; }
    /**
     * Returns the current round.
     *
     * @return the current round
     */
    public int getCurrentRound() { return currentRound; }
    /**
     * Returns the next deck era.
     *
     * @return the next deck era
     */
    public Integer getNextDeckEra() { return nextDeckEra; }
    /**
     * Returns the upper row cards.
     *
     * @return the upper row cards
     */
    public List<Integer> getUpperRowCards() { return new ArrayList<>(upperRowCards); }
    /**
     * Returns the lower row cards.
     *
     * @return the lower row cards
     */
    public List<Integer> getLowerRowCards() { return new ArrayList<>(lowerRowCards); }
}
