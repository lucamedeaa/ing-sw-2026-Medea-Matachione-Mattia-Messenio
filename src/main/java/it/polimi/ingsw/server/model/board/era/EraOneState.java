package it.polimi.ingsw.server.model.board.era;

import it.polimi.ingsw.server.model.board.Board;

/**
 * Board era state for era one.
 */
public class EraOneState implements EraState {
    /** {@inheritDoc} */
    @Override
    public int getEraNumber() { return 1; }

    /** {@inheritDoc} */
    @Override
    public void transitionSetup(Board board) {
    }

    /** {@inheritDoc} */
    @Override
    public EraState getNextEra() {
        return new EraTwoState();
    }
}