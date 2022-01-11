## xserver
The server takes two optional parameters: an IP address (or hostname) and a port. The
defaults for these parameters are 127.0.0.1 (localhost) and 8000. If only one parameter is given,
it is the port. The server listens on the specified hostname/port for player client connections.
The minimum number of players is 3. After the first three players connect, the server waits
for another 30 seconds for two more players. Once 3 to 5 players connect, the server refuses
any additional connection and should start-up a Tsuro game between a referee and the connected
players. Will output all interactions with all players to a file xserver.log as they happen.

example command line

./xserver
## xclient
The client takes four mandatory parameters: an IP address (or hostname), a port, a player
name, and a strategy which is either "dumb" or second. It connects to the game server at the given address and port,
it will not run and will throw an error if the server is not running.

example command line

./xclient 127.0.0.1 8000 ferd second
## xrun
To run xrun requires that both xclient and xserver are in the same directory as xrun when it is run
xruns reads a JSON array of player specificationsfrom standard input. The array has the following format:
[ { "name" : String, "strategy" : String}, ... ]
where the "name" field is the player’s name, and the case insensitive string in the "strategy"
field is the selected strategy for that player. The array will contain 3-5 such player specifications.
Inputs with less than 3 or more than 5 elements will cause the program to return an error.
Once input is processed, the program starts up the server and the corresponding number of
clients, locally, instructing the clients to connect to the server’s address and port. 
The game is run through Will output all interactions with all players to a file xserver.log as they happen.

example command line

./xrun 

Example standard input 

[ { "name" : "dumbguy1", "strategy" : "dumb"}, { "name" : "sec1", "strategy" : "second"}, { "name" : "sec2", "strategy" : "second"} ]
