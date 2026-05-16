package it.polimi.ingsw.client.view.gui.controllers.board;

public interface BoardSelectionListener {
    void onCardSelected(int row, int col);
    void onTotemPositionSelected(int tileIndex);
}