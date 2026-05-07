package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.dto.AvailableActionDTO;
import it.polimi.ingsw.network.dto.BoardDTO;
import it.polimi.ingsw.network.dto.GameEventDTO;
import it.polimi.ingsw.network.dto.LeaderboardSnapshot;
import it.polimi.ingsw.network.dto.PlayerDTO;
import it.polimi.ingsw.network.dto.PlayerGameCompletedDTO;
import it.polimi.ingsw.network.messages.GameInfoDTO;

import java.util.List;

public interface ServerNotificationReceiver {

    void fullSync(BoardDTO board, List<PlayerDTO> players, String activePlayer, List<AvailableActionDTO> actions);

    void deltaEvent(List<GameEventDTO> events, List<AvailableActionDTO> nextActions, String activePlayer);

    void error(String error);

    void matchmakingSuccess(String text);

    void availableGames(List<GameInfoDTO> games);

    void gameAborted(String reason);

    void roomUpdate(String notification, List<String> currentPlayers);

    void gameLeftSuccess(String text);

    void gameCompleted(PlayerGameCompletedDTO completedGame);

    void leaderboard(LeaderboardSnapshot leaderboard);
}
