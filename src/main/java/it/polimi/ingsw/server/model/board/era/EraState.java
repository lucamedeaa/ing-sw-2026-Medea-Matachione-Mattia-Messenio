package it.polimi.ingsw.server.model.board.era;

import it.polimi.ingsw.server.model.board.Board;

/**
 * State object that applies era-specific setup rules to the board.
 */
public interface EraState {
    /**
     * Returns the numeric era represented by this state.
     *
     * @return era number
     */
    int getEraNumber();

    /**
     * Applies setup changes required when this era becomes active.
     *
     * @param board board to update
     */
    void transitionSetup(Board board);

    /**
     * Returns the next era state.
     *
     * @return next era state, or this state if no later era exists
     */
    EraState getNextEra();
}