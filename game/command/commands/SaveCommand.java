package game.command.commands;

import java.io.File;
import java.text.ParseException;

import game.command.Command;
import game.context.GameContext;

/**
 * @author Jacob Herrmann
 * 
 * Save the current game state to the disk. 
 */
public class SaveCommand extends Command {

    public SaveCommand() {
        super(1); 
    }

    @Override
    public void run(GameContext context) throws ParseException {
        try {
            context.getLoader().saveContext(new File(arguments[0].toLowerCase()), context);
        } catch (Exception e) {
            throw new ParseException("Exception while trying to save game state: " + e.getMessage(), 1);
        }
        context.getOutputWriter().write("Game state saved.\n");
    }
    
}