import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;

//A Board is a Tsuro board, it holds the currently placed tiles allows for new ones to be placed,
// and is able to return location where a path ends and is able to tell if a location is an edge location.
public class Board {
    //The 2D matrix of tiled represents the board state. Matrix will empty at first.
    private Tile[][] tiles = new Tile[10][10];

    // Places the given non initial tile at the tile connected to the current tile on the board
    // based on boardLocation which denotes the current location if the placement is valid
    // is valid if given non null input and the placement does not already have a tile at the location
    // and the current location is not an edge
    public void placeTile(PlayerAction playerAction) {
        if(!isValidPlaceTile(playerAction)) {
            throw new IllegalArgumentException("invalid placement");
        }
        Point newXY = getNewXY(playerAction.getBoardLocation().getX(), playerAction.getBoardLocation().getY(), playerAction.getBoardLocation().getPortNum());
        playerAction.getTile().setNumRotations(playerAction.getNumRot());
        tiles[newXY.x][newXY.y] = playerAction.getTile();
    }

    //Places the given initial tile at given location if valid
    // is valid if given non null input
    // and the placement has no neighbors and the placement does not already have a tile at the location
    public void placeTileInitial(PlayerAction playerAction) {
        if(!isValidInitialPlaceTile(playerAction)) {
            throw new IllegalArgumentException("invalid placement");
        }
        playerAction.getTile().setNumRotations(playerAction.getNumRot());
        tiles[playerAction.getBoardLocation().getX()][playerAction.getBoardLocation().getY()] = playerAction.getTile();
    }

    // returns the end of path if given tile is placed
    // does not mutate or edit anything
    public BoardLocation endIfPlaceTile(PlayerAction playerAction) {
        if(!isValidPlaceTile(playerAction)) {
            throw new IllegalArgumentException("invalid placement");
        }
        Point newXY = getNewXY(playerAction.getBoardLocation().getX(), playerAction.getBoardLocation().getY(), playerAction.getBoardLocation().getPortNum());
        int originalRot = playerAction.getTile().getNumRotations();
        playerAction.getTile().setNumRotations(playerAction.getNumRot());
        tiles[newXY.x][newXY.y] = playerAction.getTile();
        BoardLocation endLoc = getEnd(playerAction.getBoardLocation());
        tiles[newXY.x][newXY.y] = null;
        playerAction.getTile().setNumRotations(originalRot);
        return endLoc;
    }

    // returns the end of path if given tile is placed
    // does not mutate or edit anything
    public BoardLocation endIfPlaceTileInitial(PlayerAction playerAction) {
        if(!isValidInitialPlaceTile(playerAction)) {
            throw new IllegalArgumentException("invalid placement");
        }
        int originalRot = playerAction.getTile().getNumRotations();
        playerAction.getTile().setNumRotations(playerAction.getNumRot());
        tiles[playerAction.getBoardLocation().getX()][playerAction.getBoardLocation().getY()] = playerAction.getTile();
        BoardLocation endLoc = getEndFromInitial(playerAction.getBoardLocation());
        tiles[playerAction.getBoardLocation().getX()][playerAction.getBoardLocation().getY()] = null;
        playerAction.getTile().setNumRotations(originalRot);
        return endLoc;
    }

    // checks if this non initial tile placement is valid
    // is valid if given non null input and the placement does not already have a tile at the location
    // and the current location is not an edge
    public boolean isValidPlaceTile(PlayerAction playerAction) {
        if(playerAction.getBoardLocation() == null || playerAction.getTile() == null ||
                playerAction.getBoardLocation().isEdge() || playerAction.isIntial()) {
            return false;
        }
        Point newXY = getNewXY(playerAction.getBoardLocation().getX(), playerAction.getBoardLocation().getY(), playerAction.getBoardLocation().getPortNum());
        if(tiles[newXY.x][newXY.y] != null) {
            return false;
        }
        return true;
    }

    // checks if this initial tile placement is valid
    // is valid if given non null input
    // and the placement has no neighbors and the placement does not already have a tile at the location
    public boolean isValidInitialPlaceTile(PlayerAction playerAction) {
        if(playerAction.getBoardLocation() == null || playerAction.getTile() == null || !playerAction.isIntial()) {
            return false;
        }
        if(tiles[playerAction.getBoardLocation().getX()][playerAction.getBoardLocation().getY()] != null ||
                !hasNeighbors(playerAction.getBoardLocation().getX(), playerAction.getBoardLocation().getY())) {
            return false;
        }
        return true;
    }

