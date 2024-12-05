package game.command.commands;

import java.text.ParseException;

import game.command.Command;
import game.context.GameContext;
import game.map.Item;

/**
 * @author Jacob Herrmann
 * 
 * Command to pick up items
 */
public class GrabCommand extends Command {

    public GrabCommand() {
        super(1);
    }

    @Override
    public void run(GameContext context) throws ParseException {
        Item item = context.getPlayer().getCurrentRoom().getItem(arguments[0]);
        if (item == null) {
            context.getOutputWriter().write("You cannot find item " + arguments[0].toLowerCase() + " in this room.\n");
            return;
        }

        context.getPlayer().addItem(item);
        context.getOutputWriter().write("You picked up the " + item.name() + ".\n");
    }
    
}
