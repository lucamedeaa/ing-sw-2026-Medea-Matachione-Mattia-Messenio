package it.polimi.ingsw.server.model.board.era;

import it.polimi.ingsw.server.model.board.Board;

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