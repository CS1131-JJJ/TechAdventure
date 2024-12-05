package game.command.commands;

import java.text.ParseException;

import game.command.Command;
import game.context.GameContext;
import game.map.Direction;
import game.map.Room;

/**
 * @author Jacob Herrmann
 * 
 * User command to move between rooms. 
 */
public class GoCommand extends Command {

    public GoCommand() {
        super(1);
    }

    @Override
    public void run(GameContext context) throws ParseException {
        Direction d;
        try {
            d = Direction.valueOf(arguments[0].toUpperCase());
        }catch (IllegalArgumentException e) {
            throw new ParseException("Could not parse argument into direction: " + arguments[0], 1);
        }

        Room room = context.getPlayer().getCurrentRoom().getRoom(d);;
        if (room != null) {
            context.getPlayer().setRoom(room, context);
        }else {
            context.getOutputWriter().write("You cannot go " + d.name().toLowerCase() + " from this location.\n");
        }

    }

    
}
