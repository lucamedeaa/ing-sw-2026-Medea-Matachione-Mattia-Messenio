package it.polimi.ingsw.client.view.tui.command;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.view.tui.OutputPort;
import it.polimi.ingsw.common.network.dto.BoardDto;
import it.polimi.ingsw.common.network.dto.action.PlaceTotemActionDto;
import it.polimi.ingsw.common.network.dto.action.SkipActionDto;
import it.polimi.ingsw.common.network.dto.action.TakeCardActionDto;
import it.polimi.ingsw.common.visitor.ActionVisitor;

public class ActionExecutor implements ActionVisitor {
    private final ServerCommandPort server;
    private final GameModel gameModel;
    private final OutputPort out;
    private final String[] inputParts;

    public ActionExecutor(ServerCommandPort server, GameModel gameModel, OutputPort out, String[] inputParts) {
        this.server = server;
        this.gameModel = gameModel;
        this.out = out;
        this.inputParts = inputParts;
    }

    private boolean isStrictInteger(String str) {
        return str.matches("0|[1-9]\\d*");
    }

    @Override
    public void visit(PlaceTotemActionDto action) {
        if (inputParts.length != 2 || !isStrictInteger(inputParts[1])) {
            gameModel.setGlobalError("Uso corretto: <id_azione> <indice_tessera> (senza zeri iniziali)");
            return;
        }
        try {
            int tileIdx = Integer.parseInt(inputParts[1]);
            server.placeTotem(tileIdx);
        } catch (NumberFormatException e) {
            out.print("L'indice deve essere un numero valido.");
        }
    }

    @Override
    public void visit(TakeCardActionDto action) {
        if (inputParts.length != 3 || !isStrictInteger(inputParts[1]) || !isStrictInteger(inputParts[2])) {
            gameModel.setGlobalError("L'indice deve essere un numero valido.");
            return;
        }
        try {
            int row = Integer.parseInt(inputParts[1]);
            int col = Integer.parseInt(inputParts[2]);
            server.takeCard(row, col);
        } catch (NumberFormatException e) {
            gameModel.setGlobalError("Gli indici devono essere numeri validi.");
        }
    }

    @Override
    public void visit(SkipActionDto action) {
        server.skipAction();
    }

    @Override
    public void visit(BoardDto board) {}
}