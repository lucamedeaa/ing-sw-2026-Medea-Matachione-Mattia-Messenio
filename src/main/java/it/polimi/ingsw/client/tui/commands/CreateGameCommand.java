package it.polimi.ingsw.client.tui.commands;

import it.polimi.ingsw.client.network.ServerController;
import it.polimi.ingsw.client.tui.TUI;

public class CreateGameCommand implements GameCommand {
    private final ServerController controller;
    private final TUI tui;
    private final String nickname;
    private final int maxPlayers;

    public CreateGameCommand(ServerController controller, TUI tui, String nickname, int maxPlayers) {
        this.controller = controller;
        this.tui = tui;
        this.nickname = nickname;
        this.maxPlayers = maxPlayers;
    }

    @Override
    public void execute() {
        if (maxPlayers < 2 || maxPlayers > 5) {
            tui.print("Errore: Il numero di giocatori deve essere compreso tra 2 e 5.");
            return;
        }
        // RIMOSSO: tui.setMyNickname(nickname);
        controller.createGame(nickname, maxPlayers);
        tui.print("Richiesta di creazione partita inviata. In attesa del server...");
    }
}