package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.network.dto.InitTurnOrderTileDto;
import it.polimi.ingsw.server.model.update.*;

import java.util.List;

/**
 * Observer receiving full and incremental server-model updates.
 */
public interface ModelObserver {
    /**
     * Called after one or more gameplay events have changed the model.
     *
     * @param update incremental model update
     */
    void onModelUpdate(ModelUpdate update);

    /**
     * Called when a client needs a complete state snapshot.
     *
     * @param board board state
     * @param players player states
     * @param activePlayer active player nickname, or null if no action is pending
     * @param actions actions available to the active player
     */
    void onFullSync(BoardUpdate board, List<PlayerUpdate> players, String activePlayer, List<AvailableAction> actions, InitTurnOrderTileUpdate turnOrderTile);
}