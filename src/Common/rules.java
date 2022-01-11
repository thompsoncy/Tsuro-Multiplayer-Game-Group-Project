import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

//The rule checkers will be used by the game referee or the player . It will check on the legality of players actions
// given current player position,the position for tile placements, the rotation degree of the tile.
class RuleChecker {
    //The data represents the current board state
    private Board board;
    // mapping of all players info which is their unique color and location.
    private HashMap<String, BoardLocation> startingLocations;

    public RuleChecker(Board board, HashMap<String, BoardLocation> startingLocations){
        this.board = board;
        this.startingLocations = startingLocations;
    }

     //This checks if an initial placement is valid
     //An initial placement of an avatar on a specific port portNum and tile at a coordinate x, y on the
     //board is legal if the squares horizontally and vertically neighboring (x, y) are unoccupied
     //and the port is on a path that connects the edge of the board to an unoccupied square.
     //the tile placement may not cause the player’s suicide (as in exiting the board),
     //unless this is the only possible option, based on the supplied tiles.
    public boolean isLegalInitial(PlayerAction playerAction, List<Tile> tileChoices) {
        // making sure there has not been an initial placement for given player
        if(playerAction == null || startingLocations.containsKey(playerAction.getColor()) || !playerAction.getBoardLocation().isEdge() ||
                !tileChoices.contains(playerAction.getTile())) {
            return false;
        }
        // making sure the placement itself is valid
        if(board.isValidInitialPlaceTile(playerAction) && playerAction.getBoardLocation().isEdge()) {
            // making sure that if the placement leads to a game over all other placements also led to a game over
            if (board.endIfPlaceTileInitial(playerAction).isEdge()) {
                for (int newRotation = 0; newRotation < 4; newRotation++) {
                    for (Tile otherTile : tileChoices) {
                        if(!board.endIfPlaceTileInitial(new PlayerAction(playerAction.getColor(), otherTile, playerAction.getBoardLocation(), newRotation, true)).isEdge()) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

        //This checks if an non-initial placement is valid
        //An non-initial placement of an avatar on a tile at a coordinate x, y on the
        //board is legal if the x and y match the location where the given player can next place a tile
        //the tile placement may not cause the player’s suicide (as in exiting the board),
        //unless this is the only possible option, based on the supplied tiles which are given as other tiles.
    public boolean isLegal(PlayerAction playerAction, List<Tile> tileChoices) {
        // making sure there has been an initial placement for given player
        if(playerAction == null || !startingLocations.containsKey(playerAction.getColor()) || !tileChoices.contains(playerAction.getTile())) {
            return false;
        }
        BoardLocation curLoc = board.getEndFromInitial(startingLocations.get(playerAction.getColor()));
        Point newLoc = board.getNewXY(curLoc.getX(), curLoc.getY(), curLoc.getPortNum());
        // making sure given x and y were the correct x and y  for given player
        if(playerAction.getBoardLocation().getX() != newLoc.x || playerAction.getBoardLocation().getY() != newLoc.y) {
            return false;
        }
        PlayerAction testAction = new PlayerAction(playerAction.getColor(), playerAction.getTile(), curLoc, playerAction.getNumRot(), false);
        // making sure the placement itself is valid
        if(board.isValidPlaceTile(testAction)) {
            // making sure that if the placement leads to a game over all other placements also led to a game over
            if (board.endIfPlaceTile(testAction).isEdge()) {
                for (int newRotation = 0; newRotation < 4; newRotation++) {
                    for (Tile otherTile : tileChoices) {
                        if(!board.endIfPlaceTile(new PlayerAction(playerAction.getColor(), otherTile, curLoc, newRotation, false)).isEdge()) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }
}
