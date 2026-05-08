package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.update.AvailableAction;
import it.polimi.ingsw.server.model.update.BoardUpdate;
import it.polimi.ingsw.server.model.update.ModelUpdate;
import it.polimi.ingsw.server.model.update.PlayerUpdate;

import java.util.List;

public interface ModelObserver {
    // Chiamato per ogni mossa/cambiamento
    void onModelUpdate(ModelUpdate update);

    // Chiamato solo all'inizio o per ripristinare un client
    void onFullSync(BoardUpdate board, List<PlayerUpdate> players, String activePlayer, List<AvailableAction> actions);
}