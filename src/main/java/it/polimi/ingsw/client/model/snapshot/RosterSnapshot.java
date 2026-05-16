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

    public void updateTotemPosition(String nickname, int positionIndex) {
        playerTotemPositions.put(nickname, positionIndex);
        playerReturnPositions.remove(nickname);
    }

    public void returnTotemToTrack(String nickname, int returnIndex) {
        playerTotemPositions.remove(nickname);
        playerReturnPositions.put(nickname, returnIndex);
    }

    public void addCardToTribe(String nickname, Integer cardId) {
        playerTribes.computeIfAbsent(nickname, k -> new CopyOnWriteArrayList<>()).add(cardId);

    }

    public void updateTribe(String nickname, List<Integer> newTribeCards) {
        playerTribes.put(nickname, new ArrayList<>(newTribeCards));
    }


    public void clearReturnPositions() {
        playerReturnPositions.clear();
    }

    //public Map<String, PlayerSnapshot> getPlayers() { return new HashMap<>(players); }
    public Map<String, Integer> getTotemPositions() { return new HashMap<>(playerTotemPositions); }
    public Map<String, Integer> getReturnPositions() { return new HashMap<>(playerReturnPositions); }
    //public Map<String, List<Integer>> getTribes() { return new HashMap<>(playerTribes); }

    public Map<String, PlayerSnapshot> getPlayers() {
        Map<String, PlayerSnapshot> copy = new HashMap<>();
        for (Map.Entry<String, PlayerSnapshot> entry : players.entrySet()) {
            copy.put(entry.getKey(), new PlayerSnapshot(entry.getValue()));
        }
        return copy;
    }

    public Map<String, List<Integer>> getTribes() {
        Map<String, List<Integer>> copy = new HashMap<>();
        for (Map.Entry<String, List<Integer>> entry : playerTribes.entrySet()) {
            copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copy;
    }

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