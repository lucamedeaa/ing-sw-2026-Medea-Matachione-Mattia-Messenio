package it.polimi.ingsw.model.board.Era;

import it.polimi.ingsw.model.board.Board;

public class EraOneState implements EraState {
    @Override
    public int getEraNumber() { return 1; }

    @Override
    public void transitionSetup(Board board) {
    }

    @Override
    public EraState getNextEra() {
        return new EraTwoState();
    }
}