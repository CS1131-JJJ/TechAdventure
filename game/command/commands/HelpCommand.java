package game.command.commands;

import java.text.ParseException;

import game.command.Command;
import game.context.GameContext;
import game.map.Item;

/**
 * @author Jacob Charmley
 * 
 * Command to pick up items
 */
public class HelpCommand extends Command {

    public HelpCommand() {
        super(0);
    }

    @Override
    public void run(GameContext context) throws ParseException {
        String output = "List of commands: \n";
        String[] commands = context.getAllCommands();
        for (int i = 0; i < commands.length; i++) {
            output += commands[i] + "\n";
        }
        context.getOutputWriter().write(output);
    }
    
}