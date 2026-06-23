package it.polimi.ingsw.server.model.board.era;

import it.polimi.ingsw.server.model.board.Board;

/**
 * Board era state for era two.
 */
public class EraTwoState implements EraState {
    /** {@inheritDoc} */
    @Override
    public int getEraNumber() { return 2; }

    /** {@inheritDoc} */
    @Override
    public void transitionSetup(Board board) {
        board.shiftBuildingsToBottomRow();
        board.setupNewEraBuildings();
    }

    /** {@inheritDoc} */
    @Override
    public EraState getNextEra() {
        return new EraThreeState();
    }
}