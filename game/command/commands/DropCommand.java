package game.command.commands;

import game.command.Command;
import game.context.GameContext;
import game.map.Item;
import java.text.ParseException;

/**
 * @author Julie Truckenbrod
 * 
 * Command to drop items from inventory into the current room
 */
public class DropCommand extends Command {

    public DropCommand() {
        super(1);
    }

    @Override
    public void run(GameContext context) throws ParseException {
        // Try to get the item from player's inventory
        Item item = context.getPlayer().getItem(arguments[0].toUpperCase());
        if (item == null) {
            context.getOutputWriter().write("You don't have " + arguments[0].toLowerCase() + " in your inventory.\n");
            return;
        }

        // Add the item to the current room
        context.getPlayer().getCurrentRoom().addItem(item);
        //need to add this 
        context.getOutputWriter().write("You dropped the " + item.name() + ".\n");
    }
    
}