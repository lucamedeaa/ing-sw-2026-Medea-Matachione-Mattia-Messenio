package it.polimi.ingsw.client.tui.render;

import it.polimi.ingsw.client.tui.TUI;
import it.polimi.ingsw.network.dto.BoardDTO;
import it.polimi.ingsw.network.dto.actions.PlaceTotemActionDTO;
import it.polimi.ingsw.network.dto.actions.SkipActionDTO;
import it.polimi.ingsw.network.dto.actions.TakeCardActionDTO;
import it.polimi.ingsw.network.visitor.ActionVisitor;

public class ActionExecutor implements ActionVisitor {
    private final TUI tui;
    private final String[] inputParts;

    public ActionExecutor(TUI tui, String[] inputParts) {
        this.tui = tui;
        this.inputParts = inputParts;
    }

    @Override
    public void visit(PlaceTotemActionDTO action) {
        if (inputParts.length != 2) {
            tui.print("Uso corretto: <id_azione> <indice_tessera>");
            return;
        }
        try {
            int tileIdx = Integer.parseInt(inputParts[1]);
            tui.getController().placeTotem(tileIdx);
        } catch (NumberFormatException e) {
            tui.print("L'indice deve essere un numero.");
        }
    }

    @Override
    public void visit(TakeCardActionDTO action) {
        if (inputParts.length != 3) {
            tui.print("Uso corretto: <id_azione> <riga> <colonna>");
            return;
        }
        try {
            int row = Integer.parseInt(inputParts[1]);
            int col = Integer.parseInt(inputParts[2]);
            tui.getController().takeCard(row, col);
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            tui.print("Usage: <action_id> <row> <column>");
        }
    }

    @Override
    public void visit(SkipActionDTO action) {
        tui.getController().skipAction();
    }

    @Override
    public void visit(BoardDTO board) {
        // Ignored in this context
    }
}