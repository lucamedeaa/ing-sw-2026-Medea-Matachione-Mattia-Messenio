package it.polimi.ingsw.server.model.board.era;

import it.polimi.ingsw.server.model.board.Board;

public interface EraState {
    int getEraNumber();
    void transitionSetup(Board board);
    EraState getNextEra();
}