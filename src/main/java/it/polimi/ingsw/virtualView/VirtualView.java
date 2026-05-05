package it.polimi.ingsw.virtualView;

import it.polimi.ingsw.model.ModelObserver;
import it.polimi.ingsw.model.updates.AvailableAction;
import it.polimi.ingsw.model.updates.BoardUpdate;
import it.polimi.ingsw.model.updates.ModelUpdate;
import it.polimi.ingsw.model.updates.PlayerUpdate;
import it.polimi.ingsw.network.dto.*;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.visitor.InGameVisitor;
import it.polimi.ingsw.network.server.ClientProxy;
import it.polimi.ingsw.controller.GameController;

import java.util.List;
//import java.util.concurrent.TimeUnit;

public class VirtualView implements ModelObserver, InGameVisitor {

    private final String nickname;
    private final ClientProxy client;
    private final GameController controller;

    //TODO: check!
    // Gestione Timer
    // private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    // private ScheduledFuture<?> turnTimer;
    // private static final int TURN_TIMEOUT_SECONDS = 60;

    public VirtualView(String nickname, ClientProxy client, GameController controller) {
        this.nickname = nickname;
        this.client = client;
        this.controller = controller;
    }

    @Override
    public void onModelUpdate(ModelUpdate modelUpdate) {
        ModelUpdateDTO update =  MapperToDTO.modelToDTO(modelUpdate);
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
        BoardDTO board = MapperToDTO.boardToDTO(boardUpdate);
        List<PlayerDTO> players = playersUpdates.stream().map(MapperToDTO::playerToDTO).toList();
        List<AvailableActionDTO> actions = actionsUpdates.stream().map(MapperToDTO::availableActionToDTO).toList();

        // Invia le azioni solo se il nickname della vista corrisponde al giocatore attivo
        List<AvailableActionDTO> myActions = this.nickname.equals(activePlayer) ? actions : List.of();

        client.fullSync(board, players, activePlayer, myActions);
    }

    //Messaggi che arrivano dal giocatore verso il Server.

    public void onMessageReceived(InGameMessage message) {
        // Il pattern Visitor qui smista l'azione verso i metodi 'visit'
        // che chiameranno poi il GameController.
        message.accept(this);
    }


    //TODO: scrivere sta roba che chiami il controller e faccia zompare la partita
    //attento se due fanno insieme
    public void handleDisconnection() {
        //cancelTurnTimer();
        // Propago la disconnessione al controller per salto turno/ fine partita
        controller.handlePlayerDisconnection(this.nickname);
    }
    //TODO: fare i vari visit che chiamano il controller

    @Override
    public void visit(TakeCardMessage msg) {
        takeCard(msg.row(), msg.col());
    }

    @Override
    public void visit(PlaceTotemMessage msg) {
        placeTotem(msg.positionIndex());
    }

    @Override
    public void visit(SkipActionMessage msg) {
        skipAction();
    }

    public void takeCard(int row, int col) {
        controller.handleTakeCard(this.nickname, row, col, errorMessage -> {
            client.error("Errore mossa: " + errorMessage);
        });
    }

    public void placeTotem(int positionIndex) {
        controller.handlePlaceTotem(this.nickname, positionIndex, errorMessage -> {
            client.error("Errore mossa: " + errorMessage);
        });
    }

    public void skipAction() {
        controller.handleSkipBonus(this.nickname, errorMessage -> {
            client.error("Errore mossa: " + errorMessage);
        });
    }
}


    // metodi timer   PROBABILMENTE NON SERVONO
    /*
    private void startTurnTimer() {
        cancelTurnTimer();
        // Allo scadere del tempo, il giocatore viene disconnesso per sbloccare la partita
        turnTimer = scheduler.schedule(() -> {
            System.err.println("[TIMEOUT] Player " + nickname + " AFK.");
            handleDisconnection(nickname);
        }, TURN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    private void cancelTurnTimer(){
        if(turnTimer != null && !turnTimer.isDone()){
            turnTimer.cancel(false);
        }
    }
}

     */
