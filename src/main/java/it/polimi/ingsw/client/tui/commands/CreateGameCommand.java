package it.polimi.ingsw.client.tui.commands;

import it.polimi.ingsw.client.network.ServerController;
import it.polimi.ingsw.client.tui.OutputPort;

public class CreateGameCommand implements GameCommand {
    private final ServerController controller;
    private final OutputPort out;
    private final String nickname;
    private final int maxPlayers;

    public CreateGameCommand(ServerController controller, OutputPort out, String nickname, int maxPlayers) {
        this.controller = controller;
        this.out = out;
        this.nickname = nickname;
        this.maxPlayers = maxPlayers;
    }

    @Override
    public void execute() {
        controller.createGame(nickname, maxPlayers);
        out.print("Richiesta di creazione partita inviata. In attesa del server...");
    }
}