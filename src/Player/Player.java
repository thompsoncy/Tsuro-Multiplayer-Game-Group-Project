import org.json.JSONArray;

import java.awt.*;
import java.io.*;
import java.lang.management.PlatformLoggingMXBean;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

// this abstract class represents functionality required by all players
// this includes local automated players and remote players
// any player class represnts all interactions with one individual player
abstract class APlayer {
    // name for this player
    String playerName;
    // represents the color of this player
    String color;
    // represents a reference to the board of the game the player is in
    // The player should not mutate the board
    Board board;
    // represents the players starting location
    BoardLocation startingLocation;
    // represents a reference to the rulechecker of the game the player is in
    RuleChecker ruleChecker;

    // Informs the player of their given tile choices and requests a response within the given number of seconds
    abstract public PlayerAction getPlayerAction(List<Tile> tileChoices);

    // updates all the connected players to given state of the board
    abstract void updatePlayers(PlayerAction newAction, String boardState);


    public void setColor(String color) {
        this.color = color;
    }

    public void setStartingLocation(BoardLocation startingLocation) {
        this.startingLocation = startingLocation;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setRuleChecker(RuleChecker ruleChecker) {
        this.ruleChecker = ruleChecker;
    }

}

// a automated local player that uses a deterministic dumb strategy to pick its tiles
class DumbPlayer extends APlayer {

    @Override
    // decides an action based on board state
    public PlayerAction getPlayerAction(List<Tile> tileChoices) {
        //this means this is an initial placement
        if(tileChoices.size() == 3) {
            return getInitialPlacement(tileChoices.get(2), tileChoices);
        } else {
            //For an intermediate placement, it uses the first given tile without rotating it and places it on
            //the square adjacent to the player’s avatar. It does not check the legality of this action
            BoardLocation curlocation = board.getEndFromInitial(startingLocation);
            Point newxy = board.getNewXY(curlocation.getX(),curlocation.getY(), curlocation.getPortNum());
            BoardLocation newLocation = new BoardLocation(newxy.x,newxy.y,curlocation.getPortNum());
            return new PlayerAction(color, tileChoices.get(0), newLocation, 0, false);
        }
    }

    @Override
    void updatePlayers(PlayerAction newAction, String boardState) {

    }

    //uses the third given tile without rotation and searches for the first
    //legal spot available, clockwise, starting from (0, 0). To place the avatar, it searches, clockwise,
    //for the first port that will face an empty square of the board and is connected to the edge of
    //the board. The avatar should be placed on that port’s path (on the edge port or the port facing
    //the empty square – at the end of the turn the avatar should be considered as being on the port
    //facing the empty square).
    private PlayerAction getInitialPlacement(Tile choice, List tileChoices) {
        PlayerAction output = null;
        int[] validPorts = {1,2,3,4};
        output = LegalToPlace(choice,tileChoices, validPorts,0,0,0,0, false);
        if(output != null) {
            return output;
        }
        validPorts = new int[]{1,2};
        output = LegalToPlace(choice,tileChoices, validPorts,1,9,0,0, false);
        if(output != null) {
            return output;
        }
        validPorts = new int[]{7,0};
        output = LegalToPlace(choice,tileChoices, validPorts,9,9,0,9, false);
        if(output != null) {
            return output;
        }
        validPorts = new int[]{6,5};
        output = LegalToPlace(choice,tileChoices, validPorts,0,9,9,9, true);
        if(output != null) {
            return output;
        }
        validPorts = new int[]{4,3};
        output = LegalToPlace(choice,tileChoices, validPorts,0,0,0,9, false);
        if(output != null) {
            return output;
        }
        // no legal action just takes a illegal one
        return new PlayerAction(color, choice,new BoardLocation(0,0,0),0, true);
    }

    // returns the first legal action found within given range and within given ports
    private PlayerAction LegalToPlace(Tile choice, List tileChoices, int[] validPorts,
                                      int xmin, int xmax, int ymin, int ymax, boolean goBackwards) {
        LinkedList<PlayerAction> actions = new LinkedList<>();
        for(int x = xmin; x <= xmax; x++) {
            for(int y = ymin; y <= ymax; y++) {
                for (int port: validPorts) {
                    PlayerAction newAction = new PlayerAction(color, choice,new BoardLocation(x,y,port),0, true);
                    if(!goBackwards) {
                        actions.addLast(newAction);
                    } else {
                        actions.addFirst(newAction);
                    }
                }
            }
        }
        for (PlayerAction newAction:actions) {
            if(ruleChecker.isLegalInitial(newAction,tileChoices)) {
                return newAction;
            }
        }
        return null;
    }

}

// a automated local player that uses a deterministic not so dumb strategy to pick its tiles
class SecondPlayer extends APlayer {

    @Override
    // decides an action based on board state
    public PlayerAction getPlayerAction(List<Tile> tileChoices) {
        //this means this is an initial placement
        if(tileChoices.size() == 3) {
            return getInitialPlacement(tileChoices.get(2), tileChoices);
        } else {
            //When the player is asked to take its turn, it starts the search for a legal option with the second
            //tile type, trying all possible rotations starting from 0 degrees. If none of these possibilities
            //work out, it goes back to the first one and repeats the process. If no possible action is legal, it
            //chooses the second tile at 0 degrees.
            BoardLocation curlocation = board.getEndFromInitial(startingLocation);
            Point newxy = board.getNewXY(curlocation.getX(),curlocation.getY(), curlocation.getPortNum());
            BoardLocation newLocation = new BoardLocation(newxy.x,newxy.y,curlocation.getPortNum());
            PlayerAction newAction = new PlayerAction(color, tileChoices.get(0), newLocation, 0, false);
            for(int i = 0; i < 4; i++) {
                newAction.setNumRot(i);
                if(ruleChecker.isLegal(newAction,tileChoices)) {
                    return newAction;
                }
            }
            newAction = new PlayerAction(color, tileChoices.get(1), newLocation, 0, false);
            for(int i = 0; i < 4; i++) {
                newAction.setNumRot(i);
                if(ruleChecker.isLegal(newAction,tileChoices)) {
                    return newAction;
                }
            }
            return newAction;
        }
    }

