package it.polimi.ingsw.client.tui.commands;

import it.polimi.ingsw.client.lightGameModel.MatchModel;
import it.polimi.ingsw.client.tui.NavigationPort;
import it.polimi.ingsw.client.tui.OutputPort;
import it.polimi.ingsw.network.dto.BoardDTO;
import it.polimi.ingsw.network.dto.actions.PlaceTotemActionDTO;
import it.polimi.ingsw.network.dto.actions.SkipActionDTO;
import it.polimi.ingsw.network.dto.actions.TakeCardActionDTO;
import it.polimi.ingsw.network.visitor.ActionVisitor;
import it.polimi.ingsw.client.tui.ServerCommandPort;

public class ActionExecutor implements ActionVisitor {
    private final ServerCommandPort server;
    private final MatchModel matchModel;
    private final OutputPort out;
    private final String[] inputParts;

    public ActionExecutor(ServerCommandPort server, MatchModel matchModel, OutputPort out, String[] inputParts) {
        this.server = server;
        this.matchModel = matchModel;
        this.out = out;
        this.inputParts = inputParts;
    }

    private boolean isStrictInteger(String str) {
        return str.matches("0|[1-9]\\d*");
    }

    @Override
    public void visit(PlaceTotemActionDTO action) {
        if (inputParts.length != 2 || !isStrictInteger(inputParts[1])) {
            matchModel.setGlobalError("Uso corretto: <id_azione> <indice_tessera> (senza zeri iniziali)");
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
    public void visit(TakeCardActionDTO action) {
        if (inputParts.length != 3 || !isStrictInteger(inputParts[1]) || !isStrictInteger(inputParts[2])) {
            matchModel.setGlobalError("L'indice deve essere un numero valido.");
            return;
        }
        try {
            int row = Integer.parseInt(inputParts[1]);
            int col = Integer.parseInt(inputParts[2]);
            server.takeCard(row, col);
        } catch (NumberFormatException e) {
            matchModel.setGlobalError("Gli indici devono essere numeri validi.");
        }
    }

    @Override
    public void visit(SkipActionDTO action) {
        server.skipAction();
    }

    @Override
    public void visit(BoardDTO board) {}
}