package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.model.ClientSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.FlowPane;

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
        gameModel.getReadLock().lock();
        try {
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
        } finally {
            gameModel.getReadLock().unlock();
        }
    }
}