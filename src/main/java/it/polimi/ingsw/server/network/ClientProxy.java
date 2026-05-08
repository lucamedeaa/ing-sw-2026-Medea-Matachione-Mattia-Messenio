package it.polimi.ingsw.server.network;

import it.polimi.ingsw.common.network.dto.action.ActionDto;
import it.polimi.ingsw.common.network.dto.BoardDto;
import it.polimi.ingsw.common.network.dto.event.GameEventDto;
import it.polimi.ingsw.common.network.dto.LeaderboardSnapshotDto;
import it.polimi.ingsw.common.network.dto.PlayerDto;
import it.polimi.ingsw.common.network.dto.PlayerGameCompletedDto;
import it.polimi.ingsw.common.network.dto.GameInfoDto;

import java.util.List;

public interface ClientProxy {

    void fullSync(BoardDto board, List<PlayerDto> players, String activePlayer, List<ActionDto> actions);

    void deltaEvent(List<GameEventDto> events, List<ActionDto> nextActions, String activePlayer);

    void error(String error);

    void matchmakingSuccess(String text);

    void availableGames(List<GameInfoDto> games);

    void gameAborted(String reason);

    void roomUpdate(String notification, List<String> currentPlayers);

    void gameLeftSuccess(String text);

    void gameCompleted(PlayerGameCompletedDto completedGame);

    void leaderboard(LeaderboardSnapshotDto leaderboard);
}
