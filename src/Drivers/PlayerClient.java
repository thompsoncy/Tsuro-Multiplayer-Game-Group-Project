import org.json.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

// represents a remote player client, it requires a ip address of the server, the port number of the server, the name of
// the player, the player type or strategy the player wishes to use
public class PlayerClient {
    public static void main(String[] args) {
        if (args.length != 4) {
            throw new IllegalArgumentException("Please input an IP address (or hostname), a port, a player\n" +
                    "name, and a strategy");
        }
        String IPAddress = args[0];
        int portNumber = Integer.parseInt(args[1]);
        String username = args[2];
        String strategy = args[3];
        APlayer player;
        if(strategy.equalsIgnoreCase("dumb")) {
            player = new DumbPlayer();
        } else {
            player = new SecondPlayer();
        }
        try {
            Socket serverSocket = new Socket(IPAddress, portNumber);
            PrintWriter outToServer = new PrintWriter(serverSocket.getOutputStream(), true);
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            outToServer.println("[\"" + username + "\"]");
            PlayerClientDriver driver = new PlayerClientDriver();
            System.out.println(driver.playGame(player, inFromServer, outToServer));
        } catch (Exception e) {
            System.out.println("server is not acting correctly make sure your client and server are up to date");
        }
    }
}

