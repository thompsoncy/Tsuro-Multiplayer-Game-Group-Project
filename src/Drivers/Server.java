import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Stack;

// represents a server that hosts a tsuro game
public class Server {
    public static void main(String[] args) throws Exception {
        String IPAddress = "127.0.0.1";
        int portNumber = 8000;
        if (args.length == 1) {
            portNumber = Integer.parseInt(args[0]);
        } else if (args.length == 2) {
            IPAddress = args[0];
            portNumber = Integer.parseInt(args[1]);
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("xserver.log"));
            writer.write("starting up server\n");
            writer.close();
            Referee referee = new Referee();
            referee.runGame(getPlayers(IPAddress,portNumber), new Stack<IObserver>(), null);
        } catch (Exception e) {
            System.out.println("oof that was not meant to happen, blame QA");
            throw e;
        }
    }

    // gets player connections
    //The minimum number of players is 3. After the first three players connect, the server should wait
    //for another 30 seconds for two more players.
    private static ArrayList<APlayer> getPlayers(String IPAddress, int portNumber) {
        try {
        ServerSocket serverSocket = new ServerSocket(portNumber, 6, InetAddress.getByName(IPAddress));
        ArrayList<APlayer> players = new ArrayList<>();
        LocalTime curtime  = java.time.LocalTime.now();
        serverSocket.setSoTimeout(10000);
        while(players.size() < 3 || (players.size() < 5 && curtime.isAfter(java.time.LocalTime.now().minusSeconds(30)))) {
            Socket clientSocket;
            try {
                clientSocket = serverSocket.accept();
            } catch(Exception e) {
                continue;
            }
            RemotePlayer newplayer = new RemotePlayer(clientSocket);
            if(!newplayer.setPlayerName()) {
               continue;
            }
            players.add(newplayer);
            if(players.size() == 3) {
                curtime  = java.time.LocalTime.now();
            }
        }
        return players;
        } catch (Exception e) {
            System.out.println(e);
            throw new IllegalStateException("Connection issues please check your connection");
        }
    }


}
