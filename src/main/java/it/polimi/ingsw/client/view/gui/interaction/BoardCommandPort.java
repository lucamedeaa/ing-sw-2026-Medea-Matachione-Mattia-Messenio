package it.polimi.ingsw.client.view.gui.interaction;

public interface BoardCommandPort {
    void takeCard(int row, int col);
    void placeTotem(int position);
}