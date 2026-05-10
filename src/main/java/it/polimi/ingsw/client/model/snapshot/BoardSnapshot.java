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

    public void setCards(List<Integer> upper, List<Integer> lower) {
        upperRowCards.clear();
        upperRowCards.addAll(upper);
        lowerRowCards.clear();
        lowerRowCards.addAll(lower);
    }

    public void removeCard(int row, int col) {
        if (row == 0) upperRowCards.set(col, null);
        else lowerRowCards.set(col, null);
    }

    public void refillRow(int row, List<Integer> newCardIds) {
        List<Integer> targetRow = (row == 0) ? upperRowCards : lowerRowCards;
        targetRow.clear();
        targetRow.addAll(newCardIds);
    }

    public void setEra(int era) { this.currentEra = era; }
    public void setRound(int round) { this.currentRound = round; }

    public int getCurrentEra() { return currentEra; }
    public int getCurrentRound() { return currentRound; }
    public List<Integer> getUpperRowCards() { return new ArrayList<>(upperRowCards); }
    public List<Integer> getLowerRowCards() { return new ArrayList<>(lowerRowCards); }
}