package game.command.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Scanner;

import game.command.Command;
import game.context.GameContext;

/**
 * @author Jacob Charmley
 * 
 * Command to pick up items
 */
public class MapCommand extends Command {

    public MapCommand() {
        super(0);
    }

    @Override
    public void run(GameContext context) throws ParseException {

        try {
            // Reads map file
            File mapFile = new File("resources/map.txt");
            Scanner scanner = new Scanner(mapFile);
            String input = scanner.useDelimiter("\\A").next(); // Read the whole input
            scanner.close();

            // Converts map file into array of chars
            int width = 30;
            int height = 80;
            char[][] map = new char[width][height];
            int count = 0;
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    if (count < input.length()) {
                        map[i][j] = input.charAt(count);
                        count++;
                    }
                }
            }

            // DEBUG prints map to console
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    System.out.print(map[i][j]);
                }
            }

            // context.getOutputWriter().write(output);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //context.getPlayer().getCurrentRoom();
        //context.getRooms()
    }
    
}
