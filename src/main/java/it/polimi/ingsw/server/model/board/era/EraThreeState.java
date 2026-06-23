package it.polimi.ingsw.server.model.board.era;

import it.polimi.ingsw.server.model.board.Board;

/**
 * Board era state for era three.
 */
public class EraThreeState implements EraState {
    /** {@inheritDoc} */
    @Override
    public int getEraNumber() { return 3; }

    /** {@inheritDoc} */
    @Override
    public void transitionSetup(Board board) {
        board.clearBuildingsFromLowerRow();
        board.shiftBuildingsToBottomRow();
        board.setupNewEraBuildings();
    }

    /** {@inheritDoc} */
    @Override
    public EraState getNextEra() {
        return this;
    }
}