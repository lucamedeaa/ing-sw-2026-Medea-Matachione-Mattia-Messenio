When a player interacts with the view, a PlaceTotemMessage is sent upstream via the VirtualServer.

On the server side, the VirtualView dispatches the message to the GameController via the Visitor Pattern.
Then the controller delegates the action directly to the model (placeTotem) wrapped within a try-catch block.

If the action violates any rule (e.g., wrong turn, incorrect phase, or invalid tile index), the model
throws an exception. The controller catches this exception and translates it into an InvalidActionException,
which the VirtualView handles by sending an ErrorMessageDTO downstream to display an Error Popup to the Player.

If the move is valid, the model applies the change and notifies the VirtualView via the Observer pattern.
The VirtualView then generates a GameSnapshotDTO containing the newly created TotemPlacedEventDTO.
Once the client receives the snapshot, it visits the DeltaEvents list in the snapshot, triggering the
method to visually move the player's marker on the screen.[Sequence_Diagram_Totem_Placement.pdf](../../../Downloads/Sequence_Diagram_Totem_Placement.pdf)