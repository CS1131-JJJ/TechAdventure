package game;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import game.context.GameContext;
import game.context.GameContextLoader;
import game.io.InputReader;
import game.io.OutputWriter;
import game.io.console.ConsoleInputReader;
import game.io.console.ConsoleOutputWriter;
import game.map.Room;

/**
 * @author Jacob Herrmann with Braden McKenzie, Julie Truckenbrod, Jacob Charmley
 * 
 * The entry point of the application. 
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Map<String, Consumer<GameContext>> entryEventMap = new HashMap<>();
        OutputWriter writer = new ConsoleOutputWriter();
        //TODO populate entry event map here


        GameContextLoader loader = new GameContextLoader(entryEventMap);
        loader.loadContext(writer);
        GameContext context = loader.getLoadedContext();
        InputReader reader = new ConsoleInputReader();
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
