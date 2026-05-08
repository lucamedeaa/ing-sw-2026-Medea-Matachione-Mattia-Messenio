package it.polimi.ingsw.server.model.board;

/**
 * Enumeration of predefined tile templates used to create {@link OfferTile} instances.
 * <p>
 * Each template defines:
 * <ul>
 *     <li>An identifier</li>
 *     <li>The number of upper picks</li>
 *     <li>The number of lower picks</li>
 *     <li>A food modifier</li>
 * </ul>
 */
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

    /**
     * Constructs a tile template.
     * @param id template identifier
     * @param upperPicks number of upper picks
     * @param lowerPicks number of lower picks
     * @param foodModifier food modifier applied by the tile
     */
    TileTemplate(char id, int upperPicks, int lowerPicks, int foodModifier) {
        this.id = id;
        this.upperPicks = upperPicks;
        this.lowerPicks = lowerPicks;
        this.foodModifier = foodModifier;
    }

    /**
     * Creates a new {@link OfferTile} instance based on this template.
     * @return a new OfferTile with the template's parameters
     */
    public OfferTile createTile() {
        return new OfferTile(this.id, this.upperPicks, this.lowerPicks, this.foodModifier);
    }
}