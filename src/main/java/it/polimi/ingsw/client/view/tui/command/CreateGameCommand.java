package it.polimi.ingsw.client.view.tui.command;

import it.polimi.ingsw.client.view.tui.OutputPort;

/**
 * Command that asks the server to create a new game room.
 */
public class CreateGameCommand implements GameCommand {
    private final ServerCommandPort controller;
    private final OutputPort out;
    private final String nickname;
    private final int maxPlayers;

    /**
     * Creates a command for a game creation request.
     *
     * @param controller server command port
     * @param out output port used for user feedback
     * @param nickname nickname chosen by the local player
     * @param maxPlayers maximum number of players for the new room
     */
    public CreateGameCommand(ServerCommandPort controller, OutputPort out, String nickname, int maxPlayers) {
        this.controller = controller;
        this.out = out;
        this.nickname = nickname;
        this.maxPlayers = maxPlayers;
    }

    /** {@inheritDoc} */
    @Override
    public void execute() {
        controller.createGame(nickname, maxPlayers);
        out.print("Request to create a game has been sent. Waiting for the server...");
    }
}
