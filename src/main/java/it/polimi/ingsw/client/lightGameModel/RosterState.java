package it.polimi.ingsw.client.lightGameModel;

import it.polimi.ingsw.network.dto.PlayerDTO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class RosterState {
    private final Map<String, LightPlayer> players = new ConcurrentHashMap<>();
    private final Map<String, Integer> playerTotemPositions = new ConcurrentHashMap<>();
    private final Map<String, Integer> playerReturnPositions = new ConcurrentHashMap<>();
    private final Map<String, List<Integer>> playerTribes = new ConcurrentHashMap<>();

    public void initPlayers(List<PlayerDTO> playersList) {
        players.clear();
        playerReturnPositions.clear();
        playerTotemPositions.clear();
        for (PlayerDTO p : playersList) {
            players.put(p.nickname(), new LightPlayer(p));
            playerTribes.putIfAbsent(p.nickname(), new ArrayList<>());
        }
    }

    public void updateTotemPosition(String nickname, int positionIndex) {
        playerTotemPositions.put(nickname, positionIndex);
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

    public void updatePlayerResources(String nickname, int newFood, int newPrestige, int newFoodDiscount) {
        LightPlayer player = players.get(nickname);
        if (player != null) {
            player.setFood(newFood);
            player.setPrestige(newPrestige);
            player.setFoodDiscount(newFoodDiscount);
        }
    }

    public void clearReturnPositions() {
        playerReturnPositions.clear();
    }

    public Map<String, LightPlayer> getPlayers() { return new HashMap<>(players); }
    public Map<String, Integer> getTotemPositions() { return new HashMap<>(playerTotemPositions); }
    public Map<String, Integer> getReturnPositions() { return new HashMap<>(playerReturnPositions); }
    public Map<String, List<Integer>> getTribes() { return new HashMap<>(playerTribes); }
}