import org.json.JSONArray;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Stack;

// represents the Player client driver, who directly bridges the server and the player type or strategy
public class PlayerClientDriver {
    // the current board state empty initially
    private Board board;
    // the current player
    private APlayer player;
    // mapping from player colors to starting locations
    private HashMap<String, BoardLocation> startingLocations;

    // plays the game of Tsuro with a remote server through given communication lines with given player
    public String playGame(APlayer player,BufferedReader inFromServer,PrintWriter outToServer) throws IOException {
        this.board = new Board();
        startingLocations = new HashMap<>();
        this.player = player;
        this.player.setRuleChecker(new RuleChecker(board,startingLocations));
        this.player.setBoard(board);
        String receivedInput = inFromServer.readLine();
        JSONArray jsonArray = new JSONArray(receivedInput);
        this.player.setColor((String)jsonArray.get(0));
        // this loop is the actual game running
        while (true) {
            receivedInput = inFromServer.readLine();
            jsonArray = new JSONArray(receivedInput);
            if(jsonArray.length() == 1) {
                // server says game is over
                return jsonArray.get(0).toString();
            } else if(jsonArray.length() == 2 && jsonArray.get(1) instanceof String) {
                // server is updating player
                updateGameState(new PlayerAction(jsonArray.getJSONArray(0)), jsonArray.getString(1));
            } else {
                // server is requesting an action
                Stack<Tile> tileChoices = new Stack<>();
                for (Object tileIndex: jsonArray) {
                    tileChoices.add(AllTiles.getAllTiles()[(int)tileIndex]);
                }
                PlayerAction playerAction = player.getPlayerAction(tileChoices);
                if(playerAction.isIntial()) {
                    this.player.setStartingLocation(playerAction.getBoardLocation());
                }
                outToServer.println(playerAction.getJsonArray().toString());
            }
        }
    }

    // updates the game board with new action and informs the player of the changes
    private void updateGameState(PlayerAction newAction, String serverRendering) {
        player.updatePlayers(newAction, serverRendering);
        if(newAction.isIntial()) {
            board.placeTileInitial(newAction);
            startingLocations.put(newAction.getColor(), newAction.getBoardLocation());
        } else {
            BoardLocation currentLoc = board.getEndFromInitial(startingLocations.get(newAction.getColor()));
            board.placeTile(new PlayerAction(newAction.getColor(), newAction.getTile(), currentLoc, newAction.getNumRot(), false));
        }
    }
}
