package it.polimi.ingsw.client.model.snapshot;

import it.polimi.ingsw.common.network.dto.PlayerDto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Client-side snapshot of players, totem positions, return order, and tribes.
 */
public class RosterSnapshot {
    private final Map<String, PlayerSnapshot> players = new ConcurrentHashMap<>();
    private final Map<String, Integer> playerTotemPositions = new ConcurrentHashMap<>();
    private final Map<String, Integer> playerReturnPositions = new ConcurrentHashMap<>();
    private final Map<String, List<Integer>> playerTribes = new ConcurrentHashMap<>();

    /**
     * Rebuilds the roster from the latest full-sync player list.
     *
     * @param playersList players received from the server
     */
    public void initPlayers(List<PlayerDto> playersList) {
        players.clear();
        playerReturnPositions.clear();
        playerTotemPositions.clear();
        for (PlayerDto p : playersList) {
            players.put(p.nickname(), new PlayerSnapshot(p));
            playerTribes.putIfAbsent(p.nickname(), new ArrayList<>());
        }
    }

    /**
     * Stores the board position occupied by a player's totem.
     *
     * @param nickname player nickname
     * @param positionIndex offer tile position index
     */
    public void updateTotemPosition(String nickname, int positionIndex) {
        playerTotemPositions.put(nickname, positionIndex);
        playerReturnPositions.remove(nickname);
    }

    /**
     * Moves a player's totem back to the return track.
     *
     * @param nickname player nickname
     * @param returnIndex totem return-track index
     */
    public void returnTotemToTrack(String nickname, int returnIndex) {
        playerTotemPositions.remove(nickname);
        playerReturnPositions.put(nickname, returnIndex);
    }

    /**
     * Adds a card to a player's tribe.
     *
     * @param nickname player nickname
     * @param cardId card identifier
     */
    public void addCardToTribe(String nickname, Integer cardId) {
        playerTribes.computeIfAbsent(nickname, k -> new CopyOnWriteArrayList<>()).add(cardId);

    }

    /**
     * Replaces the cards in a player's tribe.
     *
     * @param nickname player nickname
     * @param newTribeCards replacement tribe card identifiers
     */
    public void updateTribe(String nickname, List<Integer> newTribeCards) {
        playerTribes.put(nickname, new ArrayList<>(newTribeCards));
    }


    /** Clears the return positions. */
    public void clearReturnPositions() {
        playerReturnPositions.clear();
    }

    //public Map<String, PlayerSnapshot> getPlayers() { return new HashMap<>(players); }
    /**
     * Returns the totem positions.
     *
     * @return the totem positions
     */
    public Map<String, Integer> getTotemPositions() { return new HashMap<>(playerTotemPositions); }
    /**
     * Returns the return positions.
     *
     * @return the return positions
     */
    public Map<String, Integer> getReturnPositions() { return new HashMap<>(playerReturnPositions); }
    //public Map<String, List<Integer>> getTribes() { return new HashMap<>(playerTribes); }

    /**
     * Returns the players.
     *
     * @return defensive copy of player snapshots by nickname
     */
    public Map<String, PlayerSnapshot> getPlayers() {
        Map<String, PlayerSnapshot> copy = new HashMap<>();
        for (Map.Entry<String, PlayerSnapshot> entry : players.entrySet()) {
            copy.put(entry.getKey(), new PlayerSnapshot(entry.getValue()));
        }
        return copy;
    }

    /**
     * Returns the tribes.
     *
     * @return defensive copy of tribe card identifiers by nickname
     */
    public Map<String, List<Integer>> getTribes() {
        Map<String, List<Integer>> copy = new HashMap<>();
        for (Map.Entry<String, List<Integer>> entry : playerTribes.entrySet()) {
            copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copy;
    }

    /**
     * Updates the player resources.
     *
     * @param nickname player nickname
     * @param food food value
     * @param prestige prestige value
     * @param foodDiscount food discount value
     * @param sustenanceDiscount sustenance discount value
     */
    public void updatePlayerResources(String nickname, int food, int prestige, int foodDiscount, int sustenanceDiscount) {
    PlayerSnapshot player = players.get(nickname);
    if (player != null) {
        player.setFood(food);
        player.setPrestige(prestige);
        player.setFoodDiscount(foodDiscount);
        player.setSustenanceDiscount(sustenanceDiscount);
    }
}
}
