## Running xref -
Simply input a nonempty json array of player names that has a length less than 6. This test is not deterministic because the tiles that each player gets are random. Thus putting in the same input will result in different outputs.
#### Sample input 
["jack","alice","bob"] 



## Running xobs - 
Put in a non-empty json array of moves at least one initial move for each color. 
 The formats of inputs are as follows 

####  Initial move
[ [color, tile-index, rotation, x, y, port], tile-index, tile-index, tile-index ]
Example 
[["white",2,0,0,0,"A"],0,3,2]

#### Intermediant move
[ [color, tile-index, rotation, x, y], tile-index, tile-index ]  
Example
[["white",5,0,0,1],3,5]

One of the tile indexes in the outer array given must be equal to the tile index in the inner array this is because the chosen tile has to be one of the choices presented. 

#### Example complete input for one test 
[[["white",2,0,0,0,"A"],0,3,2],[["black",2,0,2,0,"A"],4,5,2],[["white",5,0,0,1],3,5],[["black",7,0,2,1],5,7]]]


The output which will be sent to a new file tsuro.txt will be all information given to the observer throughout the game.
 
