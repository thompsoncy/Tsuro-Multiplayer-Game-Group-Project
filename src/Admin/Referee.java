import java.awt.*;
import java.util.*;
import java.util.List;

// This class is the game driver,
// in other words actually runs the game of Tsuro from connecting to players to driving each turn till the game ends
public class Referee {
    // colors unchanging ordering
    private static String[] colors = {"white", "black", "red", "green", "blue"};
    // the current board state empty initially
    private Board board;
    // the current connected players along with player connections and info
    private ArrayList<APlayer> players;
    // the current list of observers of which are sent data
    private List<IObserver> observers;
    // the rule checker used to check if a player action is valid
    private RuleChecker ruleChecker;
    // mapping from player colors to starting locations
    private HashMap<String, BoardLocation> startingLocations;
    // mapping from player colors to their death turn
    private HashMap<String, Integer> deathTurns;
    // current turn
    private int turnNum;
    // tiles to be given to the players
    private List<Integer> tilesToBeGiven;

    // takes in a list of the types of players this game will use such as automated or remote players and game observers
    // Starts and runs game which includes getting given players types connections if applicable, starting the game,
    // allowing for player interaction and enforcing rules on each turn and moving between turns automatically.
    // If a deterministic game is needed takes in a list of tile indexes that will be given in order to players
    String runGame(ArrayList<APlayer> newPlayers, List<IObserver> observers, List<Integer> tilesToBeGiven) throws IllegalArgumentException {
        // initialize data
        initializeData(newPlayers, observers, tilesToBeGiven);

        //takes initial moves
        initialMoves();

        // runs the intermediate part of the game
        while(!isGameOver()) {
            intermediateMoves();
            turnNum++;
        }


        return getEndGameState();
    }

    // initializes data for the game and connects to players
    private void initializeData(ArrayList<APlayer> newPlayers, List<IObserver> observers,  List<Integer> tilesToBeGiven) throws IllegalArgumentException {
        if(newPlayers == null || newPlayers.isEmpty()) {
            throw new IllegalArgumentException("Player types are Required");
        }
        this.tilesToBeGiven = tilesToBeGiven;
        this.observers = observers;
        this.board = new Board();
        this.startingLocations = new HashMap<>();
        this.deathTurns = new HashMap<>();
        this.turnNum = 1;
        players = newPlayers;
        this.ruleChecker = new RuleChecker(board, startingLocations);
        for(int i = 0; i < players.size(); i++) {
            players.get(i).setBoard(board);
            players.get(i).setRuleChecker(ruleChecker);
            players.get(i).setColor(colors[i]);
        }
        for (IObserver observer: this.observers) {
            if(!observer.receiveObserverConnections()) {
                throw new IllegalArgumentException("could not connect to observers");
            }
        }
    }

    // does the initial moves for all connected players
    private void initialMoves() {
        for(int i = 0; i < players.size(); i++) {
            Stack<Tile> choices = getTiles(3);
            Stack<Tile> copyChoices = new Stack<>();
            copyChoices.addAll(choices);
            PlayerAction newAction = players.get(i).getPlayerAction(copyChoices);
            updateAllObservers(colors[i], choices, newAction);
            // checks if move is legal if not kicks from game
            if(ruleChecker.isLegalInitial(newAction, choices)) {
                board.placeTileInitial(newAction);
                startingLocations.put(colors[i], newAction.getBoardLocation());
                updateDeathTurn(colors[i]);
                players.get(i).setStartingLocation(newAction.getBoardLocation());
                updateAllPlayers(newAction);
            } else {
                deathTurns.put(colors[i],turnNum);
                startingLocations.put(colors[i], null);
            }
        }
    }

    // does one set of the intermediate moves for all connected players
    private void intermediateMoves() {
        for(int i = 0; i < players.size(); i++) {
            BoardLocation startLoc = startingLocations.get(colors[i]);
            if(startLoc != null && !board.getEndFromInitial(startLoc).isEdge()) {
                Stack<Tile> choices = getTiles(2);
                Stack<Tile> copyChoices = new Stack<>();
                copyChoices.addAll(choices);
                PlayerAction newAction = players.get(i).getPlayerAction(copyChoices);
                updateAllObservers(colors[i], choices, newAction);
                // checks if move is legal if not kicks from game
                if (ruleChecker.isLegal(newAction, choices)) {
                    BoardLocation currentLoc = board.getEndFromInitial(startLoc);
                    board.placeTile(new PlayerAction(colors[i], newAction.getTile(), currentLoc, newAction.getNumRot(), false));
                    updateDeathTurn(colors[i]);
                    updateAllPlayers(newAction);
                } else {
                    deathTurns.put(colors[i],turnNum);
                    startingLocations.put(colors[i], null);
                } }
        }
    }


    // updates the death turn if this player died
    private void updateDeathTurn(String color) {
        if(board.getEndFromInitial(startingLocations.get(color)).isEdge()) {
            deathTurns.put(color,turnNum);
        }
    }

