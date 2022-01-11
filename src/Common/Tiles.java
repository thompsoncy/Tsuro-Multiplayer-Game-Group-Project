import java.awt.*;
import java.util.*;

// represents a Tile in the game Tsuro
// each tile is a square and has 8 ports 2 symmetrical placed on each side
// each port is a number between 0 and 7
// each port has a specific location and does not move
// the larger the port number the larger angle that denotes it's location
// ports angle and thus location range= between  number denoting port*45degrees + 45 and  number denoting port*45degrees
// example port 0 is between 45 degrees and 0 thus lies on the top half of the right side of the tile
// each port is connected to one other port within the tiles is represented by portConnections
class Tile {
    // represents all connections between ports
    // example to get which port is connected to port 3 simply do portConnections[3]
    // it is always true that portConnections[portConnections[int]] = int
    private int[] portConnections = new int[8];

    // number between 0 and 3 represents how many 90 degree counterclockwise rotations have occurred from this tile's
    // original configuration
    private int numRotations = 0;


    // creates a new tile with given portConnections
    // will check the validity of port connections and throw an illegal argument exception if
    // given portConnections is invalid
    Tile(int[] portConnections) {
        this.setPortConnections(portConnections);
    }

    // returns a copy of the current portConnections
    public int[] getPortConnections() {
        return portConnections.clone();
    }

    // creates a copy of given portConnection throws an invalidArgument if port connection is invalid
    // as defined above
    public void setPortConnections(int[] portConnections) {
        HashSet<Integer> duplicates = new HashSet<>();
        for (int portNum: portConnections) {
            duplicates.add(portNum);
            if(portConnections[portConnections[portNum]] != portNum || portConnections[portNum] == portNum) {
                throw new IllegalArgumentException("Invalid given port connections");
            }
        }
        if(duplicates.size() != 8) {
            throw new IllegalArgumentException("Invalid given port connections");
        }
        this.portConnections = portConnections.clone();
    }

    // gets the how many times this tile has been rotated 90 degrees counter clockwise
    //from its original position
    public int getNumRotations() {
        return numRotations;
    }

    //rotates this tile from it's current rotation to given rotation
    public void setNumRotations(int numRotations) {
        int differenceOfRotations = differenceOfRotations(this.numRotations, numRotations);
        this.portConnections = getRotatedPortConnection(this.portConnections, differenceOfRotations);
        this.numRotations = numRotations;
    }

    // returns the number of counter clockwise 90 degree rotations are required to get from the given
    // fromRotation to the toRotation
    private int differenceOfRotations(int fromRotation, int toRotation) {
        if(toRotation >= fromRotation) {
            return toRotation%4 - fromRotation;
        } else {
            return  4 - (fromRotation - toRotation%4);
        }
    }

    // returns version of given array of portConnections rotated counter clockwise by 90 
    // degrees given amount of times
    private int[] getRotatedPortConnection(int[] portConnections, int numRotations) {
        int[] newPortConnections = new int[8];
        for (int i = 0; i < 8; i++) {
            newPortConnections[(i + (numRotations * 2))%8] = (portConnections[i] + 2 * numRotations)%8;
        }
        return newPortConnections;
    }

    // returns false if given object is not a Tile
    // returns true if the port connections of the given Tile are equivalent to this Tile port connections
    // no matter the state of rotations 
    @Override
    public boolean equals(Object object) {
        if(!(object instanceof Tile)) {
            return false;
        }
        Tile other = (Tile) object;
        return Arrays.equals(this.portConnections, getRotatedPortConnection(other.portConnections, 0))
                || Arrays.equals(this.portConnections, getRotatedPortConnection(other.portConnections, 1))
                || Arrays.equals(this.portConnections, getRotatedPortConnection(other.portConnections, 2))
                || Arrays.equals(this.portConnections, getRotatedPortConnection(other.portConnections, 3));
    }

    // returns a int between 0 and 34 which corresponds to which unique tile this is
    @Override
    public int hashCode() {
        Tile[] uniqueTiles = AllTiles.getAllTiles();
        for(int i = 0; i < 34; i++) {
            if(uniqueTiles[i].equals(this)) {
                return i;
            }
        }
        return 34;
    }

    @Override
    // returns a string representation of this tiles in the form of a 2d grid ASCII art
    // each connection between ports is the smaller of the two ports numbers
    public String toString() {
        String[][] tileString = getTileStringArrays();
        StringBuilder output = new StringBuilder();
        // turns the tile map into a string 
        for (String[] arr: tileString) {
            for (String str:arr) {
                output.append(str);
            }
            output.append("\n");
        }
        return output.toString();
    }

    // returns a string representation of this tiles in the form of a 2d 10 by 10 arrays of strings
    // each connection between ports is the smaller of the two ports numbers
    public String[][] getTileStringArrays() {
        String[][] tileString = new String[10][10];

        //creates initial tiles
        for (String[] arr: tileString) {
            Arrays.fill(arr, " - ");
        }

        for(int x = 0; x < 10; x++) {
            for(int y = 0; y < 10; y++) {
                if(x == 0 || x == 9) {
                    tileString[y][x] = " | ";
                }
                if(y == 0 || y == 9) {
                    tileString[y][x] = "___";
                }
            }
        }

        boolean[] seenPort = new boolean[8];
        // gets points of the ports
        Point[] locations = AllTiles.getPortPoints(10);

        //goes through for each point pair connects them with a line that is made of the smaller
        // ports number
        for (int i = 0; i < 8; i++) {
            Point curlocation = locations[i];
            tileString[curlocation.y][curlocation.x] = " P ";
            if (!seenPort[i]) {
                seenPort[i] = true;
                seenPort[portConnections[i]] = true;
                Point destination = locations[portConnections[i]];

                while (!curlocation.equals(destination)) {
                    if (tileString[curlocation.y][curlocation.x].equals(" - ") ||
                            tileString[curlocation.y][curlocation.x].equals(" | ") ||
                            tileString[curlocation.y][curlocation.x].equals("___")) {
                        tileString[curlocation.y][curlocation.x] = " " + i + " ";
                    } else if (!tileString[curlocation.y][curlocation.x].equals(" P ")){
                        tileString[curlocation.y][curlocation.x] = " X ";
                    }
                    if (curlocation.x < destination.x) {
                        curlocation.x++;
                    } else if (curlocation.x > destination.x) {
                        curlocation.x--;
                    }
                    if (curlocation.y < destination.y) {
                        curlocation.y++;
                    } else if (curlocation.y > destination.y) {
                        curlocation.y--;
                    }
                }
                if (tileString[curlocation.y][curlocation.x].equals(" - ")) {
                    tileString[curlocation.y][curlocation.x] = " " + i + " ";
                } else {
                    tileString[curlocation.y][curlocation.x] = " X ";
                }
            }
        }
        return tileString;
    }
}