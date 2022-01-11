import org.json.JSONArray;
// A PlayerAction denotes a tile placement attempt by a PLayer
public class PlayerAction {
    // The Tile that the Player wants to place onto the Board
    private Tile tile;
    // The BoardLocation where the Player attempts in placing the tile
    private BoardLocation boardLocation;
    // How many time(s) the Player wishes to rotate the Tile before placing it on the Board
    private int numRot;
    // is this player action an initial move
    private boolean isIntial;
    // color of player taking action
    private String color;

    // Basic constructor
    PlayerAction(String color, Tile tile, BoardLocation boardLocation, int numRot, boolean isIntial) {
        this.color = color;
        this.tile = tile;
        this.boardLocation = boardLocation;
        this.numRot = numRot;
        this.isIntial = isIntial;
    }

    // creates a player action from a JsonArray of the following two forms
    //  is Initial- [color, tile-index, rotation, x, y, port]
    //  is not Initial-  [color, tile-index, rotation, x, y]
    PlayerAction(JSONArray jsonArray) {
        this.color = jsonArray.getString(0);
        this.tile = AllTiles.getAllTiles()[jsonArray.getInt(1)];
        this.numRot = jsonArray.getInt(2);
        if(jsonArray.length() == 6) {
            this.isIntial = true;
            this.boardLocation = new BoardLocation(jsonArray.getInt(3), jsonArray.getInt(4), jsonArray.getInt(5));
        } else {
            this.isIntial = false;
            this.boardLocation = new BoardLocation(jsonArray.getInt(3), jsonArray.getInt(4), 0);
        }
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public void setBoardLocation(BoardLocation boardLocation) {
        this.boardLocation = boardLocation;
    }

    public void setNumRot(int numRot) {
        this.numRot = numRot;
    }

    public void setIntial(boolean intial) {
        isIntial = intial;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Tile getTile() {
        return tile;
    }

    public BoardLocation getBoardLocation() {
        return boardLocation;
    }

    public int getNumRot() {
        return numRot;
    }

    public boolean isIntial() {
        return isIntial;
    }
    public String getColor() {
        return color;
    }

    //  creates a JsonArray of the following two forms
    //  is Initial- [color, tile-index, rotation, x, y, port]
    //  is not Initial-  [color, tile-index, rotation, x, y]
    public JSONArray getJsonArray() {
        JSONArray jsonArray;
        if(isIntial) {
            jsonArray = new JSONArray(new Object[6]);
            jsonArray.put(5,boardLocation.getPortNum());
        } else {
            jsonArray = new JSONArray(new Object[5]);
        }
        jsonArray.put(0,color);
        jsonArray.put(1,tile.hashCode());
        jsonArray.put(2,numRot);
        jsonArray.put(3,boardLocation.getX());
        jsonArray.put(4,boardLocation.getY());
        return jsonArray;
    }
}
