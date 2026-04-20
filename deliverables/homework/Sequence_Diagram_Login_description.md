This sequence diagram illustrates the initial connection and setup phase of the MVC architecture. 
When a Player enters their nickname, the View establishes a TCP connection with the GameServer, which spawns a dedicated ClientHandler thread for that connection. 
The VirtualServer then sends a logical LoginRequest over the network.

The Lobby acts as the session manager. It validates the login attempt. 
If successful and the required number of players is reached, the Lobby instantiates the core MVC components: the Game (Model), the GameController, and a VirtualView for each player. 
The Lobby registers each VirtualView as an observer of the Model using addObserver(). Finally, the game is started. 
The Model initializes its state and triggers notifyObservers() internally, pushing the first GameMemento to all registered views, which translates it into a GameSnapshotDTO sent to the clients to render the initial board.
