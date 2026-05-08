package it.polimi.ingsw.client.lightGameModel;

import java.util.ArrayList;
import java.util.List;

//PER EVITARE CODICE DUPLICATEO NELLA GESTIONE DELLE NOTIFICHE UI

public abstract class ObservableModel {
    private final List<UIObserver> observers = new ArrayList<>();
    protected boolean batchMode = false;
    private String globalError = "";

    public void addObserver(UIObserver observer) {
        this.observers.add(observer);
    }

    public void startBatch() { this.batchMode = true; }

    public void endBatch() {
        this.batchMode = false;
        notifyUI();
    }

    protected void notifyUI() {
        if (batchMode) return;
        for (UIObserver obs : observers) {
            obs.onStateChanged();
        }
    }
    public void setGlobalError(String error) {
        this.globalError = error;
        notifyUI();
    }

    public String consumeGlobalError() {
        String err = this.globalError;
        this.globalError = "";
        return err;
    }
}