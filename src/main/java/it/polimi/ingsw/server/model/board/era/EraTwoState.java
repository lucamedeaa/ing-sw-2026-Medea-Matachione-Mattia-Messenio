package it.polimi.ingsw.server.model.board.era;

import it.polimi.ingsw.server.model.board.Board;

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