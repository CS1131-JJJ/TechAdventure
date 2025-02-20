package game.context;
import java.util.List;

import game.io.OutputWriter;
import game.map.Room;
import game.player.Player;

/**
 * @author Jacob Herrmann
 * @author Jacob Charmley
 * @author Julie Truckenbrod
 * @author Braden McKenzie
 * 
 * The current context of the game. 
 */
public class GameContext {
    private final Room[] rooms;
    private final Player player;
    private final OutputWriter writer;
    private final GameContextLoader loader;
    private String[] commandNames;

    private boolean gameIsOver = false;
    private boolean hasWon = false;

    public GameContext(Room[] rooms, Player player, OutputWriter writer, GameContextLoader loader) {
        this.player = player;
        this.rooms = rooms;
        this.writer = writer;
        this.loader = loader;
    }

    public Player getPlayer() {
        return player;
    }

    public List<Room> getRooms() {
        return List.of(rooms);
    }

    public OutputWriter getOutputWriter() {
        return writer;
    }

    public GameContextLoader getLoader() {
        return loader;
    }

    public void endGame() {
        gameIsOver = true;
    }

    public void win() {
        gameIsOver = true;
        hasWon = true;
    }

    public boolean isGameOver() {
        return gameIsOver;
    }

    public boolean hasWon() {
        return hasWon;
    }

    public String[] getAllCommands() {
        return commandNames;
    }

    public void setAllCommands(String[] arr) {
        commandNames = arr;
    }
    
}
