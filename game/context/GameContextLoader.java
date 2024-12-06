package game.context;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.function.Consumer;
import java.awt.Point;

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

    private final File saveDir = new File("resources");
    private final String roomSubdir = "rooms";
    private final String roomFileExtension = ".roomsave";
    private final String playerFile = "player.psave";
    private final String itemFile = "items.itemsave";

    private final Map<String, Consumer<GameContext>> entryEventMap;

    private final Map<String, Map<Direction, String>> directionMap = new HashMap<>();

    private Map<String, Item> itemMap; //used for loading from file

    private Map<Item, String> itemReverseMap; //used for writing to file

    public GameContextLoader(Map<String, Consumer<GameContext>> entryEventMap) {
        this.entryEventMap = entryEventMap;
    }

    /**
     * Save the current game state.
     * @param saveDir The directory to save the files to. 
     * @param context The context to save. 
     * @throws Exception
     */
    public void saveContext(File saveDir, GameContext context) throws Exception {
        if (saveDir.equals(this.saveDir)) {
            throw new Exception("Cannot save game state to default state directory.");
        }

        if(!saveDir.exists()) {
            saveDir.mkdirs();
        }

        List<Item> items = new ArrayList<>();

        for (Room r : context.getRooms()) {
            items.addAll(r.getItems());
        }
        items.addAll(context.getPlayer().getInventory());

        itemReverseMap = saveItems(new File(saveDir, itemFile), items);

        File roomSaveDir = new File(saveDir, roomSubdir);
        roomSaveDir.mkdir();
        for (int i = 0; i < context.getRooms().size(); i++) {
            context.getRooms().get(i)
            .writeToFile(
                new File(roomSaveDir, i+roomFileExtension), itemReverseMap
            ); 
        }

        savePlayer(new File(saveDir, playerFile), context.getPlayer());
    }

    /**
     * Load the default game context. The game context is loaded from the default directory. 
     * @param writer OutputWriter to construct context with
     * @return The GameContext which represents the starting context for a new game
     * @throws Exception
     */
    public GameContext loadContext(OutputWriter writer) throws Exception {
        return loadContext(writer, saveDir);
    }

    /**
     * Load the game context from the given directory. 
     * @param writer OutputWriter to construct context with
     * @param saveDir Directory containing save files. 
     * @return GameContext based on the contents of the save files.
     * @throws Exception
     */
    public GameContext loadContext(OutputWriter writer, File saveDir) throws Exception {
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
            writer,
            this
        );
        
    }

    private Map<String, Item> getItems(File file) throws Exception {
        Scanner sc = new Scanner(file);
        Map<String, Item> rtn = new HashMap<>();

        while (sc.hasNextLine()) {
            String type = sc.nextLine().trim();

            if (type.isBlank()) {
                continue;
            }

            if (type.equals("ITEMDEF")) {
                rtn.put(sc.nextLine().trim(), new Item(sc.nextLine().trim(), sc.nextLine().trim()));
            }else {
                sc.close();
                throw new Exception("Exception occurred while parsing items, type " + type + " not recognized.");
            }
        }

        sc.close();
        return rtn;
    }

    /**
     * Save items to the given file. 
     * @param file File to write to 
     * @param items Items to save
     * @return A Map relating the Item objects to the IDs which will be used for this save. 
     * @throws IOException
     */
    private Map<Item, String> saveItems(File file, List<Item> items) throws IOException {
        FileWriter writer = new FileWriter(file);

        Map<Item, String> map = new HashMap<>();

        for (int i = 0; i < items.size(); i++) {
            writer.append("ITEMDEF\n");
            writer.append(i + "\n");
            writer.append(items.get(i).name() + "\n");
            writer.append(items.get(i).description() + "\n");

            map.put(items.get(i), i + "");
        }

        writer.flush();
        writer.close();

        return map;
    }

    /**
     * Save player state to a file
     * @param file File to save state to 
     * @param player Player state to save
     * @throws IOException
     */
    private void savePlayer(File file, Player player) throws IOException {
        FileWriter writer = new FileWriter(file);

        writer.append("CURR\n");
        writer.append(player.getCurrentRoom().getName() + "\n");

        for (Item i : player.getInventory()) {
            writer.append("ITEM\n");
            writer.append(itemReverseMap.get(i) + "\n");
        }

        writer.flush();
        writer.close();
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
        boolean isVisible = false;
        Point mapCoordFirst = new Point(0, 0);
        Point mapCoordSecond = new Point(0, 0);

        Scanner sc = new Scanner(file);

        String id;

        while (sc.hasNextLine()) {
            String type = sc.nextLine().trim();

            if (type.isBlank()) {
                continue;
            }

            switch (type) {
                case "NAME":
                    name = sc.nextLine().trim();
                    break;
                case "DESC":
                    description = sc.nextLine().trim();
                    break;
                case "ITEM":
                    id = sc.nextLine().trim();
                    if (!itemMap.containsKey(id)) {
                        sc.close();
                        throw new Exception("Exception occurred while parsing room, item with ID " + id + " does not exist.");
                    }
                    items.add(itemMap.get(id));
                    break;
                case "REQUIRED":
                    id = sc.nextLine().trim();
                    if (!itemMap.containsKey(id)) {
                        sc.close();
                        throw new Exception("Exception occurred while parsing room, item with ID " + id + " does not exist.");
                    }
                    requiredItems.put(
                        itemMap.get(id),
                        sc.nextLine().trim()
                    );
                    break;
                case "ROOM":
                    map.put(
                        Direction.valueOf(sc.nextLine().trim()),
                        sc.nextLine().trim()
                    );
                    break;
                case "VISIBLE":
                    if (sc.nextLine().trim().equals("TRUE")) {
                        isVisible = true;
                    }else {
                        isVisible = false;
                    }
                    break;
                case "COORDS":
                    mapCoordFirst.x = sc.nextInt() - 1;
                    mapCoordFirst.y = sc.nextInt() - 1;
                    mapCoordSecond.x = sc.nextInt();
                    mapCoordSecond.y = sc.nextInt();
                    break;
                default:
                    sc.close();
                    throw new Exception("Exception occurred while parsing room, type " + type + " not recognized.");
            }
        }
        directionMap.put(name, map);
        sc.close();
        
        return new Room(name, description, items, requiredItems, entryEventMap.getOrDefault(name, (c) -> {}), isVisible, mapCoordFirst, mapCoordSecond);
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
            String type = sc.nextLine().trim();

            if (type.isBlank()) {
                continue;
            }

            if (type.equals("CURR")) {
                roomName = sc.nextLine().trim();
            }else if(type.equals("ITEM")) {
                String id = sc.nextLine().trim();
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
