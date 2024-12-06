package game.command.commands;

import java.text.ParseException;

import game.command.Command;
import game.context.GameContext;
import game.map.Room;

/**
 * @author Julie Truckenbrod
 * 
 * Command to look around the current room and get its description
 */
public class LookCommand extends Command {

    public LookCommand() {
        super(0); // Takes no arguments
    }

    @Override
    public void run(GameContext context) throws ParseException {
        // Get the current room
        Room room = context.getPlayer().getCurrentRoom();
        
        // Output room name and description without triggering entry event
        context.getOutputWriter().write("===" + room.getName() + "===\n");
        context.getOutputWriter().write(room.getRoomDescription() + "\n");
    }
    
}