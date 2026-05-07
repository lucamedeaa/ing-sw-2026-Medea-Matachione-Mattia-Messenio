package it.polimi.ingsw.client.tui.render;

import it.polimi.ingsw.client.tui.OutputPort;
import it.polimi.ingsw.network.messages.GameInfoDTO;

import java.util.List;

public class MatchmakingRenderer {
    private final OutputPort out;

    public MatchmakingRenderer(OutputPort out) {
        this.out = out;
    }

    public void render(List<GameInfoDTO> availableGames, String lastError) {
        out.clearScreen();
        printLogo();
        printHeader();
        printCommands();
        printGames(availableGames);

        if (lastError != null && !lastError.isEmpty()) {
            out.print("\n\033[33m[INFO]\033[0m \033[3m" + lastError + "\033[0m");
        }
        out.prompt("\n\033[1;33mDigita il tuo destino > \033[0m");
    }

    private void printLogo() {
        String[] logoLines = {

                "███╗   ███╗███████╗███████╗ ██████╗ ███████╗",
                "████╗ ████║██╔════╝██╔════╝██╔═══██╗██╔════╝",
                "██╔████╔██║█████╗  ███████╗██║   ██║███████╗",
                "██║╚██╔╝██║██╔══╝  ╚════██║██║   ██║╚════██║",
                "██║ ╚═╝ ██║███████╗███████║╚██████╔╝███████║",
                "╚═╝     ╚═╝╚══════╝╚══════╝ ╚═════╝ ╚══════╝"
        };
        String[] colors = {"\033[38;5;226m", "\033[38;5;220m", "\033[38;5;214m", "\033[38;5;208m", "\033[38;5;202m", "\033[38;5;166m"};
        for (int i = 0; i < logoLines.length; i++) {
            out.print(colors[i] + logoLines[i] + "\033[0m");
        }
    }

    private void printHeader() {
        out.print("\033[1;30m" + "━".repeat(52) + "\033[0m");
        out.print("\033[1;37mBenvenuto Capotribù. Incidi la tua storia nel tempo.\033[0m");
        out.print("\033[1;30m" + "━".repeat(52) + "\033[0m\n");
    }

    private void printCommands() {
        out.print("   \033[1;33m•\033[0m \033[1mlist\033[0m       \033[90m| Osserva le Cronache (Partite disponibili)\033[0m");
        out.print("   \033[1;33m•\033[0m \033[1mcreate <nickname> <players>\033[0m     \033[90m| Fonda un nuovo Insediamento\033[0m");
        out.print("   \033[1;33m•\033[0m \033[1mjoin <nickname> <gameID>\033[0m       \033[90m| Unisciti a una Tribù esistente\033[0m");
        out.print("   \033[1;33m•\033[0m \033[1m0\033[0m          \033[90m| Abbandona la Storia ed esci\033[0m\n");
    }

    private void printGames(List<GameInfoDTO> availableGames) {
        if (!availableGames.isEmpty()) {
            out.print("   \033[1;32mCRONACHE ATTIVE:\033[0m");
            for (GameInfoDTO g : availableGames) {
                out.print(String.format("   \033[33m▶\033[0m \033[1mID: %-8s\033[0m \033[90m| Fondatore:\033[0m %-12s \033[90m| Popolazione:\033[0m [%d/%d]",
                        g.getGameId(), g.getCreatorNickname(), g.getCurrentPlayers(), g.getMaxPlayers()));
            }
        } else {
            out.print("   \033[3mNessuna storia iniziata. Sii il primo a incidere la pietra.\033[0m");
        }
    }
}