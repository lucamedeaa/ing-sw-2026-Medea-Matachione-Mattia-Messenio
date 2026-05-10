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

    public String getNickname() { return nickname; }
    public int getFood() { return food; }
    public int getPrestige() { return prestige; }
    public TotemColor getTotemColor() { return totemColor; }
    public int getFoodDiscount() { return foodDiscount; }
    public int getSustenanceDiscount() { return sustenanceDiscount; }

    public void setFood(int food) { this.food = food; }
    public void setPrestige(int prestige) { this.prestige = prestige; }
    public void setFoodDiscount(int foodDiscount) { this.foodDiscount = foodDiscount; }
    public void setSustenanceDiscount(int sustenanceDiscount) { this.sustenanceDiscount = sustenanceDiscount; }

}
