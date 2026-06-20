package it.polimi.ingsw.client.view.tui.command;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.view.tui.OutputPort;
import it.polimi.ingsw.common.config.CardRegistry;
import it.polimi.ingsw.common.network.dto.BoardDto;
import it.polimi.ingsw.common.network.dto.action.PlaceTotemActionDto;
import it.polimi.ingsw.common.network.dto.action.SkipActionDto;
import it.polimi.ingsw.common.network.dto.action.TakeCardActionDto;
import it.polimi.ingsw.common.visitor.ActionVisitor;

import java.util.List;

/**
 * Visitor that validates action-specific command arguments and sends the resulting action to the server.
 */
public class ActionExecutor implements ActionVisitor {
    private final ServerCommandPort server;
    private final GameModel gameModel;
    private final OutputPort out;
    private final String[] inputParts;
    private boolean commandSent = false;


    /**
     * Creates an executor for a selected action.
     *
     * @param server server command port
     * @param gameModel model used to validate board selections
     * @param out output port used for local messages
     * @param inputParts tokenized command input
     */
    public ActionExecutor(ServerCommandPort server, GameModel gameModel, OutputPort out, String[] inputParts) {
        this.server = server;
        this.gameModel = gameModel;
        this.out = out;
        this.inputParts = inputParts;

    }

    /**
     * Returns whether this executor successfully sent a command to the server.
     *
     * @return true if a command was sent, false otherwise
     */
    public boolean isCommandSent() {
        return commandSent;
    }

    private boolean isStrictInteger(String str) {
        return str.matches("0|[1-9]\\d*");
    }

    /** {@inheritDoc} */
    @Override
    public void visit(PlaceTotemActionDto action) {
        if (inputParts.length != 2 || !isStrictInteger(inputParts[1])) {
            gameModel.setGlobalError("Correct usage: <action_id> <card_number> (without leading zeros)");
            return;
        }
        try {
            int tileIdx = Integer.parseInt(inputParts[1]);
            if (!action.availableTileIndices().contains(tileIdx)) {
                gameModel.setGlobalError("Position not valid. Available spaces: " + action.availableTileIndices());
                return;
            }
            server.placeTotem(tileIdx);
            commandSent = true;
        } catch (NumberFormatException e) {
            out.print("The index must be a valid number.");
        }
    }

    /** {@inheritDoc} */
    @Override
    public void visit(TakeCardActionDto action) {
        if (inputParts.length != 3 || !isStrictInteger(inputParts[1]) || !isStrictInteger(inputParts[2])) {
            gameModel.setGlobalError("The index must be a valid number.");
            return;
        }

        try {
            int row = Integer.parseInt(inputParts[1]);
            int col = Integer.parseInt(inputParts[2]);

            if (!isTakeCardSelectionValid(action, row, col)) {
                return;
            }

            server.takeCard(row, col);
            commandSent = true;
        } catch (NumberFormatException e) {
            gameModel.setGlobalError("Indexes must be valid numbers.");
        }
    }

    private boolean isTakeCardSelectionValid(TakeCardActionDto action, int row, int col) {
        if (row != 0 && row != 1) {
            gameModel.setGlobalError("Invalid row. Use 0 for upper row or 1 for lower row.");
            return false;
        }

        if (row == 0 && action.upperRowPick() <= 0) {
            gameModel.setGlobalError("You have no remaining picks from the upper row.");
            return false;
        }

        if (row == 1 && action.lowerRowPick() <= 0) {
            gameModel.setGlobalError("You have no remaining picks from the lower row.");
            return false;
        }

        List<Integer> rowCards = row == 0
                ? gameModel.getUpperRowCards()
                : gameModel.getLowerRowCards();

        if (col < 0 || col >= rowCards.size()) {
            gameModel.setGlobalError("Invalid card index.");
            return false;
        }

        Integer cardId = rowCards.get(col);
        if (cardId == null) {
            gameModel.setGlobalError("That slot is empty.");
            return false;
        }

        if ("Event".equals(CardRegistry.getCard(cardId).type())) {
            gameModel.setGlobalError("Event cards cannot be taken directly.");
            return false;
        }

        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void visit(SkipActionDto action) {
        server.skipAction();
        commandSent = true;
    }


}
