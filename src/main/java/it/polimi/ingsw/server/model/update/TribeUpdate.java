package it.polimi.ingsw.server.model.update;

import it.polimi.ingsw.common.network.dto.TribeDto;
import java.util.List;

public record TribeUpdate(String nickname, List<Integer> tribe) {
    public TribeDto toDTO() {
        return new TribeDto(nickname, tribe);
    }
}