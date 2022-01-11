import org.json.JSONArray;

import java.util.*;

public class RefTests {
    public static void main(String args[]) {
        //Scanner sc = new Scanner("[\"jack\",\"alice\",\"bob\"]");
        Scanner sc = new Scanner(System.in);
        StringBuilder input = new StringBuilder();
        // outputting a JSON array of responses
        StringBuilder output = new StringBuilder();
        while (sc.hasNext()) {
            input.append(sc.next());
        }


        JSONArray jsonArrays = new JSONArray(input.toString());
        ArrayList<String> playersNames = new ArrayList<>();
        ArrayList<APlayer> playersObjects = new ArrayList<>();
        for (Object obj:jsonArrays) {
            playersNames.add(playersNames.size(), obj.toString());
            playersObjects.add(new DumbPlayer());
        }
        Referee ref = new Referee();
        Stack<IObserver> observers = new Stack<>();
        //observers.add(new LocalObserver());
        String gameResult = ref.runGame(playersObjects, observers, new Stack<Integer>());
        sc = new Scanner(gameResult);
        String losers = "],\"losers\":[";
        HashMap<Integer, Stack<String>> winningturns = new HashMap<>();
        for(int i = 0; i < playersNames.size(); i++) {
            sc.next();
            int nextup = sc.nextInt();
             if(nextup == -1){
                 losers = losers + "\"" + playersNames.remove(i) + "\",";
                 i--;
            } else {
                 if(!winningturns.containsKey(nextup)) {
                     winningturns.put(nextup, new Stack<>());
                 }
                 winningturns.get(nextup).add(playersNames.remove(i));
                 i--;
            }
        }
        Set<Integer> notsorted = winningturns.keySet();
        SortedSet<Integer> sortedSet = new TreeSet();
        sortedSet.addAll(notsorted);
        String winner = "{\"winners\":[";
        for (Integer num: sortedSet) {
            winner = winner + "[";
            for (String str: winningturns.get(num)) {
                winner = winner + "\"" + str +  "\",";
            }
            winner = winner.substring(0, winner.length() - 1) + "],";
        }
        winner = winner.substring(0, winner.length() - 1);
        losers = losers.substring(0, losers.length() - 1) + "]}";
        System.out.println(winner + losers);
    }
}
