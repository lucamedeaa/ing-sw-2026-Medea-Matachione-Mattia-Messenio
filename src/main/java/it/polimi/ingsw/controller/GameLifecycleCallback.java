package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.CompletedGameResult;
import it.polimi.ingsw.network.dto.LeaderboardEntryDTO;

import java.util.List;

public interface GameLifecycleCallback {

    void closeCompletedRoom(CompletedGameResult completedGame, List<LeaderboardEntryDTO> personalBestEntries);

    void closeAbortedRoom(String reason, String excludedNickname);
}
