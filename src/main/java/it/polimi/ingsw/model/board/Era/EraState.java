package it.polimi.ingsw.model.board.Era;

import it.polimi.ingsw.model.board.Board;

public interface EraState {
    int getEraNumber();
    void transitionSetup(Board board);
    EraState getNextEra();
}