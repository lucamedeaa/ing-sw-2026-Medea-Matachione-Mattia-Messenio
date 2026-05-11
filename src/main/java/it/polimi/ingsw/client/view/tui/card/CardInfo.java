package it.polimi.ingsw.client.view.tui.card;

public record CardInfo(
        String name,
        String type,
        int era,
        String detail,
        String cost,
        String extraPP
) {}