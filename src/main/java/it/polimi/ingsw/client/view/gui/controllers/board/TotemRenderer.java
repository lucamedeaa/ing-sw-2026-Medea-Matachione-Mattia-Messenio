package it.polimi.ingsw.client.view.gui.controllers.board;

import it.polimi.ingsw.client.view.gui.viewstate.BoardViewState;
import it.polimi.ingsw.client.view.gui.viewstate.PlayerInfo;
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

    public void renderTurnOrderTotems(BoardViewState state) {
        trackOverlays.forEach(p -> p.getChildren().clear());
        Map<String, Integer> offerPos = state.totemPositions();
        Map<String, Integer> retPos   = state.returnPositions();
        double[] ySteps = BoardLayoutProvider.getTotemYSteps(state.players().size());
        int fallback = 0;

        for (PlayerInfo p : state.players()) {
            ImageView tv = factory.createTotemView(p.totemColor());
            if (tv == null) continue;
            Integer offer = offerPos.get(p.nickname());
            Integer ret   = retPos.get(p.nickname());

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