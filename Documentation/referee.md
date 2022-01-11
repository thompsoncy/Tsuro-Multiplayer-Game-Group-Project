// This class is the game driver, in other words actually runs the game of Tsuro from connecting to players to driving
each turn till the game ends 
## Referee
### Data 
// the current board state empty initially
#### Board board
// the current connected players along with player connections and info 
#### Player players
// the rule checker used to check if a player action is valid
#### Rulechecker ruleChecker

### Functions 
// Starts game which includes getting players, starting the game,  allowing for player interaction and enforcing rules on each turn and moving between turns automatically. 
Creates a new board and but does not disconnect from current connected players  returns the end status of the game
#### String runGame()
// disconnects all currently connected players and ends the game if one is running
#### Boolen  dropAllPlayers()
