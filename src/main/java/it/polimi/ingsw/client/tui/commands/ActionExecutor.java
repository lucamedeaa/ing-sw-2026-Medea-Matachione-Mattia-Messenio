package it.polimi.ingsw.client.tui.commands;

import it.polimi.ingsw.client.tui.NavigationPort;
import it.polimi.ingsw.client.tui.OutputPort;
import it.polimi.ingsw.network.dto.BoardDTO;
import it.polimi.ingsw.network.dto.actions.PlaceTotemActionDTO;
import it.polimi.ingsw.network.dto.actions.SkipActionDTO;
import it.polimi.ingsw.network.dto.actions.TakeCardActionDTO;
import it.polimi.ingsw.network.visitor.ActionVisitor;

public class ActionExecutor implements ActionVisitor {
    private final NavigationPort nav;
    private final OutputPort out;
    private final String[] inputParts;

    public ActionExecutor(NavigationPort nav, OutputPort out, String[] inputParts) {
        this.nav = nav;
        this.out = out;
        this.inputParts = inputParts;
    }

    private boolean isStrictInteger(String str) {
        return str.matches("0|[1-9]\\d*");
    }

    @Override
    public void visit(PlaceTotemActionDTO action) {
        if (inputParts.length != 2 || !isStrictInteger(inputParts[1])) {
            out.print("Uso corretto: <id_azione> <indice_tessera> (senza zeri iniziali)");
            return;
        }
        try {
            int tileIdx = Integer.parseInt(inputParts[1]);
            nav.getController().placeTotem(tileIdx);
        } catch (NumberFormatException e) {
            out.print("L'indice deve essere un numero valido.");
        }
    }

    @Override
    public void visit(TakeCardActionDTO action) {
        if (inputParts.length != 3 || !isStrictInteger(inputParts[1]) || !isStrictInteger(inputParts[2])) {
            out.print("Uso corretto: <id_azione> <riga> <colonna> (senza zeri iniziali)");
            return;
        }
        try {
            int row = Integer.parseInt(inputParts[1]);
            int col = Integer.parseInt(inputParts[2]);
            nav.getController().takeCard(row, col);
        } catch (NumberFormatException e) {
            out.print("Gli indici devono essere numeri validi.");
        }
    }

    @Override
    public void visit(SkipActionDTO action) {
        nav.getController().skipAction();
    }

    @Override
    public void visit(BoardDTO board) {}
}