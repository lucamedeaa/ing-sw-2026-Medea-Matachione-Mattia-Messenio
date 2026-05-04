package it.polimi.ingsw.network.dto.events;

import it.polimi.ingsw.network.dto.GameEventDTO;
import it.polimi.ingsw.network.visitor.EventVisitor;

/** Event DTO representing a change in a player's resources. */
public record PlayerResourcesChangedEventDTO(String nickname, int newFood, int newPrestige, int foodDiscount, String reason) implements GameEventDTO {

    /** Accepts a visitor. @param visitor */
    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }
}