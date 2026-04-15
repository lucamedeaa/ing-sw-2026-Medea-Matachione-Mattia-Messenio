package it.polimi.ingsw.model.board.Era;

import it.polimi.ingsw.model.board.Board;

public class EraTwoState implements EraState {
    @Override
    public int getEraNumber() { return 2; }

    @Override
    public void transitionSetup(Board board) {
        board.shiftBuildingsToBottomRow();
        board.setupNewEraBuildings();
    }

    @Override
    public EraState getNextEra() {
        return new EraThreeState();
    }
}