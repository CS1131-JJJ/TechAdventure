package game.command.commands;

import game.command.Command;
import game.context.GameContext;
import java.text.ParseException;

/**
 * @author Jacob Herrmann
 * 
 * Quit the game
 */
public class QuitCommand extends Command {

    public QuitCommand() {
        super(0);
    }

    @Override
    public void run(GameContext context) throws ParseException {
       context.endGame();
    }
    
}