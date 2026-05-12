package it.polimi.ingsw.client.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

//PER EVITARE CODICE DUPLICATEO NELLA GESTIONE DELLE NOTIFICHE UI

/**
 * Base class for client models that notify UI observers after state changes.
 */
public abstract class ObservableModel {
    private final List<UIObserver> observers = new CopyOnWriteArrayList<>();
    protected volatile boolean batchMode = false;
    private String globalError = "";

    protected final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Registers a UI observer for future state-change notifications.
     *
     * @param observer observer to register
     */
    public void addObserver(UIObserver observer) {
        this.observers.add(observer);
    }

    /**
     * Executes multiple model updates while emitting a single UI notification.
     *
     * @param updates updates to run under the model write lock
     */
    public void executeBatch(Runnable updates) {
        lock.writeLock().lock();
        boolean wasBatching = batchMode;
        batchMode = true;

        try {
            updates.run();
        } finally {
            batchMode = wasBatching;
            lock.writeLock().unlock();
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

    /**
     * Notifies observers unless the model is currently batching updates.
     */
    protected void notifyUI() {
        if (batchMode) return;
        for (UIObserver obs : observers) {
            obs.onStateChanged();
        }
    }
    /**
     * Stores a global error and notifies observers.
     *
     * @param error error text to expose to the UI
     */
    public void setGlobalError(String error) {
        lock.writeLock().lock();
        try {
            if (!this.globalError.isEmpty()) {
                this.globalError += "\n";
            }
            this.globalError += error;
        } finally {
            lock.writeLock().unlock();
        }
        forceNotifyUI();
    }

    /**
     * Returns the read lock guarding this model.
     *
     * @return read lock for safe UI reads
     */
    public Lock getReadLock() {
        return lock.readLock();
    }

    /**
     * Returns and clears the latest global error.
     *
     * @return current global error, or an empty string if none is pending
     */
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
    /**
     * Stores a global error without notifying observers immediately.
     *
     * @param error error text to expose later
     */
    public void setGlobalErrorSilent(String error) {
        lock.writeLock().lock();
        try {
            if (!this.globalError.isEmpty()) {
                this.globalError += "\n";
            }
            this.globalError += error;
        } finally {
            lock.writeLock().unlock();
        }
    }
}