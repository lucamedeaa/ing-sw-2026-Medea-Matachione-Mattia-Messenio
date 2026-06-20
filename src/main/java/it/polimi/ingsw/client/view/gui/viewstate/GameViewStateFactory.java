package it.polimi.ingsw.client.view.gui.viewstate;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.model.snapshot.PlayerSnapshot;
import it.polimi.ingsw.client.view.gui.controllers.board.CardAffordabilityPolicy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Factory that adapts {@link GameModel} data into GUI-specific view-state records.
 */
public class GameViewStateFactory {

    private GameViewStateFactory() {}

    /**
     * Builds the current in-game view state for a local player.
     *
     * @param m client-side game model
     * @param self nickname of the local player
     * @return view state consumed by the in-game screen
     */
    public static GameViewState from(GameModel m, String self) {
        List<PlayerInfo> playerInfos = m.getPlayers().values().stream()
                .map(p -> new PlayerInfo(p.getNickname(), p.getTotemColor(), p.getFood(), p.getPrestige()))
                .toList();

        PlayerSnapshot me = m.getPlayers().get(self);
        Set<Integer> affordable   = new HashSet<>();
        Set<Integer> unaffordable = new HashSet<>();
        if (me != null) {
            Stream.concat(m.getUpperRowCards().stream(), m.getLowerRowCards().stream()) // split board cards into affordable / unaffordable-but-selectable; events are skipped
                    .filter(id -> id != null && !CardAffordabilityPolicy.isEvent(id))
                    .forEach(id -> {
                        if (CardAffordabilityPolicy.isAffordable(id, me)) affordable.add(id);
                        else unaffordable.add(id);
                    });
        }

        return new GameViewState(
                new BoardViewState(m.getPlayers().size(), m.getUpperRowCards(), m.getLowerRowCards(),
                        playerInfos, m.getTotemPositions(), m.getReturnPositions(), m.getNextDeckEra(), m.getCurrentEra(), m.getCurrentRound()),
                new ActionsViewState(m.getMyActions(), self.equals(m.getActivePlayer()), affordable, unaffordable),
                playerInfos, m.getTribes(), m.consumeGameLogs(), self, m.getActivePlayer());
    }
}
