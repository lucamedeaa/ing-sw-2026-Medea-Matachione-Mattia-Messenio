package it.polimi.ingsw.client.model.snapshot;

/**
 * Resource delta or absolute resource values for a player.
 *
 * @param food food amount
 * @param prestige prestige amount
 * @param discount permanent food discount
 * @param sustenanceDiscount Sustenance-specific discount
 */
public record PlayerResources(int food, int prestige, int discount, int sustenanceDiscount) {}