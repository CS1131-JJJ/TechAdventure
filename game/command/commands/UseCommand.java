package game.command.commands;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import game.command.Command;
import game.context.GameContext;
import game.map.Direction;
import game.map.Room;

/**
 * @author Jacob Herrmann
 * 
 * Use the shovel to uncover the room under the statue.
 */
public class UseCommand extends Command {

    public UseCommand() {
        super(1); 
    }

    @Override
    public void run(GameContext context) throws ParseException {
        Room room = context.getPlayer().getCurrentRoom();
        if (arguments[0].equals("SHOVEL")) {
            if (room.getName().equals("Plaza")) {
                HashMap<Direction, Room> map = new HashMap<>();
                map.putAll(room.getDirections());
                map.put(Direction.DOWN, getRoom("Under Statue", context.getRooms()));
                room.setDirections(map);
                context.getOutputWriter().write("Using the shovel, you uncover a hidden pathway under the statue. A muffled voice beckons you to descend.\n");

            }else {
                context.getOutputWriter().write("You cannot use that item here.\n");
            }
        }else {
            context.getOutputWriter().write("You cannot use this.\n");
        }
    }

    private Room getRoom(String name, List<Room> rooms) {
        for (Room r : rooms) {
            if (r.getName().equals(name)) {
                return r;
            }
        }

        return null;
    }
    
}