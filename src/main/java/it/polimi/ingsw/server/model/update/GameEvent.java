package it.polimi.ingsw.server.model.update;

import it.polimi.ingsw.common.network.dto.event.*;
import it.polimi.ingsw.common.network.dto.PlayerScoreDto;
import java.util.List;

public interface GameEvent {

    GameEventDto toDTO();

    record BoardRefilledEvent(int row, List<Integer> newCardIds) implements GameEvent {
        @Override public GameEventDto toDTO() { return new BoardRefilledDto(row, newCardIds); }
    }

    record CardAddedToTribeEvent(String nickname, Integer cardId) implements GameEvent {
        @Override public GameEventDto toDTO() { return new CardAddedToTribeDto(nickname, cardId); }
    }

    record CardTakenEvent(String nickname, int row, int col) implements GameEvent {
        @Override public GameEventDto toDTO() { return new CardTakenDto(nickname, row, col); }
    }

    record EraTransitionEvent(int newEraNumber) implements GameEvent {
        @Override public GameEventDto toDTO() { return new EraTransitionDto(newEraNumber); }
    }

    record GameOverEvent(List<PlayerScoreUpdate> leaderboard) implements GameEvent {
        @Override public GameEventDto toDTO() {
            List<PlayerScoreDto> dtoList = leaderboard.stream()
                .map(data -> new PlayerScoreDto(data.nickname(), data.finalScore(), data.remainingFood()))
                .toList();
            return new GameOverDto(dtoList);
        }
    }

    record PlayerLeftGame(String nickname) implements GameEvent {
        @Override public GameEventDto toDTO() { return new PlayerLeftGameDto(nickname); }
    }

    record PlayerResourcesChangedEvent(String nickname, int newFood, int newPrestige, int foodDiscount, int sustenanceDiscount, String reason) implements GameEvent {
        @Override public GameEventDto toDTO() { return new PlayerResourcesChangedDto(nickname, newFood, newPrestige, foodDiscount, sustenanceDiscount, reason); }
    }

    record RoundAdvancedEvent(int newRound) implements GameEvent {
        @Override public GameEventDto toDTO() { return new RoundAdvancedDto(newRound); }
    }

    record TotemPlacedEvent(String nickname, int positionIndex) implements GameEvent {
        @Override public GameEventDto toDTO() { return new TotemPlacedDto(nickname, positionIndex); }
    }

    record WinnersAnnouncedEvent(List<String> winnersNicknames) implements GameEvent {
        @Override public GameEventDto toDTO() { return new WinnersAnnouncedDto(winnersNicknames); }
    }

    record TotemReturnedEvent(String nickname, int returnIndex) implements GameEvent {
        @Override public GameEventDto toDTO() { return new TotemReturnedDto(nickname, returnIndex); }
    }
}