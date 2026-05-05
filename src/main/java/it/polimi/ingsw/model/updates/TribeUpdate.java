package it.polimi.ingsw.model.updates;

import it.polimi.ingsw.network.dto.TribeDTO;
import java.util.List;

public record TribeUpdate(String nickname, List<Integer> tribe) {
    public TribeDTO toDTO() {
        return new TribeDTO(nickname, tribe);
    }
}