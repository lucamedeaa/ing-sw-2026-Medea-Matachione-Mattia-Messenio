package it.polimi.ingsw.virtualView;

import it.polimi.ingsw.model.updates.*;
import it.polimi.ingsw.model.updates.GameEvent.*;
import it.polimi.ingsw.network.dto.*;
import it.polimi.ingsw.network.dto.actions.*;
import it.polimi.ingsw.network.dto.events.*;

import java.util.List;

public class MapperToDTO {

    public static GameEventDTO eventToDTO(GameEvent update) {
        return switch (update) {

            case BoardRefilledEvent b ->
                new BoardRefilledEventDTO(b.row(), b.newCardIds());

            case CardAddedToTribeEvent c ->
                new CardAddedToTribeEventDTO(c.nickname(), c.cardId());

            case CardTakenEvent c ->
                new CardTakenEventDTO(c.nickname(), c.row(), c.col());

            case EraTransitionEvent e ->
                new EraTransitionEventDTO(e.newEraNumber());

            case PlayerLeftGame p ->
                new PlayerLeftGameDTO(p.nickname());

            case PlayerResourcesChangedEvent p ->
                    new PlayerResourcesChangedEventDTO(p.nickname(), p.newFood(), p.newPrestige(),p.foodDiscount(), p.reason());

            case GameOverEvent g -> {
                List<PlayerScoreDTO> dtoList = g.leaderboard().stream()
                    .map(data -> new PlayerScoreDTO(
                            data.nickname(),
                            data.finalScore(),
                            data.remainingFood()
                    ))
                    .toList();
                yield new GameOverEventDTO(dtoList);
            }

            case RoundAdvancedEvent r ->
                new RoundAdvancedEventDTO(r.newRound());


            case TotemPlacedEvent t ->
                new TotemPlacedEventDTO(t.nickname(), t.positionIndex());


            case WinnersAnnouncedEvent w ->
                new WinnersAnnouncedEventDTO(w.winnersNicknames());

            case TotemReturnedEvent t ->
                    new TotemReturnedEventDTO(t.nickname(), t.returnIndex());


            default -> throw new IllegalArgumentException("Errore di mapping: update del Model non gestito o di tipo errato -> " + update.getClass().getSimpleName());
        };


    }

    public static AvailableActionDTO availableActionToDTO(AvailableAction action) {
        return switch (action) {
            
            case AvailableAction.PlaceTotemAction p ->
                new PlaceTotemActionDTO(p.availableTileIndices());
                
            case AvailableAction.TakeCardAction t ->
                new TakeCardActionDTO(t.upperRowPick(), t.lowerRowPick());
                
            case AvailableAction.SkipAction s ->
                new SkipActionDTO();
                
            default -> 
                throw new IllegalArgumentException("Errore di mapping: azione del Model non gestita -> " + action.getClass().getSimpleName());
        };
    }

    public static PlayerDTO playerToDTO(PlayerUpdate player) {
        return new PlayerDTO(player.nickname(), player.food(), player.prestige(), player.totemColor(), player.foodDiscount());
    }

    public static BoardDTO   boardToDTO(BoardUpdate board) {
        return new BoardDTO(board.UpperRowCards(), board.LowerRowCards(), board.currentEra(), board.currentRound());
    }

    public static ModelUpdateDTO  modelToDTO(ModelUpdate model) {
        return new ModelUpdateDTO(model.events().stream().map(MapperToDTO::eventToDTO).toList(), model.activePlayerNickname(), model.activePlayerActions().stream().map(MapperToDTO::availableActionToDTO).toList());
    }

    public static PlayerScoreDTO PlayerScoreUpdateToDTO(PlayerScoreUpdate playerScore) {
        return new PlayerScoreDTO(playerScore.nickname(), playerScore.finalScore(), playerScore.remainingFood());
    }

    public static TribeDTO tribeUpdatetoDTO(TribeUpdate tribe){
        return new TribeDTO(tribe.nickname(), tribe.tribe());
    }

}