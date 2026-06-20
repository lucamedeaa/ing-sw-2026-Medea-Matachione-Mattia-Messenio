package it.polimi.ingsw.client.view.gui.viewstate;

import it.polimi.ingsw.server.model.enums.TotemColor;

/**
 * Compact immutable player summary for GUI panels.
 *
 * @param nickname player nickname
 * @param totemColor player totem color
 * @param food current food amount
 * @param prestige current prestige amount
 */
public record PlayerInfo(String nickname, TotemColor totemColor, int food, int prestige) {}
