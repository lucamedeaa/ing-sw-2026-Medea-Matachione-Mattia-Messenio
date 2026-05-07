package it.polimi.ingsw.network.dto;

import java.io.Serializable;
import java.util.List;

public record LeaderboardSnapshot(int playerCount, List<LeaderboardEntryDTO> entries) implements Serializable {
    public LeaderboardSnapshot {
        entries = List.copyOf(entries);
    }
}
