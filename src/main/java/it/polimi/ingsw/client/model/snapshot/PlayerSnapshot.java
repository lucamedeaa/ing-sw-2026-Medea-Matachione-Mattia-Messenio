package it.polimi.ingsw.client.model.snapshot;

import it.polimi.ingsw.server.model.enums.TotemColor;
import it.polimi.ingsw.common.network.dto.PlayerDto;

//invece di usare PlayerDTO che è immutabile, diventa una rottura aggiornare il food e prestige ogni volta
/**
 * Mutable client-side copy of a player's public state.
 */
public class PlayerSnapshot {
    private final String nickname;
    private int food;
    private int prestige;
    private final TotemColor totemColor;
    private int foodDiscount;
    private int sustenanceDiscount;


    /**
     * Creates a snapshot from an immutable player DTO.
     *
     * @param dto source player data
     */

    public PlayerSnapshot(PlayerDto dto) {
        this.nickname = dto.nickname();
        this.food = dto.food();
        this.prestige = dto.prestige();
        this.totemColor = dto.totemColor();
        this.foodDiscount = dto.foodDiscount();
        this.sustenanceDiscount = dto.sustenanceDiscount();
    }
    /**
     * Creates a defensive copy of another player snapshot.
     *
     * @param other source snapshot
     */
    public PlayerSnapshot(PlayerSnapshot other) {
        this.nickname = other.nickname;
        this.food = other.food;
        this.prestige = other.prestige;
        this.totemColor = other.totemColor;
        this.foodDiscount = other.foodDiscount;
        this.sustenanceDiscount = other.sustenanceDiscount;
    }

    /**
     * Returns the nickname.
     *
     * @return the current nickname
     */
    public String getNickname() { return nickname; }
    /**
     * Returns the food.
     *
     * @return the food
     */
    public int getFood() { return food; }
    /**
     * Returns the prestige.
     *
     * @return the prestige
     */
    public int getPrestige() { return prestige; }
    /**
     * Returns the totem color.
     *
     * @return the totem color
     */
    public TotemColor getTotemColor() { return totemColor; }
    /**
     * Returns the food discount.
     *
     * @return the food discount
     */
    public int getFoodDiscount() { return foodDiscount; }
    /**
     * Returns the sustenance discount.
     *
     * @return the sustenance discount
     */
    public int getSustenanceDiscount() { return sustenanceDiscount; }

    /**
     * Sets the food.
     *
     * @param food food value
     */
    public void setFood(int food) { this.food = food; }
    /**
     * Sets the prestige.
     *
     * @param prestige prestige value
     */
    public void setPrestige(int prestige) { this.prestige = prestige; }
    /**
     * Sets the food discount.
     *
     * @param foodDiscount food discount value
     */
    public void setFoodDiscount(int foodDiscount) { this.foodDiscount = foodDiscount; }
    /**
     * Sets the sustenance discount.
     *
     * @param sustenanceDiscount sustenance discount value
     */
    public void setSustenanceDiscount(int sustenanceDiscount) { this.sustenanceDiscount = sustenanceDiscount; }

}
