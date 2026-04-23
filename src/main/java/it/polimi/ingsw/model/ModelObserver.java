package it.polimi.ingsw.model;

import it.polimi.ingsw.network.dto.AvailableActionDTO;
import it.polimi.ingsw.network.dto.BoardDTO;
import it.polimi.ingsw.network.dto.GameEventDTO;
import it.polimi.ingsw.network.dto.PlayerDTO;

import java.util.List;

public interface ModelObserver {
    // Chiamato per ogni mossa/cambiamento
    void onModelUpdate(GameEventDTO event, List<AvailableActionDTO> availableActions);

    // Chiamato solo all'inizio o per ripristinare un client
    void onFullSync(BoardDTO board, List<PlayerDTO> players, String activePlayer);
}