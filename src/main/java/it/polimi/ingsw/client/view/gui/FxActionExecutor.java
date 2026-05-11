package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.common.network.dto.BoardDto;
import it.polimi.ingsw.common.network.dto.action.PlaceTotemActionDto;
import it.polimi.ingsw.common.network.dto.action.SkipActionDto;
import it.polimi.ingsw.common.network.dto.action.TakeCardActionDto;
import it.polimi.ingsw.common.visitor.ActionVisitor;

// Visitor delle ActionDto per la GUI. Equivalente funzionale di ActionExecutor TUI,
// senza OutputPort né String[]. I parametri vengono raccolti dall'UI prima della chiamata.
// Usare i factory methods statici per costruire l'executor — evitano parametri ambigui.
// Esempio: action.accept(FxActionExecutor.forTotem(ctx, tileIdx))
public class FxActionExecutor implements ActionVisitor {

    private final GuiContext ctx;
    private final int param1;
    private final int param2;

    private FxActionExecutor(GuiContext ctx, int param1, int param2) {
        this.ctx = ctx;
        this.param1 = param1;
        this.param2 = param2;
    }

    // Uso: action.accept(FxActionExecutor.forTotem(ctx, tileIdx))
    public static FxActionExecutor forTotem(GuiContext ctx, int tileIdx) {
        return new FxActionExecutor(ctx, tileIdx, -1);
    }

    public static FxActionExecutor forCard(GuiContext ctx, int upper, int lower) {
        return new FxActionExecutor(ctx, upper, lower);
    }

    public static FxActionExecutor forSkip(GuiContext ctx) {
        return new FxActionExecutor(ctx, -1, -1);
    }

    @Override
    public void visit(PlaceTotemActionDto action) {
        ctx.controller().placeTotem(param1);
    }

    @Override
    public void visit(TakeCardActionDto action) {
        ctx.controller().takeCard(param1, param2);
    }

    @Override
    public void visit(SkipActionDto action) {
        ctx.controller().skipAction();
    }

    @Override
    public void visit(BoardDto board) {
        // no-op — BoardDto non rappresenta un'azione del giocatore
    }
}