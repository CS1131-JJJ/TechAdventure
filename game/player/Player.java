package game.player;

import java.util.ArrayList;
import java.util.List;

import game.context.GameContext;
import game.map.Item;
import game.map.Room;

/**
 * @author Jacob Herrmann
 * @author Jacob Charmley
 * @author Julie Truckenbrod
 * @author Braden McKenzie
 * 
 * This class represents the current state of the player
 */
public class Player {

    private final List<Item> inventory;
    private Room currentRoom;

    public Player(List<Item> inventory, Room startRoom) {
        this.inventory = new ArrayList<Item>();
        this.inventory.addAll(inventory);

        currentRoom = startRoom;
    }

    /**
     * Get an immutable list representing the players inventory
     * @return the inventory of the player
     */
    public List<Item> getInventory() {
        //Make list immutable
        return List.of(inventory.toArray(new Item[inventory.size()]));
    }

    public void addItem(Item item) {
        inventory.add(item);
    }

    /**
     * Get an item from the players inventory. The returned item will be removed from the players inventory. 
     * @param name The name of the item
     * @return The item with the matching name or null. 
     */
    public Item getItem(String name) {
        Item item = null;
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).name().toUpperCase().equals(name)) {
                item = inventory.get(i);
                inventory.remove(i);
                break;
            }
        }

        return item;
    }

    /**
     * Get the room the player is currently in. 
     * @return
     */
    public Room getCurrentRoom() {
        return currentRoom;
    }

    /**
     * Set the room the player is in. If the room is null this method will do nothing. 
     * @param room The room the player is entering. 
     * @param context The game context which will be passed to the room enter event call. 
     */
    public void setRoom(Room room, GameContext context) {
        if (room != null) {
            currentRoom = room;
            currentRoom.enterRoom(context);
        }
    }
    
}