    @Override
    void updatePlayers(PlayerAction newAction, String boardState) {

    }


    //When the player is asked to place an initial tile, it searches for the first legal spot available
    //in counter-clockwise direction starting from (0,0) [exclusive]. To place the avatar, the player
    //searches for the first legal port in counter-clockwise fashion that faces an empty square. The
    //player uses the third given tile, without rotating it.
    private PlayerAction getInitialPlacement(Tile choice, List tileChoices) {
        PlayerAction output = null;
        int[] validPorts = {1,2,3,4};
        output = LegalToPlace(choice,tileChoices, validPorts,0,0,0,0, true);
        if(output != null) {
            return output;
        }
        validPorts = new int[]{4,3};
        output = LegalToPlace(choice,tileChoices, validPorts,0,0,0,9, true);
        if(output != null) {
            return output;
        }
        validPorts = new int[]{6,5};
        output = LegalToPlace(choice,tileChoices, validPorts,0,9,9,9, false);
        if(output != null) {
            return output;
        }
        validPorts = new int[]{7,0};
        output = LegalToPlace(choice,tileChoices, validPorts,9,9,0,9, true);
        if(output != null) {
            return output;
        }
        validPorts = new int[]{1,2};
        output = LegalToPlace(choice,tileChoices, validPorts,1,9,0,0, true);
        if(output != null) {
            return output;
        }

        // no legal action just takes a illegal one
        return new PlayerAction(color, choice,new BoardLocation(0,0,0),0, true);
    }

    // returns the first legal action found within given range and within given ports
    private PlayerAction LegalToPlace(Tile choice, List tileChoices, int[] validPorts,
                                      int xmin, int xmax, int ymin, int ymax, boolean goBackwards) {
        LinkedList<PlayerAction> actions = new LinkedList<>();
        for(int x = xmin; x <= xmax; x++) {
            for(int y = ymin; y <= ymax; y++) {
                for (int port: validPorts) {
                    PlayerAction newAction = new PlayerAction(color, choice,new BoardLocation(x,y,port),0, true);
                    if(!goBackwards) {
                        actions.addLast(newAction);
                    } else {
                        actions.addFirst(newAction);
                    }
                }
            }
        }
        for (PlayerAction newAction:actions) {
            if(ruleChecker.isLegalInitial(newAction,tileChoices)) {
                return newAction;
            }
        }
        return null;
    }

}


// holds a list of actions that this player acts upon
class PreDestinedPlayer extends APlayer {

    private List<PlayerAction> playerActions;
    PreDestinedPlayer(List<PlayerAction> playerActions) {
        if(playerActions == null || playerActions.size() == 0) {
            throw new IllegalArgumentException("This class needs actions");
        }
        this.playerActions = playerActions;
    }

    @Override
    public PlayerAction getPlayerAction(List<Tile> tileChoices) {
        if (playerActions.size() == 1) {
            return playerActions.get(0);
        } else {
            return playerActions.remove(0);
        }
    }

    @Override
    void updatePlayers(PlayerAction newAction, String boardState) {

    }
}

// represents a remote player
class RemotePlayer extends APlayer {


    private PrintWriter out;
    private BufferedReader inFromUser;
    // initializes remote player with a reader and writer to the server
    RemotePlayer(Socket clientSocket) throws IOException {
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        inFromUser = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

    }

    // first informs the remote player of it's tile choices and then waits for a responding player action
    @Override
    public PlayerAction getPlayerAction(List<Tile> tileChoices) {
        JSONArray output = new JSONArray(new Object[tileChoices.size()]);
        for(int i = 0; i < tileChoices.size(); i++) {
            output.put(i, tileChoices.get(i).hashCode());
        }
        out.println(output.toString());
        writeToLog("server" + " >> " + color + "\n" + output.toString());
        String input = null;
        try {
            input = inFromUser.readLine();
        } catch (IOException e) {
            writeToLog("server" + " << " + color + "\n nothing expected player action");
            return null;
        }
        writeToLog("server" + " << " + color + "\n" + input);
        return new PlayerAction(new JSONArray(input));
    }

    // sends a update to the player with the latest valid action and a boardstate which is a server side rendering of the game
    @Override
    void updatePlayers(PlayerAction newAction, String boardState) {
        JSONArray output;
        if(newAction == null) {
            output = new JSONArray(new Object[1]);
            output.put(0,boardState);
        } else {
            output = new JSONArray(new Object[2]);
            output.put(0,newAction.getJsonArray());
            output.put(1,boardState);
        }
        writeToLog("server" + " >> " + color + "\n" + output.toString());
        out.println(output.toString());
    }

    // sets the color of this player
    @Override
    public void setColor(String color) {
        writeToLog("server" + " >> " + color + "\n" + "[\"" + color + "\"]");
        out.println("[\"" + color + "\"]");
        this.color = color;
    }

    // append the given interaction to the server log
    public void writeToLog(String str) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("xserver.log", true));
            writer.append(str + "\n");
            writer.close();
        } catch (IOException e) {
        }
    }



    // attepts to get the players username from user if fails return false
    public boolean setPlayerName() {
        try {
            this.playerName = inFromUser.readLine();
            writeToLog("server" + " <<  unknown color \n" + this.playerName);
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
