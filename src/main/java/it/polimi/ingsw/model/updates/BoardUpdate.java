package it.polimi.ingsw.model.updates;

import java.util.List;

public record BoardUpdate(List<Integer> UpperRowCards,
                          List<Integer> LowerRowCards,
                          int currentEra,
                          int currentRound) {
}
