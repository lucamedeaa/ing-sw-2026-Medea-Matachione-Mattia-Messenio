package it.polimi.ingsw.network.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record LeaderboardSnapshot(int playerCount, List<LeaderboardEntryDTO> entries) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public LeaderboardSnapshot {
        entries = List.copyOf(entries);
    }
}
