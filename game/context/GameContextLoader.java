package game.context;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.function.Consumer;

import game.io.OutputWriter;
import game.map.Direction;
import game.map.Item;
import game.map.Room;
import game.player.Player;

/**
 * @author Jacob Herrmann
 * 
 * Load the game context from the given directory. 
 */
public class GameContextLoader {

    private final File saveDir;
    private final String roomSubdir = "rooms";
    private final String roomFileExtension = ".roomsave";
    private final String playerFile = "player.psave";
    private final String itemFile = "items.itemsave";

    private final Map<String, Consumer<GameContext>> entryEventMap;

    private final Map<String, Map<Direction, String>> directionMap = new HashMap<>();

    private Map<String, Item> itemMap;

    public GameContextLoader(File saveDir, Map<String, Consumer<GameContext>> entryEventMap) {
        this.entryEventMap = entryEventMap;
        this.saveDir = saveDir;
    }

    public GameContext loadContext(OutputWriter writer) throws Exception {
        //load items
        itemMap = getItems(new File(saveDir, itemFile));

        //load rooms
        File roomDir = new File(saveDir, roomSubdir);
        Map<String, Room> rooms = new HashMap<>();
        
        for (File file : roomDir.listFiles()) {
            String name = file.getName();
            //skip files with non-matching extensions
            if (name.substring(name.lastIndexOf('.')).equals(roomFileExtension)) {
                Room room = getRoom(file);
                rooms.put(room.getName(), room);
            }
        }

        {
            //using Direction->String map, set Direction->Room map of each individual room
            //in this code block, Strings represent the name of a room

            Map<Direction, Room> map;
            //iterate through each pair of String -> Map<Direction, String>
            for (Entry<String, Map<Direction, String>> entry : directionMap.entrySet()) {
                map = new HashMap<>();

                //Convert Direction->String to Direction->Room. This is possible because the String represents the name of the room, and every room has now been loaded.
                for (Entry<Direction, String> a : entry.getValue().entrySet()) {
                    if (! rooms.containsKey(a.getValue())) {
                        throw new Exception("Room " + entry.getKey() + " refers to not-existent room " + a.getValue());
                    }
                    map.put(a.getKey(), rooms.get(a.getValue()));
                }

                //now that we have the complete Direction->Room map, we add it to the Room we are working on
                rooms.get(entry.getKey()).setDirections(map);
            }

            //all Rooms now have a complete map
        }

        return new GameContext(
            rooms.values().toArray(new Room[0]), 
            getPlayer(new File(saveDir, playerFile), rooms), 
            writer
        );
        
    }

    private Map<String, Item> getItems(File file) throws Exception {
        Scanner sc = new Scanner(file);
        Map<String, Item> rtn = new HashMap<>();

        while (sc.hasNextLine()) {
            String type = sc.nextLine();
            if (type.equals("ITEMDEF")) {
                rtn.put(sc.nextLine(), new Item(sc.nextLine(), sc.nextLine()));
            }else {
                sc.close();
                throw new Exception("Exception occurred while parsing items, type " + type + " not recognized.");
            }
        }

        sc.close();
        return rtn;
    }

    /**
     * Parse a room object from the given file. 
     * @param file Text file containing room state information. 
     * @return A Room object based on the content of the file
     * @throws Exception FileNotFound or general exception if there is an issue with parsing. 
     */
    private Room getRoom(File file) throws Exception {
        String name = null;
        String description = null;
        List<Item> items = new ArrayList<>();
        LinkedHashMap<Item, String> requiredItems = new LinkedHashMap<>();
        Map<Direction, String> map = new HashMap<>();

        Scanner sc = new Scanner(file);

        String id;

        while (sc.hasNextLine()) {
            String type = sc.nextLine();

            if (type.isBlank()) {
                continue;
            }

            switch (type) {
                case "NAME":
                    name = sc.nextLine();
                    break;
                case "DESC":
                    description = sc.nextLine();
                    break;
                case "ITEM":
                    id = sc.nextLine();
                    if (!itemMap.containsKey(id)) {
                        sc.close();
                        throw new Exception("Exception occurred while parsing room, item with ID " + id + " does not exist.");
                    }
                    items.add(itemMap.get(id));
                    break;
                case "REQUIRED":
                    id = sc.nextLine();
                    if (!itemMap.containsKey(id)) {
                        sc.close();
                        throw new Exception("Exception occurred while parsing room, item with ID " + id + " does not exist.");
                    }
                    requiredItems.put(
                        itemMap.get(id),
                        sc.nextLine()
                    );
                    break;
                case "ROOM":
                    map.put(
                        Direction.valueOf(sc.nextLine()),
                        sc.nextLine()
                    );
                    break;
                default:
                    sc.close();
                    throw new Exception("Exception occurred while parsing room, type " + type + " not recognized.");
            }
        }
        directionMap.put(name, map);
        sc.close();

        return new Room(name, description, items, requiredItems, entryEventMap.getOrDefault(name, (c) -> {}));
    }

    /**
     * Parse a player object from the given file
     * @param file File to parse
     * @param rooms Rooms lookup table to use for 
     * @return The Player object based on the content of the file 
     * @throws Exception FileNotFound or general exception if there is an issue with parsing
     */
    private Player getPlayer(File file, Map<String, Room> rooms) throws Exception {
        String roomName = null;
        List<Item> inv = new ArrayList<>();

        Scanner sc = new Scanner(file);

        while(sc.hasNextLine()) {
            String type = sc.nextLine();

            if (type.isBlank()) {
                continue;
            }

            if (type.equals("CURR")) {
                roomName = sc.nextLine();
            }else if(type.equals("ITEM")) {
                String id = sc.nextLine();
                if (!itemMap.containsKey(id)) {
                    sc.close();
                    throw new Exception("Exception occurred while parsing player, item with ID " + id + " does not exist.");
                }
                inv.add(itemMap.get(id));
            }else {
                sc.close();
                throw new Exception("Exception occurred while parsing player, type " + type + " not recognized.");
            }
        }
        sc.close();

        if (!rooms.containsKey(roomName)) {
            throw new Exception("Exception occurred while parsing player, room " + roomName + " does not exist.");
        }

        return new Player(inv, rooms.get(roomName));
    }

}
