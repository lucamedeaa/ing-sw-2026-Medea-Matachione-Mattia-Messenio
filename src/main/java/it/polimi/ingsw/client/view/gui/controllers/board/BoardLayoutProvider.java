package it.polimi.ingsw.client.view.gui.controllers.board;

import java.util.List;

/**
 * Provides GUI board layout data for different player counts.
 */
public final class BoardLayoutProvider {
    private BoardLayoutProvider() {}

    /**
     * Returns the visual tile layout for the given player count.
     *
     * @param playerCount number of players
     * @return ordered tile resource names
     */
    public static List<String> getTileLayout(int playerCount) {
        return switch (playerCount) {
            default -> List.of("TURNORDER_TILE_2","TILE_B","TILE_C","TILE_E","TILE_F");
            case 3  -> List.of("TURNORDER_TILE_3","TILE_B","TILE_C","TILE_D","TILE_E","TILE_F");
            case 4  -> List.of("TURNORDER_TILE_4","TILE_B","TILE_C","TILE_D","TILE_E","TILE_F","TILE_G");
            case 5  -> List.of("TURNORDER_TILE_5","TILE_A","TILE_B","TILE_C","TILE_D","TILE_E","TILE_F","TILE_G");
        };
    }

    // vertical position of each totem as a fraction of tile height`
    /**
     * Returns vertical totem placement fractions for the return area.
     *
     * @param playerCount number of players
     * @return vertical placement fractions
     */
    public static double[] getTotemYSteps(int playerCount) {
        return switch (playerCount) {
            case 2  -> new double[]{0.23, 0.40};
            case 3  -> new double[]{0.16, 0.35, 0.54};
            case 4  -> new double[]{0.12, 0.32, 0.50, 0.68};
            default -> new double[]{0.06, 0.24, 0.42, 0.61, 0.80};
        };
    }

    /**
     * Returns the visual grid column for a building deck.
     *
     * @param playerCount number of players
     * @param era building deck era
     * @return grid column index
     */
    public static int getBuildingDeckColumn(int playerCount, int era) {
        int baseTrackSize = getTileLayout(playerCount).size();
        return baseTrackSize + (era-1);
    }
}
