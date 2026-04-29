package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.dto.AvailableActionDTO;
import it.polimi.ingsw.network.dto.BoardDTO;
import it.polimi.ingsw.network.dto.PlayerDTO;
import it.polimi.ingsw.network.visitor.ClientMessageVisitor;

import java.util.List;

public record FullSyncMessage(BoardDTO board,
                              List<PlayerDTO> players,
                              String activePlayer,
                              List<AvailableActionDTO> actions) implements ServerMessage {
    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}