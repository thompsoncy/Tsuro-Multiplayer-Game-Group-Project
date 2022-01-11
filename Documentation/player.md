// Represents the all interactions concerning connections to players
## Player 
### Data
// represents a mapping from a unique color to current connections info which includes the info required to send and receive messages from the player 
#### HashMap(color, ConnectionInfo) colorConnectionMap
### Functions

// Informs the player indicated by given color of their given tile choices and requests a response within the given number of seconds 
#### Playeraction getPlayerAction(String color, List<String> tileChoices, int maxTime)

// updates all the connected players to given state of the board
#### Boolean updatePlayers(String boardState)

// awaits given number of  player connections within the given number of seconds returns false if given number of players did not connect within given max time otherwise returns true
#### Boolean receivePlayerConnections(int maxTime, int numberOfPlayers)
