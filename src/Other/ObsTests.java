
import org.json.JSONArray;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;

public class ObsTests {

    //Mapping from string ports to int ports representation
    private ArrayList<String> portList;
    //The info of board
    private Board board;
    //The rule checker for input
    private RuleChecker rule;
    //THe start location of each player
    private HashMap<String, BoardLocation> initialLocations = new HashMap<>();

    //Initialization
    private ObsTests() {
        this.board = new Board();
        this.portList = new ArrayList<>();
        portList.add("C");
        portList.add("B");
        portList.add("A");
        portList.add("H");
        portList.add("G");
        portList.add("F");
        portList.add("E");
        portList.add("D");
        rule = new RuleChecker(board,initialLocations);
    }


    public static void main(String args[]) {

        int index = 0;
        ObsTests ObsTests = new ObsTests();
        // [[["white",2,0,0,0,"A"],0,3,2],[["black",2,0,2,0,"A"],4,5,2],[["white",5,0,0,1],3,5],[["black",7,0,2,1],5,7]]]
        // [[["blue",20,0,8,0,"B"],0,7,20], [["green",7,0,6,0,"B"],0,7,2], [["red",4,0,4,0,"A"],0,4,2],[["white",2,0,0,0,"A"],0,3,2],[["black",2,0,2,0,"B"],4,5,2],[["white",5,0,0,1],3,5],[["black",7,0,2,1],5,7], [["white",20,0,0,2],3,20], [["black",2,0,1,1],5,2]]]
        Scanner sc = new Scanner(System.in);
        StringBuilder input = new StringBuilder();

        while (sc.hasNext()) {
            input.append(sc.next());
        }
        try {
            HashMap<String, List<PlayerAction>> playerAction = new HashMap<>();
            HashMap<String, List<List<Integer>>> playerTiles = new HashMap<>();
            JSONArray jsonArrays = new JSONArray(input.toString());
            // Do not parse the last json array
            while (index < jsonArrays.length()) {

                JSONArray turnJson = jsonArrays.getJSONArray(index);
                JSONArray jsonArray = turnJson.getJSONArray(0);
                if (jsonArray.length() == 6) {
                    int tileIndex = jsonArray.getInt(1);
                    int degree = jsonArray.getInt(2);
                    int rotation = ObsTests.translateRotFromDegree(degree);
                    String color = jsonArray.getString(0);
                    if (!ObsTests.checkColor(color)) {
                        throw new IllegalArgumentException("Invalid input");
                    }
                    String port = jsonArray.getString(5);
                    int port2 = ObsTests.portList.indexOf(port);
                    int x = jsonArray.getInt(3);
                    int y = jsonArray.getInt(4);
                    Tile tile = AllTiles.getAllTiles()[tileIndex];
                    BoardLocation location = new BoardLocation(x, y, port2);
                    PlayerAction action = new PlayerAction(tile,location,
                            rotation);
                    if(!playerAction.containsKey(color)) {
                      playerAction.put(color,new Stack<>());
                    }
                    playerAction.get(color).add(action);
                    if(!playerTiles.containsKey(color)) {
                        playerTiles.put(color,new Stack<>());
                    }
                    int ib = playerTiles.get(color).size();
                    playerTiles.get(color).add(new Stack<>());
                    playerTiles.get(color).get(ib).add(turnJson.getInt(1));
                    playerTiles.get(color).get(ib).add(turnJson.getInt(2));
                    playerTiles.get(color).get(ib).add(turnJson.getInt(3));
                }
                if (jsonArray.length() == 5) {
                    int tileIndex = jsonArray.getInt(1);
                    int degree = jsonArray.getInt(2);
                    int rotation = ObsTests.translateRotFromDegree(degree);
                    String color = jsonArray.getString(0);
                    if (!ObsTests.checkColor(color)) {
                        throw new IllegalArgumentException("Invalid input");
                    }
                    int x = jsonArray.getInt(3);
                    int y = jsonArray.getInt(4);
                    Tile tile = AllTiles.getAllTiles()[tileIndex];
                    BoardLocation location = new BoardLocation(x, y, 0);
                    PlayerAction action = new PlayerAction(tile,location,
                            rotation);
                    if(!playerAction.containsKey(color)) {
                        playerAction.put(color,new Stack<>());
                    }
                    playerAction.get(color).add(action);
                    if(!playerTiles.containsKey(color)) {
                        playerTiles.put(color,new Stack<>());
                    }
                    int ib = playerTiles.get(color).size();
                    playerTiles.get(color).add(new Stack<>());
                    playerTiles.get(color).get(ib).add(turnJson.getInt(1));
                    playerTiles.get(color).get(ib).add(turnJson.getInt(2));
                }
                index++;
            }



            List<Integer> tiles = new Stack<>();
            String[] colors = {"white", "black", "red", "green", "blue"};
            ArrayList<APlayer> players = new ArrayList<>();

            boolean keepgoing = true;
            int count = 0;
            boolean[] seen = new boolean[colors.length];
            while(keepgoing) {
                keepgoing = false;
                for (int i = 0; i < colors.length; i++) {
                    if(playerAction.containsKey(colors[i])) {
                        if (playerTiles.get(colors[i]).size() > count) {
                            keepgoing = true;
                            tiles.addAll(playerTiles.get(colors[i]).get(count));
                        } else {
                            if(!seen[i]) {
                                tiles.add(0);
                                tiles.add(0);
                                seen[i] = true;
                            }
                        }
                    }
                }
                count++;
            }

            for (int i = 0; i < colors.length; i++) {
                if(playerAction.containsKey(colors[i])) {
                    PreDestinedPlayer player = new PreDestinedPlayer(playerAction.get(colors[i]));
                    players.add(players.size(), player);
                }
            }

            Referee ref = new Referee();
            List<IObserver> obs = new Stack<>();
            StoreObserver storeObserver = new StoreObserver();
            obs.add(storeObserver);
            ref.runGame(players,obs,tiles);
            BufferedWriter writer = new BufferedWriter(new FileWriter("tsuro.txt"));
            writer.write(storeObserver.getFullGame());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //ObsTests.out();
    }

    private int translateRotFromDegree(int degree) {
        int rotation = 0;
        if (degree == 90) {
            rotation = 3;
        } else if (degree == 180) {
            rotation = 2;
        } else if (degree == 270) {
            rotation = 1;
        }
        return rotation;
    }

    private boolean parseTurnJson(JSONArray turnJson) {
        JSONArray input = turnJson.getJSONArray(0);
        String color = input.getString(0);
        int tilePicked = input.getInt(1);
        int degree = input.getInt(2);
        int rotation = this.translateRotFromDegree(degree);
        int x = input.getInt(3);
        int y = input.getInt(4);

        int tileGet1 = turnJson.getInt(1);
        int tileGet2 = turnJson.getInt(2);

        List<Tile> otherChoices = new ArrayList<>();
        otherChoices.add(AllTiles.getAllTiles()[tileGet1]);
        otherChoices.add(AllTiles.getAllTiles()[tileGet2]);

        // The pick up tile
        Tile tile = AllTiles.getAllTiles()[tilePicked];
        BoardLocation location = new BoardLocation(x,y,0);

        PlayerAction action = new PlayerAction(tile,location,rotation);
        return rule.isLegal(action,color,otherChoices);
    }

    private boolean checkColor(String color) {
        return color.equals("white")
                || color.equals("blue")
                || color.equals("green")
                || color.equals("black")
                || color.equals("red");
    }

    private void out() {
        StringBuilder output = new StringBuilder();
        output.append("[");
        String[] colors = {"white", "black", "red", "green", "blue" };
        for(String color : colors) {
            BoardLocation initialLocation = this.initialLocations.get(color);
            if(initialLocation == null) {
                output.append(String.format("[\"%s\", \" never played\"]",
                        color));
            }

            else {
                HashMap<Integer, Integer> rot = new HashMap<>();
                rot.put(1,3);
                rot.put(0,0);
                rot.put(3,1);
                rot.put(2,2);
                BoardLocation endLocation =
                        this.board.getEndFromInitial(initialLocation);
                if (this.checkCollision(color)) {
                    output.append(String.format("[\"%s\", \" collided\"]",
                            color));
                } else if (endLocation.isEdge()) {
                    output.append(String.format("[\"%s\", \" exited\"]",color));
                } else {
                    Tile currentTile =
                            this.board.getTiles()[endLocation.getX()][endLocation.getY()];
                    output.append(String.format("[\"%s\", %d, " +
                                    "%d, \"%s\", %d, %s]", color,
                            currentTile.hashCode(),
                            rot.get(currentTile.getNumRotations()) * 90,
                            portList.get(endLocation.getPortNum()), endLocation.getX(),
                            endLocation.getY()));
                }
            }
            output.append(",");
        }
        output.deleteCharAt(output.length()-1);
        output.append("]");
        output.append("\n");
    }

    //If the color collides with other color
    private boolean checkCollision(String color) {
        BoardLocation thisColorStartAt = this.initialLocations.get(color);
        BoardLocation thisColorEndAt =
                this.board.getEndFromInitial(thisColorStartAt);
        String[] colors = {"white", "black", "red", "green", "blue" };
        for(String otherColor : colors) {
            if(!color.equals(otherColor) && this.initialLocations.containsKey(otherColor)) {
                BoardLocation otherColorStartAt =
                        this.initialLocations.get(otherColor);
                if(thisColorEndAt.getX() == otherColorStartAt.getX()
                        && thisColorEndAt.getY() == otherColorStartAt.getY()
                        && thisColorEndAt.getPortNum() == otherColorStartAt.getPortNum()) {
                    return true;
                }
            }
        }
        return false;
    }

}
