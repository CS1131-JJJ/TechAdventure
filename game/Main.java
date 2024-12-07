package game;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import game.command.Command;
import game.command.commands.LookCommand;
import game.context.GameContext;
import game.context.GameContextLoader;
import game.io.InputReader;
import game.io.OutputWriter;
import game.io.console.ConsoleInputReader;
import game.io.console.ConsoleOutputWriter;
import game.map.Room;
import game.player.Player;

/**
 * @author Jacob Herrmann with Braden McKenzie, Julie Truckenbrod, Jacob Charmley
 * 
 * The entry point of the application. 
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Map<String, Consumer<GameContext>> entryEventMap = new HashMap<>();
        OutputWriter writer = new ConsoleOutputWriter();
        InputReader reader = new ConsoleInputReader();

        entryEventMap.put("Tech Trails", (context) -> {
            new ForceLeaveEvent(
                "KEY", 
                "You have made it to the tech trails, your key clicks smoothly unlocking the door. The room smells of dust and old wood. Snowshoes and skis are propped up neatly against the wall.\n",
                "You're facing an old building that reads Tech Trails. You try the door but it  is locked tight. Snowshoes and skis are visible inside, but without the key, you need to turn back to wads.\n",
                "Wadsworth Hall"
            ).apply(context);
        });

        entryEventMap.put("Under Statue", (context) -> {
            new ForceLeaveEvent(
                "CELLO",
                "" ,
                "“Sorry kid, but we've got no deal today. You’re missing… something” He waves his hand dismissively, and before you can protest, the world seems to twist around you. The next thing you know, you're standing back in the plaza, Joe's laughter still ringing in your ears.\n",
                "Plaza"
            ).apply(context);
        });

        entryEventMap.put("Mt. Ripley", (context) -> {
            context.getOutputWriter().write("\nYOU WIN!!\n");
            context.endGame();
        });

        entryEventMap.put("DHH", (context) -> {
            Player player = context.getPlayer();
            
            if (!ForceLeaveEvent.containsName(player.getInventory(), "SHOVEL")) {
                context.getOutputWriter().write("A man holding a shovel grins from a corner, offering a riddle: \"Answer this, and you may pass. Fail, and you won’t leave...\" \n");
                while(true) {
                    context.getOutputWriter().write(" \"What... do computers eat?\" \n> ");
                    String input = reader.getInput().trim().toLowerCase();
                    if (input.equals("chips")) {
                        break;
                    }
                    context.getOutputWriter().write(" \"WRONG!\"\n");
                }
    
                context.getOutputWriter().write(" \"Correct!\"\n");
                Command c = new LookCommand();
                try {
                    c.setArguments(new String[0]);
                    c.run(context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });


        GameContextLoader loader = new GameContextLoader(entryEventMap);
        loader.loadContext(writer);
        GameContext context = loader.getLoadedContext();
        InputParser parser = new InputParser(context);

        Room room = context.getPlayer().getCurrentRoom();
        context.getPlayer().setRoom(room, context);

        while(!context.isGameOver()) {
            writer.write("> ");
            parser.processCommand(reader.getInput());

            //if the restore command was used we change to the new context. 
            context = loader.getLoadedContext(); 
            parser.setContext(context);
        }
    }
}
