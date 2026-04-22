package it.polimi.ingsw.model.Factory;


import it.polimi.ingsw.model.board.OfferTile;
import it.polimi.ingsw.model.board.TileTemplate;

import java.util.List;
import java.util.Map;

public class TileFactory {

    private static final Map<Integer, String> TILE_LAYOUTS = Map.of(
            2, "BCEF",
            3, "BCDEF",
            4, "BCDEFG",
            5, "ABCDEFG"
    );

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