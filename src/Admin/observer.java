// This class used to send the current graphical display of the game to a local or remote user
// This class is meant to be sent data at each step of the game by the referee
interface IObserver {
    // Sends a complete ascii art string representation of the board to the observing user,
    // this includes the board state the state of the game and the state of the players and their choices
    public void sendUpdate(String completeGame);

    // returns false if given number of observers did not connect within max time otherwise returns true
    public Boolean receiveObserverConnections();

}

// local observer that outputs all updates to system.out
class LocalObserver implements IObserver {

    @Override
    public void sendUpdate(String completeGame) {
        System.out.println(completeGame);
    }

    @Override
    public Boolean receiveObserverConnections() {
        return true;
    }
}

// stores the all game updates to be able to gotten later
class StoreObserver implements  IObserver {

    private StringBuilder allUpdates = new StringBuilder();

    @Override
    public void sendUpdate(String completeGame) {
        allUpdates.append("\n" + completeGame);
    }

    @Override
    public Boolean receiveObserverConnections() {
        return true;
    }

    // get full game string
    public String getFullGame() {
        return allUpdates.toString();
    }
}