package it.polimi.ingsw.server.model.update;

import it.polimi.ingsw.common.network.dto.event.*;
import it.polimi.ingsw.common.network.dto.PlayerScoreDto;
import java.util.List;

/**
 * Server-side game event that can be converted to a client-facing DTO.
 */
public interface GameEvent {

    /**
     * Converts this event to its network DTO.
     *
     * @return game event DTO
     */
    GameEventDto toDTO();

    /**
     * Event emitted when a board row is refilled.
     *
     * @param row row index that changed
     * @param newCardIds new card identifiers in that row
     */
    record BoardRefilledEvent(int row, List<Integer> newCardIds) implements GameEvent {
        @Override public GameEventDto toDTO() { return new BoardRefilledDto(row, newCardIds); }
    }

    /**
     * Event emitted when a card enters a player's tribe.
     *
     * @param nickname player nickname
     * @param cardId identifier of the added card
     */
    record CardAddedToTribeEvent(String nickname, Integer cardId) implements GameEvent {
        @Override public GameEventDto toDTO() { return new CardAddedToTribeDto(nickname, cardId); }
    }

    /**
     * Event emitted when a card is taken from the board.
     *
     * @param nickname player nickname
     * @param row source row
     * @param col source column
     */
    record CardTakenEvent(String nickname, int row, int col) implements GameEvent {
        @Override public GameEventDto toDTO() { return new CardTakenDto(nickname, row, col); }
    }

    /**
     * Event emitted when a new era begins.
     *
     * @param newEraNumber new era number
     */
    record EraTransitionEvent(int newEraNumber) implements GameEvent {
        @Override public GameEventDto toDTO() { return new EraTransitionDto(newEraNumber); }
    }

    /**
     * Event emitted when the final leaderboard is available.
     *
     * @param leaderboard final player scores
     */
    record GameOverEvent(List<PlayerScoreUpdate> leaderboard) implements GameEvent {
        @Override public GameEventDto toDTO() {
            List<PlayerScoreDto> dtoList = leaderboard.stream()
                .map(PlayerScoreUpdate::toDTO)
                .toList();
            return new GameOverDto(dtoList);
        }
    }


    /**
     * Event emitted when a player's resources change.
     *
     * @param nickname player nickname
     * @param newFood updated food amount
     * @param newPrestige updated prestige amount
     * @param foodDiscount updated permanent food discount
     * @param sustenanceDiscount updated Sustenance discount
     * @param reason optional user-facing explanation
     */
    record PlayerResourcesChangedEvent(String nickname, int newFood, int newPrestige, int foodDiscount, int sustenanceDiscount, String reason) implements GameEvent {
        @Override public GameEventDto toDTO() { return new PlayerResourcesChangedDto(nickname, newFood, newPrestige, foodDiscount, sustenanceDiscount, reason); }
    }

    /**
     * Event emitted when the game advances to a new round.
     *
     * @param newRound new round number
     */
    record RoundAdvancedEvent(int newRound) implements GameEvent {
        @Override public GameEventDto toDTO() { return new RoundAdvancedDto(newRound); }
    }

    /**
     * Event emitted when a totem is placed on the offer track.
     *
     * @param nickname player nickname
     * @param positionIndex offer-track position
     */
    record TotemPlacedEvent(String nickname, int positionIndex) implements GameEvent {
        @Override public GameEventDto toDTO() { return new TotemPlacedDto(nickname, positionIndex); }
    }



    /**
     * Event emitted when a totem returns to the next-round order.
     *
     * @param nickname player nickname
     * @param returnIndex return position
     */
    record TotemReturnedEvent(String nickname, int returnIndex) implements GameEvent {
        @Override public GameEventDto toDTO() { return new TotemReturnedDto(nickname, returnIndex); }
    }
}
