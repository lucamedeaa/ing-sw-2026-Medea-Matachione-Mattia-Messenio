package it.polimi.ingsw.client.view.gui.interaction;

/**
 * Current board interaction mode.
 */
public enum InteractionState {
    /** No board interaction is active. */
    IDLE,

    /** The user is selecting a totem position. */
    SELECTING_TOTEM_POSITION,

    /** The user is selecting a card to take. */
    SELECTING_CARD_TO_TAKE
}
