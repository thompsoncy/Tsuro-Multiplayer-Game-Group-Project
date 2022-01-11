### Note
The player and the referees will interface with the board with getEnd function to get the players current location.
They can then give the player location a tile and a rotation to place the tile in the space next to the given player's 
current location with given rotation with PlaceTile function and then again get the players new location with getEnd. The referee will use 
this location along with isEdge to check if the location is on the edge of the board  and thus to see if the player
has lost at the end of this round. The board can be displayed in a visual manner at any time for all parties with the 
toString method.

___

A Board is a Tsuro board, it holds the currently placed tiles allows for new ones to be placed, and is able to return location where a path ends and  is able to tell if a location is an edge location. 
### Board 
### Data
The 2D matrix of tiled represents the board state. Matrix will empty at first.
### Tiles[10][10] tiles
### Functions
The Board will have basic getters and setters 
### Getters and Setters
Places the given  tile on the board based on x y as position and numRot and the rotation angle.
### void placeTile(Tile tile, int x, int y, int numRot)
Given a starting position and returns the port position of the end of the current path created by the tiles.
### BoardLocation getEnd(BoardLocation start)
returns true or false based on if the given BoardLocation is on the edge of the board 
### boolean isEdge(BoardLocation start)
Convert the board information into Ascii-art formatted string.
### String toString()  String

---

A BoardLocation denotes a location on the Board. It has no functions and all of its data is public
### BoardLocation
### Data-
How many tiles right of the left edge is this BoardLocation 
### int x 
How many tiles down from the top edge is this BoardLocation
### int y
What port in the Tile is this location
### int portNum

---



Each tile is a square and has 8 ports 2 symmetrical placed on each side.
Each port is a number between 0 and 7 and each port has a specific location and does not move. 
The larger the port number the larger angle that denotes it's location A ports angle and location range is between
number denoting port multiplied by  45degrees + 45 and  number denoting port multiplied by 45degrees. 
Example port 0 is between 45 degrees and 0 thus lies on the top half of the right side of the tile, 
each port is connected to one other port.
### Tile
### Data -
Represents all connections between ports, 
an example to get which port is connected to port 3 simply do portConnections[3].
It is always true that portConnections[portConnections[int]] = int
### int[] portConnections

Number between 0 and 3 represents how many 90 degree counterclockwise rotations have occurred from this tile's
original configuration
 ### int numRotations

### Functions -

should be able to get port connections rotations and  and set the port connections and guarantee valid data 
### Basic Setters and getters 

rotates this tile from it's current rotation to given rotation
###void setNumRotations(int numRotations)



returns false if given object is not a Tile, 
returns true if the port connections of the given Tile are equivalent to this Tile port connections
no matter the state of rotations 
### boolean equals(Object object)

returns a string representation of this tiles in the form of a 2d grid ASCII art
, each connection between ports is represented by a line of the smaller of the two ports numbers
public String toString()
