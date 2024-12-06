package game.map;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import game.context.GameContext;

/**
 * @author Jacob Herrmann with Braden McKenzie, Julie Truckenbrod, Jacob Charmley
 * 
 * A Room which the player may enter.  
 */
public class Room {
    private final LinkedHashMap<Direction, Room> rooms;
    private final List<Item> items; 

    private final String name;
    private final String description;

    private final LinkedHashMap<Item, String> requiredItems;
    private final Consumer<GameContext> entryEvent;
    
    private boolean isVisible;
    
    /**
     * 
     * @param name The name of the room
     * @param description A description of the appearance of the room
     * @param items The items in the room
     * @param requiredItems Items which are required to enter the room. 
     *                      The String is the message which is displayed 
     *                      if the item is missing from the players inventory. 
     *                      The order of the items in the map is the ordered 
     *                      in which they should be checked in order to display 
     *                      the most logical death message to the user. 
     *                      May be an empty map. 
     * @param entryEvent A GameContext consumer which runs when the user enters
     *                   this room, provided they have all the required items. 
     */
    public Room(String name, String description, List<Item> items, LinkedHashMap<Item, String> requiredItems, Consumer<GameContext> entryEvent, boolean visible) {
        this.name = name;
        this.description = description;

        rooms = new LinkedHashMap<Direction, Room>();

        this.items = new ArrayList<Item>();
        this.items.addAll(items);

        this.entryEvent = entryEvent;
        this.requiredItems = requiredItems;

        this.isVisible = visible;
    }

    /**
     * Set which rooms are connected to this one. 
     * @param map The rooms which are connected to this one, via the 
     *            corresponding cardinal direction. 
     */
    public void setDirections(Map<Direction, Room> map) {
        //sort directions so they are displayed in a consistent order across rooms
        rooms.clear();
        Direction[] dir = map.keySet().toArray(new Direction[0]);
        Arrays.sort(dir);
        for (Direction d : dir) {
            rooms.put(d, map.get(d));
        }
    }

    /**
     * This method should be called when the player enters the room. 
     * This method checks that the player has the required items. If they do not, the game ends. If the player has the proper items the room entry event is triggered. 
     * @param context The GameContext
     */
    public void enterRoom(GameContext context) {
        isVisible = true;
        context.getOutputWriter().write("===" + name + "===\n");
        Item missing = getMissingItems(context.getPlayer().getInventory());
        if (missing == null) {
            context.getOutputWriter().write(getRoomDescription() + "\n");
            entryEvent.accept(context);
        }else {
            context.getOutputWriter().write(getItemMissingDescription(missing) + "\n");
            context.endGame();
        }
    }

    public String getRoomDescription() {
        StringBuilder builder = new StringBuilder();
        builder.append(description);
        builder.append("\n\n");
        builder.append("There is a path in the following direction(s):\n");

        for (Entry<Direction, Room> entry : rooms.entrySet()) {
            builder.append(entry.getKey().name().charAt(0));
            builder.append(entry.getKey().name().substring(1).toLowerCase());

            if (entry.getValue().isVisible) {
                builder.append(" - ");
                builder.append(entry.getValue().name);
            }
            builder.append('\n');
        }


        if (items.size() != 0) {
            builder.append("\nThis area contains: ");
            for (Item i : items) {
                builder.append("\n- ");
                builder.append(i);
            }
        }else {
            builder.append("\n\nThis area is empty of items. ");
        }

        builder.append('\n');

        return builder.toString();
    }

    /**
     * Return the first item that the player is missing from the items required to enter this room. 
     * @param inv players inventory
     * @return first item missing or null
     */
    private Item getMissingItems(List<Item> inv) {
        for (Item i : requiredItems.keySet()) {
            if (!inv.contains(i)) {
                return i;
            }
        }
        return null;
    }

    /**
     * Get the description which corresponds to the missing item. 
     * @param item 
     * @return Description string or null
     */
    private String getItemMissingDescription(Item item) {
        return requiredItems.getOrDefault(item, null);
    }

    /**
     * Add an item to this room. 
     * @param item Item to store in this room. 
     */
    public void addItem(Item item) {
        items.add(item);
    }

    /**
     * Get the item with the given name
     * @param itemName The name of the item to pick up
     * @return The item you picked up, or null
     */
    public Item getItem(String itemName) {
        Item item = null;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).name().toUpperCase().equals(itemName)) {
                item = items.get(i);
                items.remove(i);
                break;
            }
        }
        return item;
    }

    public List<Item> getItems() {
        //makes the list immutable. 
        return List.of(items.toArray(new Item[0]));
    }

    /**
     * Get the room in the specified direction. If there is no room in that direction accessible from this room, return is null. 
     * @param dir A direction
     * @return A Room object other than this one or null.
     */
    public Room getRoom(Direction dir) {
        return rooms.getOrDefault(dir, null);
    }

    /**
     * Get the visibility of this room. Rooms are visible if they have been visited by the player. 
     * @return boolean value
     */
    public boolean getIsVisible() {
        return isVisible;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Write the state of this object to the given file
     * @param file File write to 
     * @param itemMap Map relating the Item objects to the item IDs 
     * @throws IOException
     */
    public void writeToFile(File file, Map<Item, String> itemMap) throws IOException {
        FileWriter writer = new FileWriter(file);

        writer.append("NAME\n");
        writer.append(name + "\n");

        writer.append("DESC\n");
        writer.append(description + "\n");

        writer.append("VISIBLE\n");
        writer.append(isVisible ? "TRUE\n" : "FALSE\n");

        for (Entry<Direction, Room> entry : rooms.entrySet()) {
            writer.append("ROOM\n");
            writer.append(entry.getKey().name() + "\n");
            writer.append(entry.getValue().name + "\n");
        }

        for (Item i : items) {
            writer.append("ITEM\n");
            writer.append(itemMap.get(i) + "\n");
        }

        for (Entry<Item, String> entry : requiredItems.entrySet()) {
            writer.append("REQUIRED\n");
            writer.append(itemMap.get(entry.getKey()) + "\n");
            writer.append(entry.getValue() + "\n");
        }

        writer.flush();
        writer.close();
    }

}
