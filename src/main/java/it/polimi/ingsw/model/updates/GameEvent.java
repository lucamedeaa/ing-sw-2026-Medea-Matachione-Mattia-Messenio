package it.polimi.ingsw.model.updates;

import it.polimi.ingsw.network.dto.GameEventDTO;
import it.polimi.ingsw.network.dto.events.*;
import it.polimi.ingsw.network.dto.PlayerScoreDTO;
import java.util.List;

public interface GameEvent {

    GameEventDTO toDTO();

    record BoardRefilledEvent(int row, List<Integer> newCardIds) implements GameEvent {
        @Override public GameEventDTO toDTO() { return new BoardRefilledEventDTO(row, newCardIds); }
    }

    record CardAddedToTribeEvent(String nickname, Integer cardId) implements GameEvent {
        @Override public GameEventDTO toDTO() { return new CardAddedToTribeEventDTO(nickname, cardId); }
    }

    record CardTakenEvent(String nickname, int row, int col) implements GameEvent {
        @Override public GameEventDTO toDTO() { return new CardTakenEventDTO(nickname, row, col); }
    }

    record EraTransitionEvent(int newEraNumber) implements GameEvent {
        @Override public GameEventDTO toDTO() { return new EraTransitionEventDTO(newEraNumber); }
    }

    record GameOverEvent(List<PlayerScoreUpdate> leaderboard) implements GameEvent {
        @Override public GameEventDTO toDTO() {
            List<PlayerScoreDTO> dtoList = leaderboard.stream()
                .map(data -> new PlayerScoreDTO(data.nickname(), data.finalScore(), data.remainingFood()))
                .toList();
            return new GameOverEventDTO(dtoList);
        }
    }

    record PlayerLeftGame(String nickname) implements GameEvent {
        @Override public GameEventDTO toDTO() { return new PlayerLeftGameDTO(nickname); }
    }

    record PlayerResourcesChangedEvent(String nickname, int newFood, int newPrestige, int foodDiscount, String reason) implements GameEvent {
        @Override public GameEventDTO toDTO() { return new PlayerResourcesChangedEventDTO(nickname, newFood, newPrestige, foodDiscount, reason); }
    }

    record RoundAdvancedEvent(int newRound) implements GameEvent {
        @Override public GameEventDTO toDTO() { return new RoundAdvancedEventDTO(newRound); }
    }

    record TotemPlacedEvent(String nickname, int positionIndex) implements GameEvent {
        @Override public GameEventDTO toDTO() { return new TotemPlacedEventDTO(nickname, positionIndex); }
    }

    record WinnersAnnouncedEvent(List<String> winnersNicknames) implements GameEvent {
        @Override public GameEventDTO toDTO() { return new WinnersAnnouncedEventDTO(winnersNicknames); }
    }

    record TotemReturnedEvent(String nickname, int returnIndex) implements GameEvent {
        @Override public GameEventDTO toDTO() { return new TotemReturnedEventDTO(nickname, returnIndex); }
    }
}