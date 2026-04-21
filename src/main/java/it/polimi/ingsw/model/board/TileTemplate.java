package it.polimi.ingsw.model.board;

public enum TileTemplate {
    A('A', 0, 0, 3),
    B('B', 0, 1, 0),
    C('C', 1, 0, 0),
    D('D', 0, 2, 0),
    E('E', 1, 1, 0),
    F('F', 2, 0, 0),
    G('G', 2, 1, 0);

    private final char id;
    private final int upperPicks;
    private final int lowerPicks;
    private final int foodModifier;

    TileTemplate(char id, int upperPicks, int lowerPicks, int foodModifier) {
        this.id = id;
        this.upperPicks = upperPicks;
        this.lowerPicks = lowerPicks;
        this.foodModifier = foodModifier;
    }
    public OfferTile createTile() {
        return new OfferTile(this.id, this.upperPicks, this.lowerPicks, this.foodModifier);
    }
}