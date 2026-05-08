package it.polimi.ingsw.client.view.tui.command;

public class GetLeaderboardCommand implements GameCommand {
    private final ServerCommandPort server;

    public GetLeaderboardCommand(ServerCommandPort server) {
        this.server = server;
    }

    @Override
    public void execute() {
        // Invia la richiesta di rete al server
        server.getLeaderboard();
    }
}