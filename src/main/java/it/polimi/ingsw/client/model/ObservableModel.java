package it.polimi.ingsw.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

//PER EVITARE CODICE DUPLICATEO NELLA GESTIONE DELLE NOTIFICHE UI

public abstract class ObservableModel {
    private final List<UIObserver> observers = new ArrayList<>();
    protected boolean batchMode = false;
    private String globalError = "";

    protected final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public void addObserver(UIObserver observer) {
        this.observers.add(observer);
    }

    public void executeBatch(Runnable updates) {
        lock.writeLock().lock();
        batchMode = true;
        try {
            updates.run();
        } finally {
            batchMode = false;
            lock.writeLock().unlock();
            notifyUI();
        }
    }

    protected void notifyUI() {
        if (batchMode) return;
        for (UIObserver obs : observers) {
            obs.onStateChanged();
        }
    }
    public void setGlobalError(String error) {
        lock.writeLock().lock();
        try {
            this.globalError = error;
        } finally {
            lock.writeLock().unlock();
        }
        notifyUI();
    }

    public Lock getReadLock() {
        return lock.readLock();
    }

    public String consumeGlobalError() {
        lock.writeLock().lock();
        try {
            String err = this.globalError;
            this.globalError = "";
            return err;
        } finally {
            lock.writeLock().unlock();
        }
    }
    public void setGlobalErrorSilent(String error) {
        lock.writeLock().lock();
        try {
            this.globalError = error;
        } finally {
            lock.writeLock().unlock();
        }
    }
}