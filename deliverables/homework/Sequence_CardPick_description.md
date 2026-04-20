When a player interacts with the View by clicking a card on the board, a TakeCardMessage is sent upstream via the VirtualServer.On the server side, the VirtualView dispatches the message to the GameController via the Visitor Pattern. 
To keep the Controller thin and deeply decoupled from the business rules, the Controller does not perform manual pre-checks. Instead, it delegates the action directly to the Model (takeCard) wrapped within a try-catch block.

The Model acts as a Rich Domain Model, encapsulating all game logic and validation (such as checking for the correct turn or sufficient food). If the action violates any rule, an exception is thrown, which the Controller surfaces as an InvalidActionException. 

The VirtualView handles this by sending an ErrorMessageDTO downstream to display an Error Popup to the Player. Conversely, if the action is entirely valid, the Model updates its internal state by creating a GameMemento and triggers notifyObservers(). 
This broadcasts the new state as a GameSnapshotDTO to all clients asynchronously, allowing the client's Visitor Pattern to process the CardTakenEventDTO and visually animate the card moving toward the player.
