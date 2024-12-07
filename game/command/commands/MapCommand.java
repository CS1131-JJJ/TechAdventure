package game.command.commands;

import java.text.ParseException;

import game.command.Command;
import game.context.GameContext;
import game.map.Map;

/**
 * @author Jacob Charmley
 * 
 * Command to display the map
 */
public class MapCommand extends Command {

    public MapCommand() {
        super(0);
    }

    @Override
    public void run(GameContext context) throws ParseException {
        context.getOutputWriter().write(Map.getMap(
            context.getRooms(),
            context.getPlayer().getCurrentRoom().getName()
        ));
    }


    
}
