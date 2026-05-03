package it.polimi.ingsw.client.tui.commands;

import it.polimi.ingsw.client.network.ServerController;
import it.polimi.ingsw.client.tui.TUI;

public class JoinGameCommand implements GameCommand {
    private final ServerController controller;
    private final TUI tui;
    private final String nickname;
    private final String gameId;

    public JoinGameCommand(ServerController controller, TUI tui, String nickname, String gameId) {
        this.controller = controller;
        this.tui = tui;
        this.nickname = nickname;
        this.gameId = gameId;
    }

    @Override
    public void execute() {

        controller.joinGame(nickname, gameId);
        tui.print("Richiesta di unione alla partita " + gameId + " inviata...");
    }
}
