package it.polimi.ingsw.client.view.tui.command;

import it.polimi.ingsw.client.view.tui.OutputPort;


public class JoinGameCommand implements GameCommand {
    private final ServerCommandPort controller;
    private final OutputPort out;
    private final String nickname;
    private final String gameId;

    public JoinGameCommand(ServerCommandPort controller, OutputPort out, String nickname, String gameId) {
        this.controller = controller;
        this.out = out;
        this.nickname = nickname;
        this.gameId = gameId;
    }

    @Override
    public void execute() {

        controller.joinGame(nickname, gameId);
        out.print("Richiesta di unione alla partita " + gameId + " inviata...");
    }
}
