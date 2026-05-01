package it.polimi.ingsw.model;

import it.polimi.ingsw.model.updates.AvailableAction;
import it.polimi.ingsw.model.updates.BoardUpdate;
import it.polimi.ingsw.model.updates.ModelUpdate;
import it.polimi.ingsw.model.updates.PlayerUpdate;
import it.polimi.ingsw.network.dto.AvailableActionDTO;
import it.polimi.ingsw.network.dto.BoardDTO;
import it.polimi.ingsw.network.dto.ModelUpdateDTO;
import it.polimi.ingsw.network.dto.PlayerDTO;
import java.util.List;

public interface ModelObserver {
    // Chiamato per ogni mossa/cambiamento
    void onModelUpdate(ModelUpdate update);

    // Chiamato solo all'inizio o per ripristinare un client
    void onFullSync(BoardUpdate board, List<PlayerUpdate> players, String activePlayer, List<AvailableAction> actions);
}