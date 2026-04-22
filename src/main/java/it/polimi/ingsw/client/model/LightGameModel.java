package it.polimi.ingsw.client.model;

import it.polimi.ingsw.network.dto.AvailableActionDTO;
import it.polimi.ingsw.network.dto.BoardDTO;
import it.polimi.ingsw.network.dto.PlayerDTO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LightGameModel {
    private BoardDTO board;
    private final Map<String, PlayerDTO> players = new HashMap<>();
    private List<AvailableActionDTO> actions;

    public void setFullState(BoardDTO board, List<PlayerDTO> playersList) {
        this.board = board;
        this.players.clear();
        for (PlayerDTO p : playersList) {
            this.players.put(p.nickname(), p);
        }
        notifyUI();
    }

    public void setAvailableActions(List<AvailableActionDTO> actions) {
        this.actions = actions;
        notifyUI();
    }

    public void removeCard(int row, int col) {
        if(row == 0) board.UpperRowCards().set(col, null);
        else board.LowerRowCards().set(col, null);
        notifyUI();
    }

    public void refillBoardRow(int row, List<String> newCardIds) {
        // TODO: Sovrascrivere UpperRowCards o LowerRowCards
        notifyUI();
    }

    public void updateTotemPosition(String nickname, int positionIndex) {
        // TODO: Aggiornare la struttura dati locale del tracciato offerte
        notifyUI();
    }

    public void updatePlayerResources(String nickname, int newFood, int newPrestige) {
        PlayerDTO player = players.get(nickname);
        if (player != null) {
            players.put(nickname, new PlayerDTO(nickname, newFood, newPrestige));
            notifyUI();
        }
    }

    public void addCardToPlayerTribe(String nickname, String cardId) {
        // TODO: Aggiungere l'ID alla lista di carte del giocatore
        notifyUI();
    }

    public void updateEra(int newEra) {
        // TODO: Aggiornare contatore Era
        notifyUI();
    }

    public void updateRound(int newRound) {
        // TODO: Aggiornare contatore Round
        notifyUI();
    }

    private void notifyUI() {
        // Implementazione dell'Observer per notificare la CLI/GUI
    }
}