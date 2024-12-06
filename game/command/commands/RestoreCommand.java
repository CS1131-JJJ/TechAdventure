package game.command.commands;

import java.io.File;
import java.text.ParseException;

import game.command.Command;
import game.context.GameContext;
import game.context.GameContextLoader;

/**
 * @author Jacob Herrmann
 * 
 * Restore game state from save files. 
 */
public class RestoreCommand extends Command {

    public RestoreCommand() {
        super(1); 
    }

    @Override
    public void run(GameContext context) throws ParseException {
        try {
            GameContextLoader loader = context.getLoader();
            loader.loadContext(context.getOutputWriter(), new File(arguments[0].toLowerCase()));
            context = loader.getLoadedContext();
            System.out.println(context.hashCode());
        } catch (Exception e) {
            throw new ParseException("Exception while trying to load game state: " + e.getMessage(), 1);
        }
        context.getOutputWriter().write("Game state loaded.\n");
        context.getPlayer().setRoom(context.getPlayer().getCurrentRoom(), context);
    }
    
}