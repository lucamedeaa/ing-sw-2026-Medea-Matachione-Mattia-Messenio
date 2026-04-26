package it.polimi.ingsw.view;

import it.polimi.ingsw.model.ModelObserver;
import it.polimi.ingsw.network.dto.*;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.visitor.InGameVisitor;
import it.polimi.ingsw.network.server.ClientConnection;
import it.polimi.ingsw.controller.GameController;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
//import java.util.concurrent.TimeUnit;

public class VirtualView implements ModelObserver, InGameVisitor {

    private final String nickname;
    private final ClientConnection connection;
    private final GameController controller;

    //TODO: check!
    // Gestione Timer
    // private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    // private ScheduledFuture<?> turnTimer;
    // private static final int TURN_TIMEOUT_SECONDS = 60;

    public VirtualView(String nickname, ClientConnection connection, GameController controller) {
        this.nickname = nickname;
        this.connection = connection;
        this.controller = controller;
    }

    @Override
    public void onModelUpdate(ModelUpdateDTO update) {
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
        connection.send(new DeltaEventMessage(update.event(), myActions));
    }

    //Inviata solo all'inizio o riconnessione.

    @Override
    public void onFullSync(BoardDTO board, List<PlayerDTO> players, String activePlayer) {
        connection.send(new FullSyncMessage(board, players, activePlayer));
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
        //ricevo l'azione quindi blocco il timer
        //cancelTurnTimer();
        try {
            controller.handleTakeCard(this.nickname, msg.row(), msg.col());
        } catch (IllegalStateException | IllegalArgumentException e) {
            connection.send(new ErrorMessage(e.getMessage()));
        }
    }

    @Override
    public void visit(PlaceTotemMessage msg) {
        //cancelTurnTimer();
        try {
            controller.handlePlaceTotem(this.nickname, msg.positionIndex());
        } catch (IllegalStateException | IllegalArgumentException e) {
            connection.send(new ErrorMessage(e.getMessage()));
        }
    }

    @Override
    public void visit(SkipActionMessage msg) {
        //cancelTurnTimer();
        try {
            controller.handleSkipBonus(this.nickname);
        } catch (IllegalStateException | IllegalArgumentException e) {
            connection.send(new ErrorMessage(e.getMessage()));
        }
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