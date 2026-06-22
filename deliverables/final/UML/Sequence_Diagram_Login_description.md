The diagram shows what happens when a player joins an existing game over Socket/TCP. The
client is already connected to the server. The player writes a nickname, selects a game, and
the view calls joinGame on the ServerProxy. The proxy sends a JoinGameMessage through
SocketServerConnection. On the server, SocketClientHandler receives the message and passes
it to the ConnectionSession.

ConnectionSession forwards the request to the current connection state. Since the player is
still in the lobby phase, the active state is LobbyConnectionState. This state asks
LobbyController to check the request. The controller validates the nickname, searches the
room using the game id, and reserves the nickname through GameManager. If one of these
checks fails, a LobbyActionException is thrown. The state catches it and sends an
ErrorMessage back to the client, where ClientNotificationController updates the lobby model
and shows the error in the current view.

If the request passes these checks, the nickname has been reserved but the player is not in
the room yet. LobbyConnectionState stores the nickname in the session and calls addPlayer on
the selected GameRoom. This can still fail if the room became full or if the game already
started before the player was added. In that case the server clears the nickname from the
session, releases it from GameManager, and sends the same error response to the client.

When the player is added successfully, GameRoom returns a RoomAdmissionResult. The server
first sends a MatchmakingSuccessMessage to the joining client. Only after that it runs
afterMatchmakingSuccess(), which broadcasts a RoomUpdateMessage to all players in the room.
This keeps the order clear: the player gets confirmation first, then everyone receives the
updated room roster.

If the room is now full, the same admission result starts the game. GameRoom creates a
VirtualView for each player, moves every session to InGameConnectionState, and registers the
virtual views as observers of the Game model. The model then starts and sends the first full
state update. Each VirtualView filters the available actions for its own player and sends a
FullSyncMessage to the client. The client resets its local game model, applies the received
board and player state, stores the initial totem positions, and renders the first game view.

