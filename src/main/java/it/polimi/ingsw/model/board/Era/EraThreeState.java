package it.polimi.ingsw.model.board.Era;

import it.polimi.ingsw.model.board.Board;

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