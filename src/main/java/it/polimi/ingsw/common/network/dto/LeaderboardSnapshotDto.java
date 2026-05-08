package it.polimi.ingsw.common.network.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record LeaderboardSnapshotDto(int playerCount, List<LeaderboardEntryDto> entries) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public LeaderboardSnapshotDto {
        entries = List.copyOf(entries);
    }
}
