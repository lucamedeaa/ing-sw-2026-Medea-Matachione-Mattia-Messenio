package it.polimi.ingsw.server.view;

import it.polimi.ingsw.common.network.dto.BoardDto;
import it.polimi.ingsw.common.network.dto.ModelUpdateDto;
import it.polimi.ingsw.common.network.dto.PlayerDto;
import it.polimi.ingsw.server.model.ModelObserver;
import it.polimi.ingsw.server.model.update.AvailableAction;
import it.polimi.ingsw.server.model.update.BoardUpdate;
import it.polimi.ingsw.server.model.update.ModelUpdate;
import it.polimi.ingsw.server.model.update.PlayerUpdate;
import it.polimi.ingsw.common.network.dto.action.ActionDto;
import it.polimi.ingsw.server.network.ClientProxy;

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
        ModelUpdateDto update =  modelUpdate.toDTO();
        List<ActionDto> myActions;

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
        BoardDto board = boardUpdate.toDTO();
        List<PlayerDto> players = playersUpdates.stream().map(PlayerUpdate::toDTO).toList();
        List<ActionDto> actions = actionsUpdates.stream().map(AvailableAction::toDTO).toList();

        // Invia le azioni solo se il nickname della vista corrisponde al giocatore attivo
        List<ActionDto> myActions = this.nickname.equals(activePlayer) ? actions : List.of();

        client.fullSync(board, players, activePlayer, myActions);
    }

}
