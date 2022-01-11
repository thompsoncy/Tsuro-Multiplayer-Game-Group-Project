# Tsuro, Design Task

Note
The rule checker checks if the tile placements in all phases of the game are legal 
and prevents a player from putting the tiles wherever they want which is cheating. 
There are two function of rule checkers, the initial rule checker and during game rule checker.
For the initial rule checker, besides the requirement in during game rule checker, 
it also needs to make sure that none of the tiles are placed next to each other.
For the during game rule checker, here are several requirements:
1. The new tile placements cannot be on a position that existed a tile already. 
2. The new tile can only have 0, 90, 180 or 270 degrees’ rotation. 
3. The tile can only have following five colors: white, black, red, green or blue. 
4. The new tile can only be placed next to the one occupied by the player’s avatar. 

The rule checkers will be used by the game referee or the player . It will check on the legality of players actions given’ current  player position,the position for tile placements, the rotation degree of the tile.

## RuleChecker

The data represents the current board state and player info which is their color and location. 

### Board

List of all players info which is their unique color and location. 

### List playerInfo

The four allowed rotations for the tiles

### Array <Integer> [0, 90, 180, 270] degree

## Functions

Check if the given tile placement is legal initially. Five things need to be checked:
1. The new tile placements cannot be on a position that already has a tile. 
2. The new tile can only have 0, 90, 180 or 270 degrees’ rotation. 
3. The tile placement color must correspond to a valid player. 
4. The new tile can only be placed in the location determined by the players board location.
5. The new tile can only be placed if there is no neighboring tiles
If any of the above condition is not matched, the function returns false, otherwise true.


### ``` Boolean isLegalInitial(int x, int y, int rotation, string color) ```

Check if the given tile placement is legal. Four information need to be checked.
1. The new tile placements cannot be on a position that already has a tile. 
2. The new tile can only have 0, 90, 180 or 270 degrees’ rotation. 
3. The tile placement color must correspond to a valid player. 
4. The new tile can only be placed in the location determined by the players board location.
If any of the above condition is not matched, the function returns false, otherwise true.


### ``` Boolean isLegal(int x, int y, int rotation, string color) ```

