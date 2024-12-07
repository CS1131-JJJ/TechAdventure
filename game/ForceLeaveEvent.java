package game;

import java.util.List;

import game.context.GameContext;
import game.map.Item;
import game.map.Room;
import game.player.Player;

public class ForceLeaveEvent {
    
    private final String itemName;
    private final String successMessage;
    private final String failMessage;
    private final String returnRoom;

    public ForceLeaveEvent(String itemName, String successMessage, String failMessage, String returnRoom) {
        this.itemName = itemName;
        this.successMessage = successMessage;
        this.failMessage = failMessage;
        this.returnRoom = returnRoom;
    }

    public void apply(GameContext context) {
        Player player = context.getPlayer();
        
        if (containsName(player.getInventory(), itemName)) {
            context.getOutputWriter().write(successMessage);
        }else {
            context.getOutputWriter().write(failMessage);
            player.setRoom(getRoom(context.getRooms(), returnRoom), context);
        }
    }

    public static boolean containsName(List<Item> list, String name) {
        for (Item i : list) {
            if (i.name().toUpperCase().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private Room getRoom(List<Room> rooms, String name) {
        for (Room r : rooms) {
            if (r.getName().equals(name)) {
                return r;
            }
        }

        return null;
    }

}
