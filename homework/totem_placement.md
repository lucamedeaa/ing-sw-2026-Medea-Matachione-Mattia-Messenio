# Totem Placement

Placing a totem follows the same pipeline as picking a card, so only the payload and the resulting event differ. The request arrives as a `PlaceTotemMessage`, the `InGameConnectionState` session delegates to the controller, and the move executes asynchronously on the game executor via `submitGameTask`, keeping game logic off the network threads. The model mutates state and enqueues events that stay private until `commitEvents()` publishes them as one atomic batch.

The Observer fan out then delivers a `deltaEvent` to every player's filtered `VirtualView`, and on the client the Visitor applies it through `visit(TotemPlacedDto)`, which clears any pending delta state before updating the totem position, so the board re-renders in a single refresh. The illegal path matches Card Pick's: an `InvalidGameActionException` is caught and returned only to the acting player as a `"Move error: ..."` popup, with no effect on any other view.
