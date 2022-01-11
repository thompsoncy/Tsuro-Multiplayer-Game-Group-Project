
import org.json.JSONArray;

import java.util.*;

public class XRules {

    //Mapping from string ports to int ports representation
    private ArrayList<String> portList;
    //The info of board
    private Board board;
    //The rule checker for input
    private RuleChecker rule;
    //THe start location of each player
    private HashMap<String, BoardLocation> initialLocations = new HashMap<>();

    //Initialization
    private XRules() {
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
        XRules xRules = new XRules();
        Scanner sc = new Scanner(System.in);
        StringBuilder input = new StringBuilder();

        while (sc.hasNext()) {
            input.append(sc.next());
        }
        try {
            JSONArray jsonArrays = new JSONArray(input.toString());
            // Do not parse the last json array
            while (index < jsonArrays.length() - 1) {
                JSONArray jsonArray = jsonArrays.getJSONArray(index);
                if (jsonArray.length() == 6) {
                    int tileIndex = jsonArray.getInt(0);
                    int degree = jsonArray.getInt(1);
                    int rotation = xRules.translateRotFromDegree(degree);
                    String color = jsonArray.getString(2);
                    if (!xRules.checkColor(color)) {
                        throw new IllegalArgumentException("Invalid input");
                    }
                    String port = jsonArray.getString(3);
                    int port2 = xRules.portList.indexOf(port);
                    int x = jsonArray.getInt(4);
                    int y = jsonArray.getInt(5);
                    Tile tile = AllTiles.getAllTiles()[tileIndex];
                    BoardLocation location = new BoardLocation(x, y, port2);
                    PlayerAction action = new PlayerAction(tile,location,
                            rotation);
                    xRules.board.placeTileInitial(action);
                    xRules.initialLocations.put(color, location);

                    tile.setNumRotations(rotation);
                }
                if (jsonArray.length() == 5) {
                    String color = jsonArray.getString(0);
                    int tileIndex = jsonArray.getInt(1);
                    int degree = jsonArray.getInt(2);
                    int rotation = xRules.translateRotFromDegree(degree);

//                    int x = jsonArray.getInt(3);
//                    int y = jsonArray.getInt(4);
                    Tile tile = AllTiles.getAllTiles()[tileIndex];
                    BoardLocation location =
                            xRules.board.getEndFromInitial(xRules.initialLocations.get(color));
                    PlayerAction action = new PlayerAction(tile,location,
                            rotation);

                    xRules.board.placeTile(action);
                }
                index++;
            }

            if(xRules.parseTurnJson(jsonArrays.getJSONArray(jsonArrays.length() - 1))){
                System.out.print("legal\n");
            }
            else {
                System.out.print("illegal\n");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        //xRules.out();
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
        System.out.print(output.toString());
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
