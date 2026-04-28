package it.polimi.ingsw.client.tui;
import it.polimi.ingsw.network.messages.GameInfoDTO;
import java.util.ArrayList;
import java.util.List;

public class MatchmakingState implements UIState {

    private final TUI tui;
    private List<GameInfoDTO> availableGames = new ArrayList<>();
    private int step = 0; // step: 0=menu, 1=create nickname, 2=create maxPlayers, 3=join gameId, 4=join nickname
    private String pendingNickname;
    private String pendingGameId;

    public MatchmakingState(TUI tui) {
        this.tui = tui;
    }

    @Override
    public void render() {
        System.out.print("\033[H\033[2J");
        System.out.println("╔══════════════════════════╗");
        System.out.println("║     MESOS — MENU         ║");
        System.out.println("╚══════════════════════════╝");
        System.out.println("  1. Create new game");
        System.out.println("  2. Join a game");
        System.out.println("  3. Available games");
        System.out.println("  0. Disconnect");
        if (!availableGames.isEmpty()) {
            System.out.println("Available games:");
            for (GameInfoDTO g : availableGames)
                System.out.println("  - " + g.getGameId() + " (" + g.getCurrentPlayers() + "/" + g.getMaxPlayers() + ")");
        }
        System.out.print("> ");
    }

    @Override
    public void handleInput(String input) {
        switch (step) {
            case 0 -> {
                switch (input.trim()) {
                    case "1" -> { step = 1; System.out.print("Nickname: "); }
                    case "2" -> { step = 3; System.out.print("Game ID: "); }
                    case "3" -> tui.getController().getAvailableGames();
                    case "0" -> tui.getController().disconnect();
                    default  -> System.out.println("Invalid choice.");
                }
            }
            case 1 -> { pendingNickname = input.trim(); step = 2; System.out.print("Number of players: "); }
            case 2 -> {
                try {
                    tui.getController().createGame(pendingNickname, Integer.parseInt(input.trim()));
                    step = 0;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number.");
                    System.out.print("Number of players: ");
                }
            }
            case 3 -> { pendingGameId = input.trim(); step = 4; System.out.print("Nickname: "); }
            case 4 -> { tui.getController().joinGame(pendingGameId, input.trim()); step = 0; }
        }
    }

    @Override
    public void onMatchmakingSuccess(String text) {
        tui.changeState(new LobbyState(tui));
    }

    @Override
    public void onAvailableGames(List<GameInfoDTO> games) {
        availableGames = games;
        this.render();
    }

    @Override
    public void onError(String errorText) {
        step = 0;
        System.out.println("[ERROR] " + errorText);
        this.render();
    }
}