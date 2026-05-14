package it.polimi.ingsw.client.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class ObservableModel {
    private final List<UIObserver> observers = new CopyOnWriteArrayList<>();
    protected boolean batchMode = false;
    private String globalError = "";


    public void addObserver(UIObserver observer) {
        this.observers.add(observer);
    }

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

    public void setGlobalError(String error) {
        if (!this.globalError.isEmpty()) {
            this.globalError += "\n";
        }
        this.globalError += error;
        forceNotifyUI();
    }

    public String consumeGlobalError() {
        String err = this.globalError;
        this.globalError = "";
        return err;
    }

    public void setGlobalErrorSilent(String error) {
        if (!this.globalError.isEmpty()) {
            this.globalError += "\n";
        }
        this.globalError += error;
    }
}