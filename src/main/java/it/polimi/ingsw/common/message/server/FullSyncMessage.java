package it.polimi.ingsw.common.message.server;

import java.io.Serial;

import it.polimi.ingsw.common.network.dto.InitTurnOrderTileDto;
import it.polimi.ingsw.common.network.dto.action.ActionDto;
import it.polimi.ingsw.common.network.dto.BoardDto;
import it.polimi.ingsw.common.network.dto.PlayerDto;
import it.polimi.ingsw.common.visitor.ClientMessageVisitor;

import java.util.List;

public record FullSyncMessage(BoardDto board,
                              List<PlayerDto> players,
                              String activePlayer,
                              List<ActionDto> actions,
                              InitTurnOrderTileDto turnOrderTileDto) implements ServerMessage {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