    // checks if this tile location has tiles next to it
    private boolean hasNeighbors(int x, int y) {
        if((x != 0 && tiles[x - 1][y] != null) ||
                (x != 9 && tiles[x + 1][y] != null) ||
                (y != 0 && tiles[x][y - 1] != null) ||
                (y != 9 && tiles[x][y + 1] != null)) {
            return false;
        }
        return true;
    }

    //Given a starting position and returns the port position of the end of the current path created by the tiles.
    // start point must be a players initial location
    // returns given location if infinite loop
    public BoardLocation getEndFromInitial(BoardLocation start) {
        Tile currentTile = tiles[start.getX()][start.getY()];
        if(currentTile == null) {
            throw new IllegalArgumentException("invalid board location");
        }
        return getEndHelper(start.getX(), start.getY(), currentTile.getPortConnections()[start.getPortNum()], start);
    }

    //Given a starting position and returns the port position of the end of the current path created by the tiles.
    //the start location must not be a players initial position
    // returns given location if infinite loop
    public BoardLocation getEnd(BoardLocation start) {
        Tile currentTile = tiles[start.getX()][start.getY()];
        if(start.isEdge()  || currentTile == null) {
            throw new IllegalArgumentException("invalid board location");
        }
        return getEndHelper(start.getX(), start.getY(), start.getPortNum(), start);
    }

    // helper used to get End of a path
    // given the location of the port at then end of the tile path
    // assumes all given input is valid
    // will return start location if there was a loop
    private BoardLocation getEndHelper(int oldX, int oldY, int oldPortNumber, BoardLocation startLoc) {
        BoardLocation newLoc = new BoardLocation(oldX,oldY,oldPortNumber);
        if(newLoc.isEdge() || newLoc.equals(startLoc)) {
            return newLoc;
        }
        Point newXY = getNewXY(oldX,oldY,oldPortNumber);
        if(tiles[newXY.x][newXY.y] == null) {
            return newLoc;
        } else {
            return getEndHelper(newXY.x, newXY.y, tiles[newXY.x][newXY.y].getPortConnections()[getOppositePort(oldPortNumber)], startLoc);
        }
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    // returns the opposite port
    private int getOppositePort(int portNum) {
        return (portNum + 3 + (portNum % 2) * 2) % 8;
    }

    // gets the x y of the connected tile
    public Point getNewXY(int curX, int curY, int portNum) {
        if(portNum%7 == 0) {
            curX++;
        } else if (portNum < 3) {
            curY--;
        }  else if (portNum < 5) {
            curX--;
        }  else {
            curY++;
        }
        return new Point(curX, curY);
    }

    @Override
    // returns a string representation of this board in the form of a 2d grid ASCII art
    // each connection between ports is the smaller of the two ports numbers
    public String toString() {
        String[][] boardString = getBoardStringArrays();
        StringBuilder output = new StringBuilder();
        // turns the board map into a string
        for (String[] arr: boardString) {
            for (String str:arr) {
                output.append(str);
            }
            output.append("\n");
        }
        return output.toString();
    }

    // returns a string representation of this board in the form of a 2d 100 by 100 array of strings
    // each connection between ports is the smaller of the two ports numbers
    public String[][] getBoardStringArrays() {
        String[][] boardString = new String[100][100];
        for(int x = 0; x < 10; x++) {
            for(int y = 0; y < 10; y++) {
                String[][] tileString = null;
                if(tiles[x][y] != null) {
                    tileString = tiles[x][y].getTileStringArrays();
                }
                for(int tx = 0; tx < 10; tx++) {
                    for (int ty = 0; ty < 10; ty++) {
                        if(tileString == null) {
                            boardString[(x * 10) + ty][(y * 10) + tx] = "|||";
                        } else {
                            boardString[(x * 10) + ty][(y * 10) + tx] = tileString[tx][ty];
                        }
                    }
                }
            }
        }
        return boardString;
    }
}
