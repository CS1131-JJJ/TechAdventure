package game.map;

/**
 * @author Jacob Herrmann
 * 
 * An item that the player can pick up. 
 */
public record Item(String name, String description) {

    @Override
    public String toString() {
        return name;
    }
}
