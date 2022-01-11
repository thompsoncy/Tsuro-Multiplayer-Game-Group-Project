// A BoardLocation denotes a location on the Board.
public class BoardLocation {
    //How many tiles right of the left edge is this BoardLocation
    private int  x;
    //How many tiles down from the top edge is this BoardLocation
    private int y;
    //What port in the Tile is this location
    private int portNum;

    // basic constructor
    BoardLocation(int x, int y, int portNum) {
        if(x < 0 || x > 9 || y < 0 || y > 9 || portNum < 0 || portNum > 7) {
            throw new IllegalArgumentException("invalid board location");
        }
        this.x = x;
        this.y = y;
        this.portNum = portNum;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPortNum() {
        return portNum;
    }

    //returns true or false based on if the given BoardLocation is on the edge of the board
    public boolean isEdge() {
        return (x == 0 && (portNum == 3 || portNum == 4))
                || (x == 9 && (portNum == 0 || portNum == 7))
                || (y == 0 && (portNum == 1 || portNum == 2))
                || (y == 9 && (portNum == 5 || portNum == 6));
    }
}
