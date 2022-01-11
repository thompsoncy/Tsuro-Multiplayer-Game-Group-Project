
		   1.Server Accepts Connections
                         Server                            player client                         player

                             |                                  |         Start the Client         |
                             |        Connect To Socket         |<---------------------------------|
                             |<---------------------------------|                                  | playing as: color
     Connection Confirmation |                                  |                                  | Players Start client and 
     sends as soon as        |                                  |       Send [player_name]         | informs the client of their name
     there is a connection   |   Send JSON input to the server  |<---------------------------------| client connects to server
                             |<---------------------------------|                                  | and informs server of player name
                             |                                  |                                  |
                             |   Send [player_color]            |                                  | The server send the players color
                             |--------------------------------->|                                  | soon as there the game starts
                             |                                  | Show response: The player color  |
                             |                                  |--------------------------------->| 
                             |                                  |                                  | 
                             |                                  |                                  | 


		2.Initial Turns Interaction 
                          Server                           player client                      player

                             |Send [tile-index, tile-index       |                                  |
			         |           ,tile-index]            |                                  |
                             |---------------------------------->|       Show Tile Choices          | 
                             |                                   |--------------------------------->| server sends possible intial
                             |                                   |                                  | move tiles and waits for a 
                             |                                   | Send Message [color, tile-index, | return message indicating 
                             |                                   |      rotation, x, y, port]       | chosen move
                             | Send JSON input to the server     |<---------------------------------|
                             |<----------------------------------|                                  |    
     Request Action Initial  |                                   |                                  | 
     send as soon as it is   |                                   |                                  | 
     the players turn        |                                   |                                  | 

		3.Intermediant Turns Interaction 
                          Server                           player client                      player


                             |Send [tile-index, tile-index]      |                                  |
                             |---------------------------------->|        Show Tile Choices         | 
                             |                                   |--------------------------------->|
                             |                                   |                                  | server sends possible Intermediant
                             |                                   | Send Message [color, tile-index, | move tiles and waits for a 
                             |                                   |        rotation, x, y]           | return message indicating 
                             |   Send JSON input to the server   |<---------------------------------| chosen move 
                             |<----------------------------------|                                  |
              Request Action |                                   |                                  |
        Intermediant send as |                                   |                                  |
          soon as it is the  |                                   |                                  |
             players turn    |                                   |                                  | 



		4.Game Update Initial Turns Interaction 
                          Server                            player client                      player

                             |Send [ [color, tile-index, rotation,|                                  |
                             |   x, y, port], server-rendering]   |                                  |
                             |----------------------------------->|                                  | Server informs playerClient of
         Game Update Initial |                                    |      Show board change           | changes to the board in the form 
    turn's send at the end of|                                    |--------------------------------->| of new valid Initial move and a 
           each turn for the |                                    |                                  | visual rendering of the board
               initial phase |                                    |                                  |      
                             |                                    |                                  | 
                             |                                    |                                  |
                             |                                    |                                  | 



		5.Game Update Intermediate Turns Interaction 
                          Server                            player client                      player

                             | Send [[color, tile-index, rotation,|                                  |
                             |      x, y], server-rendering]      |                                  |                           
    Game Update Intermediant |----------------------------------->|                                  | Server informs playerClient of 
     send at the end of each |                                    |      Show board change           | changes to the board in the form
                turn for the |                                    |--------------------------------->| of new valid Intermediate move
          intermediate phase |                                    |                                  | and a visual rendering of the 
                             |                                    |                                  | board
                             |                                    |                                  |
                             |                                    |                                  |
                             |                                    |                                  |
                             |                                    |                                  |


		     6.Game Over 
                          Server                            player client                      player

                             |      Send [game-over-message]      |                                  |
                             |----------------------------------->|                                  | server sends a string that
             Game End  sends |                                    |     Show game over message       | contains information of the      
       when the game is over |                                    |--------------------------------->| end state of every player
                             |                                    |                                  |
                             |        disconnect from Socket      |                                  |
                             |----------------------------------->|                                  |
                             |                                    |                                  |
                             |                                    |                                  |
                             |                                    |                                  |

      Example game with 3 players
      1. 3 times once for each player
      2. 3 times once for each players
      4. n number of times where n is the number of valid moves given in the last stage
      
      loops until game is over
        3. n number of times where n is the number of remaining players
        5. n number of times where n is the number of valid moves given in the last stage
      
      6. 3 times once for each player