    private void updateAllPlayers(PlayerAction newAction) {
        String boardendering = drawBoardState(newAction.getColor());
        for (APlayer player:players) {
            player.updatePlayers(newAction,boardendering);
        }
    }

    // checks if the game has ended
    private boolean isGameOver() {
        Boolean onePersonAlive = false;
        for(int i = 0; i < players.size(); i++) {
            if(startingLocations.get(colors[i]) != null && !board.getEndFromInitial(startingLocations.get(colors[i])).isEdge()) {
                if(!onePersonAlive) {
                    onePersonAlive = true;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    //return given number of random tiles from AllTiles unless a determined list of tiles was given
    private Stack<Tile> getTiles(int numTiles) {
        Stack<Tile> tiles = new Stack<>();
        if(this.tilesToBeGiven != null && this.tilesToBeGiven.size() - numTiles >= 0) {
            for (int i = 0; i < numTiles; i++) {
                tiles.add(AllTiles.getAllTiles()[this.tilesToBeGiven.remove(0)]);
            }
        } else {
            Random rand = new Random();
            TreeSet<Integer> randomnumbers = new TreeSet<>();
            while(tiles.size() < numTiles) {
                int random = rand.nextInt(AllTiles.getAllTiles().length);
                if(!randomnumbers.contains(random)) {
                    tiles.add(AllTiles.getAllTiles()[random]);
                    randomnumbers.add(random);
                }
            }
        }
        return tiles;
    }



    //updates all observers with the current board state and player tile choices
    private void updateAllObservers(String currentPlayer, List<Tile> tiles, PlayerAction playerAction ) {
        StringBuilder output = new StringBuilder();
        output.append(drawBoardState(currentPlayer));
        output.append("\n" + currentPlayer + " tile choices were \n");
        for (Tile tile: tiles) {
            if(playerAction != null && tile.equals(playerAction.getTile())) {
                output.append("\n" + currentPlayer + " decided to place this tile at x = "
                        + playerAction.getBoardLocation().getX() + " y = " + playerAction.getBoardLocation().getY());
                if (tiles.size() == 3) {
                    output.append("\n" + currentPlayer + " is starting at port " + playerAction.getBoardLocation().getPortNum());
                }
                output.append("\n");

            } else {
                output.append("\n" + currentPlayer + " did not choose this tile\n");
            }
            output.append("\n" + tile.hashCode() + "\n");
            output.append(tile.toString());
            output.append("\n");
        }
        String finalOutput = output.toString();
        for (IObserver observer: this.observers) {
            observer.sendUpdate(finalOutput);
        }

    }

    // draws the current board in ASSCI art
    private String drawBoardState(String currentPlayer) {
        String[][] boardStrings = board.getBoardStringArrays();
        List<String> cheaters = new Stack<>();
        for(int i = 0; i < players.size(); i++) {
            if(startingLocations.get(colors[i]) != null) {
                BoardLocation currentLoc = board.getEndFromInitial(startingLocations.get(colors[i]));
                Point tileLocation = AllTiles.getPortPoints(10)[currentLoc.getPortNum()];
                boardStrings[10 * currentLoc.getX() + tileLocation.x][10 * currentLoc.getY() + tileLocation.y] =
                        colors[i].substring(0,3).toUpperCase();
            } else {
                cheaters.add(colors[i]);
            }
        }
        StringBuilder output = new StringBuilder();
        // turns the board map into a string
        for(int y = 0; y < 100; y++) {
            for(int x = 0; x < 100; x++) {
                output.append(boardStrings[x][y]);
            }
            output.append("\n");
        }
        output.append("round is " + turnNum + " , it is " + currentPlayer + " turn.\n");
        output.append("The following players have been removed for cheating or have not placed a tile yet");
        for (String cheater:cheaters) {
            output.append(", " + cheater);
        }
        return output.toString();
    }

    // gets the final string state of the game and updates all connected players and observers
    private String getEndGameState() {
        StringBuilder output = new StringBuilder();
        output.append(drawBoardState("no one's"));
        output.append("\n");
        StringBuilder endStateOutput = new StringBuilder();
        // -1 equals cheated
        for(int i = 0; i < players.size(); i++) {
            if(deathTurns.get(colors[i]) == null) {
                endStateOutput.append(colors[i] + " 10000000");
            } else {
                if(startingLocations.get(colors[i]) == null) {
                    endStateOutput.append(colors[i] + " " + -1);
                } else {
                    endStateOutput.append(colors[i] + " " + deathTurns.get(colors[i]));
                }
            }
            endStateOutput.append("\n");
        }
        String finalUpdate = "The turns when each player died \n" + endStateOutput.toString() + "\n" + output.toString();
        for (IObserver observer: this.observers) {
            observer.sendUpdate(finalUpdate);
        }
        for (APlayer player: players) {
            player.updatePlayers(null, endStateOutput.toString());
        }
        return endStateOutput.toString();
    }
}
