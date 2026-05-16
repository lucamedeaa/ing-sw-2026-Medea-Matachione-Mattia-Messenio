package it.polimi.ingsw.client.view.gui.viewstate;

import it.polimi.ingsw.server.model.enums.TotemColor;

public record PlayerInfo(String nickname, TotemColor totemColor, int food, int prestige) {}