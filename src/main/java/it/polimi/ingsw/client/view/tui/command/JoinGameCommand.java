package it.polimi.ingsw.client.view.tui.command;

import it.polimi.ingsw.client.view.tui.OutputPort;


/**
 * Command that asks the server to join an existing game room.
 */
public class JoinGameCommand implements GameCommand {
    private final ServerCommandPort controller;
    private final OutputPort out;
    private final String nickname;
    private final String gameId;

    /**
     * Creates a command for a join request.
     *
     * @param controller server command port
     * @param out output port used for user feedback
     * @param nickname nickname chosen by the local player
     * @param gameId identifier of the room to join
     */
    public JoinGameCommand(ServerCommandPort controller, OutputPort out, String nickname, String gameId) {
        this.controller = controller;
        this.out = out;
        this.nickname = nickname;
        this.gameId = gameId;
    }

    /** {@inheritDoc} */
    @Override
    public void execute() {

        controller.joinGame(nickname, gameId);
        out.print("Request to be added to the register " + gameId + " sent...");
    }
}
