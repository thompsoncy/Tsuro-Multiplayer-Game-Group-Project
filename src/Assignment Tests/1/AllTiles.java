import java.awt.*;
import java.util.ArrayList;
import java.util.Stack;

//Class is used to get all unique Tiles by generation or by just grabbing the precomputed values
public class AllTiles {

    // generates from scratch all unique Tiles
    public static Tile[] generateAllTiles() {
        ArrayList<Tile> allTiles = new ArrayList<>();
        Stack<Integer> remainingNumbers = new Stack<>();
        for(int i = 0; i < 8; i++) {
            remainingNumbers.push(i);
        }
        Stack<int[]> allPortConnections = getAllPortConnections((Stack<Integer>)remainingNumbers);
        for (int[] portConnection: allPortConnections) {
            try {
                allTiles.add(new Tile(portConnection));
            } catch(IllegalArgumentException e){
                // invalid portconnection just don't add it
            }
        }
        // removing duplicate Tile who are just rotations of other tiles
        for(int i = 0; i < allTiles.size(); i++) {
            for(int b = i + 1; b < allTiles.size(); b++) {
                if(allTiles.get(i).equals(allTiles.get(b))) {
                    allTiles.remove(b);
                    b--;
                }
            }
        }
        return allTiles.toArray(new Tile[35]);
    }

    // returns all possible PortConnections including invalid ones with the remaining numbers
    private static Stack<int[]> getAllPortConnections(Stack<Integer> remainingNumbers) {
        Stack<int[]> allPortConnections = new Stack<>();
        if(remainingNumbers.isEmpty()) {
            allPortConnections.push(new int[8]);
            return allPortConnections;
        }
        for (int numOne = 0; numOne < remainingNumbers.size(); numOne++) {
            for (int numTwo = 1; numTwo < remainingNumbers.size(); numTwo++) {
                Stack<Integer> newRemainingNumbers = (Stack<Integer>)remainingNumbers.clone();
                newRemainingNumbers.remove(numOne);
                newRemainingNumbers.remove(numTwo - 1);
                Stack<int[]> newPortConnections = getAllPortConnections(newRemainingNumbers);
                for (int[] portConnection: newPortConnections) {
                    portConnection[remainingNumbers.get(numOne)] = remainingNumbers.get(numTwo);
                    portConnection[remainingNumbers.get(numTwo)] = remainingNumbers.get(numOne);
                }
                allPortConnections.addAll(newPortConnections);
            }
        }
        return allPortConnections;
    }
    //all unique Tiles from stored values in a constant order
    private static Tile[] allTiles = new Tile[]{
            new Tile(new int[]{3,5,6,0,7,1,2,4} ), new Tile(new int[]{4,5,6,7,0,1,2,3} ), new Tile(new int[]{3,6,5,0,7,2,1,4} ),
            new Tile(new int[]{4,7,6,5,0,3,2,1} ), new Tile(new int[]{1,0,3,2,5,4,7,6} ), new Tile(new int[]{1,0,6,7,5,4,2,3} ),
            new Tile(new int[]{1,0,6,5,7,3,2,4} ), new Tile(new int[]{5,4,7,6,1,0,3,2} ), new Tile(new int[]{4,5,7,6,0,1,3,2} ),
            new Tile(new int[]{3,6,7,0,5,4,1,2} ), new Tile(new int[]{4,6,7,5,0,3,1,2} ), new Tile(new int[]{1,0,7,6,5,4,3,2} ),
            new Tile(new int[]{2,3,0,1,6,7,4,5} ), new Tile(new int[]{2,3,0,1,5,4,7,6} ), new Tile(new int[]{2,4,0,6,1,7,3,5} ),
            new Tile(new int[]{2,4,0,5,1,3,7,6} ), new Tile(new int[]{2,5,0,7,6,1,4,3} ), new Tile(new int[]{2,5,0,6,7,1,3,4} ),
            new Tile(new int[]{2,6,0,7,5,4,1,3} ), new Tile(new int[]{2,6,0,5,7,3,1,4} ), new Tile(new int[]{2,7,0,6,5,4,3,1} ),
            new Tile(new int[]{2,7,0,5,6,3,4,1} ), new Tile(new int[]{3,2,1,0,7,6,5,4} ), new Tile(new int[]{3,2,1,0,6,7,4,5} ),
            new Tile(new int[]{3,2,1,0,5,4,7,6} ), new Tile(new int[]{4,2,1,7,0,6,5,3} ), new Tile(new int[]{4,2,1,6,0,7,3,5} ),
            new Tile(new int[]{4,2,1,5,0,3,7,6} ), new Tile(new int[]{5,2,1,7,6,0,4,3} ), new Tile(new int[]{5,2,1,6,7,0,3,4} ),
            new Tile(new int[]{6,2,1,7,5,4,0,3} ), new Tile(new int[]{6,2,1,5,7,3,0,4} ), new Tile(new int[]{7,2,1,6,5,4,3,0} ),
            new Tile(new int[]{7,2,1,5,6,3,4,0} ), new Tile(new int[]{7,2,1,4,3,6,5,0} )};

    // gets all unique Tiles from stored values
    public static Tile[] getAllTiles() {
        return allTiles;
    }

    // returns a list of locations of the ports in a x by x grid where x is the given number
    public static Point[] getPortPoints(int scale) {
        Point[] locations = new Point[8];
        locations[0] = new Point(9,3);
        locations[1] = new Point(6,0);
        locations[2] = new Point(3,0);
        locations[3] = new Point(0,3);
        locations[4] = new Point(0,6);
        locations[5] = new Point(3,9);
        locations[6] = new Point(6,9);
        locations[7] = new Point(9,6);
        for (Point p :locations) {
            p.x = p.x * scale / 10;
            p.y = p.y * scale / 10;
        }
        return locations;
    }
}
