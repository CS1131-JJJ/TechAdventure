package game.command;

import java.text.ParseException;

import game.context.GameContext;

/**
 * @author Jacob Herrmann with Braden McKenzie, Julie Truckenbrod, Jacob Charmley
 * 
 * The base class for commands. All commands must extend this class. 
 */
public abstract class Command {

    protected final int argCount;
    protected String[] arguments;

    protected Command(int argCount) {
        this.argCount = argCount;
    }

    public final void setArguments(String[] args) throws Exception {
        if (args.length != argCount) {
            throw new Exception("Incorrect number of arguments for command, " + argCount + " expected.");
        }
        arguments = args;
    }

    public abstract void run(GameContext context) throws ParseException;

}
