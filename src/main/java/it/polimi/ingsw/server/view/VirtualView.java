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
import it.polimi.ingsw.server.network.ConnectionContext;

import java.util.List;

/**
 * Observer that adapts model updates to client-specific network updates.
 */
public class VirtualView implements ModelObserver {

    private final String nickname;
    private final ConnectionContext session;

    /**
     * Creates a virtual view for one player connection.
     *
     * @param nickname player nickname represented by this view
     * @param session connection session used to enqueue outbound client messages
     */
    public VirtualView(String nickname, ConnectionContext session) {
        this.nickname = nickname;
        this.session = session;
    }

    @Override
    public void onModelUpdate(ModelUpdate modelUpdate) {
        ModelUpdateDto update = modelUpdate.toDTO();
        List<ActionDto> myActions;

        if (this.nickname.equals(update.activePlayerNickname())) {
            myActions = update.activePlayerActions();
        } else {
            myActions = List.of();
        }

        session.deltaEvent(update.events(), myActions, update.activePlayerNickname());
    }

    @Override
    public void onFullSync(BoardUpdate boardUpdate, List<PlayerUpdate> playersUpdates, String activePlayer, List<AvailableAction> actionsUpdates) {
        BoardDto board = boardUpdate.toDTO();
        List<PlayerDto> players = playersUpdates.stream().map(PlayerUpdate::toDTO).toList();
        List<ActionDto> actions = actionsUpdates.stream().map(AvailableAction::toDTO).toList();

        List<ActionDto> myActions = this.nickname.equals(activePlayer) ? actions : List.of();

        session.fullSync(board, players, activePlayer, myActions);
    }

}
