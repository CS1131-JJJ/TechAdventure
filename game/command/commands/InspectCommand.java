package game.command.commands;

import game.command.Command;
import game.context.GameContext;
import game.map.Item;
import java.text.ParseException;
import java.util.List;

/**
 * @author Julie Truckenbrod
 * 
 * Command to inspect items in the current room or player's inventory
 */
public class InspectCommand extends Command {

    public InspectCommand() {
        super(1); // take argument of the item name
    }

    @Override
    public void run(GameContext context) throws ParseException {
        String itemName = arguments[0];
        
        // First check player's inventory
        List<Item> inventory = context.getPlayer().getInventory();
        for (Item item : inventory) {
            if (item.name().toUpperCase().equals(itemName)) {
                context.getOutputWriter().write(item.name() + " is in your inventory.\n");
                context.getOutputWriter().write(item.description() + "\n");
                return;
            }
        }
        
        // Then check current room
        List<Item> roomItems = context.getPlayer().getCurrentRoom().getItems();
        for (Item item : roomItems) {
            if (item.name().toUpperCase().equals(itemName)) {
                context.getOutputWriter().write(item.name() + " is in the current room.\n");
                context.getOutputWriter().write(item.description() + "\n");
                return;
            }
        }
        
        // Item not found in either location
        context.getOutputWriter().write("You cannot find " + itemName.toLowerCase() + " to inspect.\n");
    }
}