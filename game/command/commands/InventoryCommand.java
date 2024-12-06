package game.command.commands;

import java.text.ParseException;
import java.util.List;

import game.command.Command;
import game.context.GameContext;
import game.map.Item;

/**
 * @author Julie Truckenbrod
 * 
 * Command to display the contents of the player's inventory
 */
public class InventoryCommand extends Command {

    public InventoryCommand() {
        super(0); // Takes no arguments
    }

    @Override
    public void run(GameContext context) throws ParseException {
        List<Item> inventory = context.getPlayer().getInventory();
        
        if (inventory.isEmpty()) {
            context.getOutputWriter().write("Your inventory is empty.\n");
            return;
        }
        
        context.getOutputWriter().write("Your inventory contains:\n");
        for (Item item : inventory) {
            context.getOutputWriter().write("- " + item.name() + "\n");
        }
    }
}