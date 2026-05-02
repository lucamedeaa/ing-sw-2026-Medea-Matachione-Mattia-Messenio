package it.polimi.ingsw.client.lightGameModel;

import it.polimi.ingsw.model.enums.TotemColor;
import it.polimi.ingsw.network.dto.PlayerDTO;

//invece di usare PlayerDTO che è immutabile, diventa una rottura aggiornare il food e prestige ogni volta
public class LightPlayer {
    private final String nickname;
    private int food;
    private int prestige;
    private TotemColor totemColor;


    public LightPlayer(PlayerDTO dto) {
        this.nickname = dto.nickname();
        this.food = dto.food();
        this.prestige = dto.prestige();
        this.totemColor = dto.totemColor();
        // magari aggiungere fooddiscount se vogliamo renderlo un parametro visualizzabile
    }

    public String getNickname() { return nickname; }
    public int getFood() { return food; }
    public int getPrestige() { return prestige; }
    public TotemColor getTotemColor() { return totemColor; }

    public void setFood(int food) { this.food = food; }
    public void setPrestige(int prestige) { this.prestige = prestige; }
}