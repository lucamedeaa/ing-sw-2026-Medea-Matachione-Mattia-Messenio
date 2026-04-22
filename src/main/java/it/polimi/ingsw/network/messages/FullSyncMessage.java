package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.dto.BoardDTO;
import it.polimi.ingsw.network.dto.PlayerDTO;

import java.util.List;

public record FullSyncMessage(
        BoardDTO board,
        List<PlayerDTO> players,
        String activePlayerNickname
)implements ServerMessage {
    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}

