package game.map;

/**
 * @author Jacob Herrmann
 * @author Jacob Charmley
 * @author Julie Truckenbrod
 * @author Braden McKenzie
 * 
 * An item that the player can pick up. 
 */
public record Item(String name, String description) {

    @Override
    public String toString() {
        return name;
    }
}
