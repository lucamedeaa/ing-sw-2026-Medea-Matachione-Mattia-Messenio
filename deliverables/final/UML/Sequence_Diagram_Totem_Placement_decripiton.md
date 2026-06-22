This sequence diagram shows the flow used when a player places a totem during the game. The
client is already inside the match. The player clicks an offer tile in the game view, and
the view sends placeTotem(positionIndex) through the ServerProxy. The proxy writes a
PlaceTotemMessage on the socket connection. On the server, SocketClientHandler receives the
message and dispatches it to the ConnectionSession.

The session forwards the command to its current state. Since the player is already in game,
the active state is InGameConnectionState. This state adds the player nickname to the
request and calls handlePlaceTotem on the GameController. The controller does not execute
the move directly on the network thread: it submits the operation to the single game
executor used by that match.

The model receives the command as placeTotem(nickname, positionIndex) and delegates it to
the current game state. During this phase the expected state is PlacementState. The state
checks that the player is actually the current player for totem placement. If the move is
invalid, for example because it is not that player's turn, an InvalidGameActionException is
thrown. The controller catches it and uses the error callback passed by
InGameConnectionState; the client receives an ErrorMessage, updates the GameModel error
state, and shows the error in the game view.

If the move is valid, PlacementState places the totem on the selected tile, advances the
placement order, and adds a TotemPlacedEvent to the model. If there are still players who
must place their totem, the game stays in PlacementState and the next active player receives
a PlaceTotemAction with the remaining free tiles. If all totems have been placed, the state
changes to ActionState. At that point the action phase starts: the first action-phase player
is selected from the offer track, possible tile bonuses may generate resource events, and
the next available actions become card-taking actions, plus SkipAction when allowed.

After the move, the controller calls commitEvents(). The model sends the collected events to
every registered VirtualView as a ModelUpdate. Each VirtualView converts the update to DTOs
and filters the available actions: only the active player receives non-empty actions, while
the other clients receive the same events with an empty action list.

The server sends the update as a DeltaEventMessage. On the client,
ClientNotificationController receives the delta, passes every event DTO to EventApplier, and
the applier updates the local GameModel. For a totem placement, this updates the totem
position on the offer track. Then the controller stores the new available actions and the
new active player. Finally, the model notifies the UI, and the game view renders the updated
offer track.
