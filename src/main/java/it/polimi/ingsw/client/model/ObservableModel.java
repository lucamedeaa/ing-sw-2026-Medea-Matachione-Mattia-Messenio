package it.polimi.ingsw.client.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Base class for client models that notify UI observers about state changes. */
public abstract class ObservableModel {
    private final List<UIObserver> observers = new CopyOnWriteArrayList<>();
    protected boolean batchMode = false;
    private String globalError = "";


    /**
     * Registers a UI observer.
     *
     * @param observer observer to notify when the model changes
     */
    public void addObserver(UIObserver observer) {
        this.observers.add(observer);
    }

    /**
     * Executes multiple state changes and sends one final UI notification.
     *
     * @param updates state updates to execute
     */
    public void executeBatch(Runnable updates) {
        boolean wasBatching = batchMode;
        batchMode = true;

        try {
            updates.run();
        } finally {
            batchMode = wasBatching;
        }

        if (!wasBatching) {
            forceNotifyUI();
        }
    }

    protected void forceNotifyUI() {
        for (UIObserver obs : observers) {
            obs.onStateChanged();
        }
    }

    protected void notifyUI() {
        if (batchMode) return;
        forceNotifyUI();
    }

    /**
     * Sets the global error.
     *
     * @param error error message
     */
    public void setGlobalError(String error) {
        if (!this.globalError.isEmpty()) {
            this.globalError += "\n";
        }
        this.globalError += error;
        forceNotifyUI();
    }

    /**
     * Consumes the global error.
     *
     * @return accumulated error text, cleared from the model
     */
    public String consumeGlobalError() {
        String err = this.globalError;
        this.globalError = "";
        return err;
    }

    /**
     * Sets the global error silent.
     *
     * @param error error message
     */
    public void setGlobalErrorSilent(String error) {
        if (!this.globalError.isEmpty()) {
            this.globalError += "\n";
        }
        this.globalError += error;
    }
}
