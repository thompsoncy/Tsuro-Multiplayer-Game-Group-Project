Tsuro’

Description

The purpose of this project is to implement a Tsuro-like board game. This game is for three to five players with unique avatars. An avatar is represented by a colored token. The game board is 10x10 grid of square holes into which players can place tiles. A tile is a square with 8 ports, 2 per side. And each tile has four distinct connections between two distinct ports. The winner is the last avatar on the game board. Each round, each player will place one tile from two randomly generated choices. They may rotate the tile before placement. The tiles construct paths and the avatar will go straight down to the end of the path, if it is the board's edge they lose.

Folder Structure

	Tsuro
	└── Documentation               # Documentation files 
	    ├────── README.md           # Project description
	    ├────── board.md            # 
	    ├────── observer.md         # 
	    ├────── player.md           # 
	    ├────── referee.md          # 
	    ├────── protocol.md         # 
	    ├────── rules.md            # 
	    └────── plan.md             # Project analysis
	
	
Phase 1 - https://github.ccs.neu.edu/cs4500-fall2019-neu/Just-Java/commit/4694aa166134b098c82179c6a21df724826f78cf
Phase 2 - https://github.ccs.neu.edu/cs4500-fall2019-neu/Just-Java/commit/50fdadcf65ca51bf2582db63c528a86a788ab159
Phase 3 - https://github.ccs.neu.edu/cs4500-fall2019-neu/Just-Java/commit/9507e841afbf0baf1d0184432cf01926461c383d
Phase 4 - https://github.ccs.neu.edu/cs4500-fall2019-neu/Just-Java/commit/5ed9dbf5b776c1637a2b8846bd57f9ee5d637f5a

