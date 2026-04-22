package it.polimi.ingsw.model;

public interface ModelObserver {
    void onModelUpdate(GameMemento memento);
}