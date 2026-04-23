package it.polimi.ingsw.client.model;

import it.polimi.ingsw.network.dto.AvailableActionDTO;
import it.polimi.ingsw.network.dto.BoardDTO;
import it.polimi.ingsw.network.dto.PlayerDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Lightweight client-side model that stores and updates the visible game state. */
public class LightGameModel {

    private BoardDTO board;
    private final Map<String, PlayerDTO> players = new HashMap<>();
    private List<AvailableActionDTO> actions;

    /** Sets the full game state. @param board @param playersList */
    public void setFullState(BoardDTO board, List<PlayerDTO> playersList) {
        this.board = board;
        this.players.clear();
        for (PlayerDTO p : playersList) {
            this.players.put(p.nickname(), p);
        }
        notifyUI();
    }

    /** Updates available actions. @param actions */
    public void setAvailableActions(List<AvailableActionDTO> actions) {
        this.actions = actions;
        notifyUI();
    }

    /** Removes a card from the board. @param row @param col */
    public void removeCard(int row, int col) {
        if (row == 0) board.UpperRowCards().set(col, null);
        else board.LowerRowCards().set(col, null);
        notifyUI();
    }

    /** Refills a board row. @param row @param newCardIds */
    public void refillBoardRow(int row, List<String> newCardIds) {
        // TODO: Sovrascrivere UpperRowCards o LowerRowCards
        notifyUI();
    }

    /** Updates a player's totem position. @param nickname @param positionIndex */
    public void updateTotemPosition(String nickname, int positionIndex) {
        // TODO: Aggiornare la struttura dati locale del tracciato offerte
        notifyUI();
    }

    /** Updates player resources. @param nickname @param newFood @param newPrestige */
    public void updatePlayerResources(String nickname, int newFood, int newPrestige) {
        PlayerDTO player = players.get(nickname);
        if (player != null) {
            players.put(nickname, new PlayerDTO(nickname, newFood, newPrestige));
            notifyUI();
        }
    }

    /** Adds a card to a player's tribe. @param nickname @param cardId */
    public void addCardToPlayerTribe(String nickname, String cardId) {
        // TODO: Aggiungere l'ID alla lista di carte del giocatore
        notifyUI();
    }

    /** Updates current era. @param newEra */
    public void updateEra(int newEra) {
        // TODO: Aggiornare contatore Era
        notifyUI();
    }

    /** Updates current round. @param newRound */
    public void updateRound(int newRound) {
        // TODO: Aggiornare contatore Round
        notifyUI();
    }

    /** Notifies the UI about state changes. */
    private void notifyUI() {
        // Implementazione dell'Observer per notificare la CLI/GUI
    }
}