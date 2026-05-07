package it.polimi.ingsw.virtualView;

import it.polimi.ingsw.model.ModelObserver;
import it.polimi.ingsw.model.updates.AvailableAction;
import it.polimi.ingsw.model.updates.BoardUpdate;
import it.polimi.ingsw.model.updates.ModelUpdate;
import it.polimi.ingsw.model.updates.PlayerUpdate;
import it.polimi.ingsw.network.dto.*;
import it.polimi.ingsw.network.server.ClientProxy;

import java.util.List;
//import java.util.concurrent.TimeUnit;

public class VirtualView implements ModelObserver {

    private final String nickname;
    private final ClientProxy client;

    public VirtualView(String nickname, ClientProxy client) {
        this.nickname = nickname;
        this.client = client;
    }

    @Override
    public void onModelUpdate(ModelUpdate modelUpdate) {
        ModelUpdateDTO update =  modelUpdate.toDTO();
        List<AvailableActionDTO> myActions;

        // Il mio nickname è uguale a tizio a? Mando le azioni.
        if (this.nickname.equals(update.activePlayerNickname())) {
            myActions = update.activePlayerActions();
            //startTurnTimer();
        } else {
            // Altrimenti mando una lista vuota (nessuna azione permessa)
            myActions = List.of();
            //cancelTurnTimer();
        }

        // Mando SEMPRE l'evento, così la UI degli altri si aggiorna
        client.deltaEvent(update.events(), myActions, update.activePlayerNickname());
    }

    //Inviata solo all'inizio o riconness@Override
    @Override
    public void onFullSync(BoardUpdate boardUpdate, List<PlayerUpdate> playersUpdates, String activePlayer, List<AvailableAction> actionsUpdates) {
        BoardDTO board = boardUpdate.toDTO();
        List<PlayerDTO> players = playersUpdates.stream().map(PlayerUpdate::toDTO).toList();
        List<AvailableActionDTO> actions = actionsUpdates.stream().map(AvailableAction::toDTO).toList();

        // Invia le azioni solo se il nickname della vista corrisponde al giocatore attivo
        List<AvailableActionDTO> myActions = this.nickname.equals(activePlayer) ? actions : List.of();

        client.fullSync(board, players, activePlayer, myActions);
    }

}
