package game;
import game.command.Command;
import game.command.commands.*;
import game.context.GameContext;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jacob Herrmann
 * This class parses input from the user into commands. 
 */
public class InputParser {
    
    private GameContext context;
    private final Map<String, Command> commandTable = new HashMap<>();

    public InputParser(GameContext context) {
        this.context = context;
        commandTable.put("GO", new GoCommand());
        commandTable.put("GRAB", new GrabCommand());
        commandTable.put("MAP", new MapCommand());
        commandTable.put("DROP", new DropCommand());
        commandTable.put("LOOK", new LookCommand());
        commandTable.put("SAVE", new SaveCommand());
        commandTable.put("INSPECT", new InspectCommand());
        commandTable.put("INVENTORY", new InventoryCommand());
        commandTable.put("HELP", new HelpCommand());
        // NEW COMMANDS MUST BE IN ALL CAPS

        setContext(context);
    }
    
    /**
     * Update the game context. Necessary for loading states from the disk. 
     * @param context new game context 
     */
    public void setContext(GameContext context) {
        this.context = context;
        context.setAllCommands(commandTable.keySet().toArray(new String[0]));
    }

    public void processCommand(String input) {
        if (input.isBlank()) {
            context.getOutputWriter().write("You must input a command\n");
        }

        String[] inputArray = input.trim().toUpperCase().split(" ");

        if (!commandTable.containsKey(inputArray[0])) {
            context.getOutputWriter().write("Command not recognized.\n");
            return;
        }

        Command command = commandTable.get(inputArray[0]);

        try {
            command.setArguments(Arrays.copyOfRange(inputArray, 1, inputArray.length));
            command.run(context);
        } catch (Exception e) {
            context.getOutputWriter().write(e.getMessage() + "\n");
        }

    }

}