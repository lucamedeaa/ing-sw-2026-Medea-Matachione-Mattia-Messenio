package it.polimi.ingsw.view;

import it.polimi.ingsw.model.ModelObserver;
import it.polimi.ingsw.network.dto.AvailableActionDTO;
import it.polimi.ingsw.network.dto.BoardDTO;
import it.polimi.ingsw.network.dto.GameEventDTO;
import it.polimi.ingsw.network.dto.PlayerDTO;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.visitor.InGameVisitor;
import it.polimi.ingsw.network.server.ClientConnection;
import it.polimi.ingsw.controller.GameController;

import java.util.List;

public class VirtualView implements ModelObserver, InGameVisitor {

    private final String nickname;
    private final ClientConnection connection;
    private final GameController controller;

    public VirtualView(String nickname, ClientConnection connection, GameController controller) {
        this.nickname = nickname;
        this.connection = connection;
        this.controller = controller;
    }
    //TODO: gestione timer se turno

    @Override
    public void onModelUpdate(GameEventDTO event, List<AvailableActionDTO> actions) {
        // TODO: trasformarlo in snapshot e mandarlo
        List<AvailableActionDTO> actions = game.getCurrentState().getAvailableActions(this.nickname);
        connection.send(new DeltaEventMessage(event, actions));
    }

     //Inviata solo all'inizio o riconnessione.

    @Override
    public void onFullSync(BoardDTO board, List<PlayerDTO> players, String activePlayer) {
        List<AvailableActionDTO> actions = game.getCurrentState().getAvailableActions(this.nickname);
        connection.send(new FullSyncMessage(board, players, activePlayer));
    }

    //Messaggi che arrivano dal giocatore verso il Server.

    public void onMessageReceived(InGameMessage message) {
        // Il pattern Visitor qui smista l'azione verso i metodi 'visit'
        // che chiameranno poi il GameController.
        message.accept(this);
    }



    //TODO: scrivere sta roba che chiami il controller e faccia zompare la partita
    public void handleDisconnection(String nickname){}
    //TODO: fare i vari visit che chiamano il controller

    @Override
    public void visit(TakeCardMessage msg) {
        try {
            controller.handleTakeCard(this.nickname, msg.row(), msg.col());
        } catch (IllegalStateException | IllegalArgumentException e) {
            connection.send(new ErrorMessage(e.getMessage()));
        }
    }

    @Override
    public void visit(PlaceTotemMessage msg) {
        try {
            controller.handlePlaceTotem(this.nickname, msg.positionIndex());
        } catch (IllegalStateException | IllegalArgumentException e) {
            connection.send(new ErrorMessage(e.getMessage()));
        }
    }

    @Override
    public void visit(SkipActionMessage msg) {
        try {
            controller.handleSkipBonus(this.nickname);
        } catch (IllegalStateException | IllegalArgumentException e) {
            connection.send(new ErrorMessage(e.getMessage()));
        }
    }
}