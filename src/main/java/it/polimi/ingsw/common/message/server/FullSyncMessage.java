package it.polimi.ingsw.common.message.server;

import java.io.Serial;

import it.polimi.ingsw.common.network.dto.InitTurnOrderTileDto;
import it.polimi.ingsw.common.network.dto.action.ActionDto;
import it.polimi.ingsw.common.network.dto.BoardDto;
import it.polimi.ingsw.common.network.dto.PlayerDto;
import it.polimi.ingsw.common.visitor.ClientMessageVisitor;

import java.util.List;

/**
 * Immutable data transfer object for full sync message.
 *
 * @param board board snapshot
 * @param players players involved in the update
 * @param activePlayer active player nickname
 * @param actions available actions
 * @param turnOrderTileDto initial turn-order tile
 */
public record FullSyncMessage(BoardDto board,
                              List<PlayerDto> players,
                              String activePlayer,
                              List<ActionDto> actions,
                              InitTurnOrderTileDto turnOrderTileDto) implements ServerMessage {
    @Serial
    private static final long serialVersionUID = 1L;

    /** {@inheritDoc} */
    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
