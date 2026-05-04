package it.polimi.ingsw.model.updates;

import java.util.List;

public interface GameEvent {
    record BoardRefilledEvent(int row, List<Integer> newCardIds) implements GameEvent {}
    
    record CardAddedToTribeEvent(String nickname, Integer cardId) implements GameEvent {}
    
    record CardTakenEvent(String nickname, int row, int col) implements GameEvent {}
    
    record EraTransitionEvent(int newEraNumber) implements GameEvent {}

    record GameOverEvent(List<PlayerScoreUpdate> leaderboard) implements GameEvent {}
    
    record PlayerLeftGame(String nickname) implements GameEvent {}

    record PlayerResourcesChangedEvent(String nickname, int newFood, int newPrestige, int foodDiscount, String reason) implements GameEvent {}
    
    record RoundAdvancedEvent(int newRound) implements GameEvent {}
    
    record TotemPlacedEvent(String nickname, int positionIndex) implements GameEvent {}
    
    record WinnersAnnouncedEvent(List<String> winnersNicknames) implements GameEvent {}

    record TotemReturnedEvent(String nickname, int returnIndex) implements GameEvent {}
}
