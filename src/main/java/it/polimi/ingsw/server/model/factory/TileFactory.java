package it.polimi.ingsw.server.model.factory;

import it.polimi.ingsw.server.model.board.OfferTile;
import it.polimi.ingsw.server.model.board.TileTemplate;

import java.util.List;
import java.util.Map;

/** Factory class responsible for creating the offer track based on the number of players. */
public class TileFactory {

    /** Maps player count to the corresponding sequence of tile templates. */
    private static final Map<Integer, String> TILE_LAYOUTS = Map.of(
            2, "BCEF",
            3, "BCDEF",
            4, "BCDEFG",
            5, "ABCDEFG"
    );

    /** Creates the offer track using predefined layouts and tile templates. @param playerCount number of players @return list of OfferTile objects forming the track @throws IllegalArgumentException if the player count is invalid */
    public static List<OfferTile> createOfferTrack(int playerCount) {
        String layout = TILE_LAYOUTS.get(playerCount);
        if (layout == null) {
            throw new IllegalArgumentException("Players number invalid: " + playerCount);
        }

        return layout.chars()
                .mapToObj(c -> String.valueOf((char) c))
                .map(TileTemplate::valueOf)
                .map(TileTemplate::createTile)
                .toList();
    }
}