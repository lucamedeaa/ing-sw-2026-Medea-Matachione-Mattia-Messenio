package it.polimi.ingsw.client.view.tui.command;

import it.polimi.ingsw.client.view.tui.OutputPort;

public class CreateGameCommand implements GameCommand {
    private final ServerCommandPort controller;
    private final OutputPort out;
    private final String nickname;
    private final int maxPlayers;

    public CreateGameCommand(ServerCommandPort controller, OutputPort out, String nickname, int maxPlayers) {
        this.controller = controller;
        this.out = out;
        this.nickname = nickname;
        this.maxPlayers = maxPlayers;
    }

    @Override
    public void execute() {
        controller.createGame(nickname, maxPlayers);
        out.print("Request to create a game has been sent. Waiting for the server...");
    }
}