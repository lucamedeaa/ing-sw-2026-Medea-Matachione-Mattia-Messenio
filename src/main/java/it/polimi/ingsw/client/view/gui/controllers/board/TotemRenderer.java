package it.polimi.ingsw.client.view.gui.controllers.board;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.model.snapshot.PlayerSnapshot;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.List;
import java.util.Map;

public class TotemRenderer {
    private final List<Pane> trackOverlays;
    private final CardNodeFactory factory;

    public TotemRenderer(List<Pane> trackOverlays, CardNodeFactory factory) {
        this.trackOverlays = trackOverlays; this.factory = factory;
    }

    public void renderTurnOrderTotems(GameModel model) {
        trackOverlays.forEach(p -> p.getChildren().clear());
        Map<String, PlayerSnapshot> players = model.getPlayers();
        Map<String,Integer> offerPos = model.getTotemPositions();
        Map<String,Integer> retPos = model.getReturnPositions();

        double[] ySteps = BoardLayoutProvider.getTotemYSteps(players.size());
        int fallback = 0;

        for (PlayerSnapshot p : players.values()) {
            ImageView tv = factory.createTotemView(p.getTotemColor());

            if (tv == null) continue;
            Integer offer = offerPos.get(p.getNickname());
            Integer ret   = retPos.get(p.getNickname());

            if (offer != null) {
                int visualCol = offer + 1;
                if (visualCol < trackOverlays.size()) {
                    Pane overlay = trackOverlays.get(visualCol);
                    tv.layoutYProperty().bind(overlay.heightProperty().multiply(0.18));
                    overlay.getChildren().add(tv);
                }
            } else {
                Pane overlay = trackOverlays.get(0);

                int yIdx = (ret != null) ? ret : fallback++;

                if (yIdx >= 0 && yIdx < ySteps.length) {
                    tv.layoutYProperty().bind(overlay.heightProperty().multiply(ySteps[yIdx]));
                    overlay.getChildren().add(tv);
                }
            }
        }
    }
}