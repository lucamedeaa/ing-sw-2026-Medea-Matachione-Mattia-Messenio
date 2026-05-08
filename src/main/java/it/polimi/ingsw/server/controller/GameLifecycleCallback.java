package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.CompletedGameResult;
import it.polimi.ingsw.common.network.dto.LeaderboardEntryDto;

import java.util.List;

public interface GameLifecycleCallback {

    void closeCompletedRoom(CompletedGameResult completedGame, List<LeaderboardEntryDto> personalBestEntries);

    void closeAbortedRoom(String reason, String excludedNickname);
}
