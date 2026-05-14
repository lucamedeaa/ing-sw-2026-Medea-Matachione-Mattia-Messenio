package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.model.ClientSession;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import java.util.List;

public class TribePanelController {

    @FXML private TabPane tribeTabPane;

    private GameModel gameModel;
    private ClientSession session;

    public void init(GameModel gameModel, ClientSession session) {
        this.gameModel = gameModel;
        this.session = session;
    }

    public void update() {

            List<String> players = List.copyOf(gameModel.getPlayers().keySet());
            tribeTabPane.getTabs().clear();

            for (String player : players) {
                Tab tab = new Tab(player);
                tab.setClosable(false);

                FlowPane cardsPane = new FlowPane();
                cardsPane.setHgap(8);
                cardsPane.setVgap(8);

                var tribe = gameModel.getTribes().get(player);
                if (tribe != null) {
                    tribe.forEach(card ->
                            cardsPane.getChildren().add(new Label(card.toString()))
                    );
                }

                tab.setContent(cardsPane);

                if (player.equals(session.getNickname())) {
                    tab.setStyle("-fx-font-weight: bold;");
                }

                tribeTabPane.getTabs().add(tab);
            }

    }

    public void refresh(GameModel model) {
        Platform.runLater(() -> {
            tribeTabPane.getTabs().clear();

            // Itera sui giocatori per creare un Tab per ognuno
            for (String playerName : model.getPlayers().keySet()) {
                Tab playerTab = new Tab(playerName);
                playerTab.setClosable(false);

                // Contenitore a scorrimento orizzontale per le carte della tribù
                ScrollPane scroll = new ScrollPane();
                HBox cardsContainer = new HBox(10);
                cardsContainer.setPadding(new Insets(10));

                // Recupera le carte del giocatore e le renderizza
                List<Integer> tribeCardIds = model.getTribes().get(playerName);
                if (tribeCardIds != null) {
                    renderTribeInContainer(tribeCardIds, cardsContainer);
                }

                scroll.setContent(cardsContainer);
                playerTab.setContent(scroll);
                tribeTabPane.getTabs().add(playerTab);
            }
        });
    }

    private void renderTribeInContainer(List<Integer> cardIds, HBox container) {
        for (Integer id : cardIds) {
            // Recupera l'asset usando l'ID della carta
            Image img = it.polimi.ingsw.client.view.gui.GuiAssetManager.getCardImage(id);

            if (img != null) {
                ImageView iv = new ImageView(img);
                iv.setPreserveRatio(true);

                // Altezza fissa per le carte nella tab, altrimenti occupano troppo spazio
                iv.setFitHeight(140);

                container.getChildren().add(iv);
            }
        }
}
}