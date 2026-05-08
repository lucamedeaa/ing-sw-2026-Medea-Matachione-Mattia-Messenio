package it.polimi.ingsw.server.model.board.era;

import it.polimi.ingsw.server.model.board.Board;

public class EraThreeState implements EraState {
    @Override
    public int getEraNumber() { return 3; }

    @Override
    public void transitionSetup(Board board) {
        board.clearBuildingsFromLowerRow();
        board.shiftBuildingsToBottomRow();
        board.setupNewEraBuildings();
    }

    @Override
    public EraState getNextEra() {
        return this;
    }
}