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
            int width = 32;
            int height = 83;
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

            char space = '\u0020';

            for (int i = 0; i < context.getRooms().size(); i++) {
                if (!context.getRooms().get(i).getIsVisible()) {
                    map = fill(
                        map, 
                        context.getRooms().get(i).getMapCoordFirst().x,
                        context.getRooms().get(i).getMapCoordFirst().y,
                        context.getRooms().get(i).getMapCoordSecond().x,
                        context.getRooms().get(i).getMapCoordSecond().y,
                        space
                    );
                }
            }

            //map = fill(map, 3, 10, 7, 17, space);

            print(map);

            // context.getOutputWriter().write(output);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //context.getPlayer().getCurrentRoom();
        //context.getRooms()
    }

    // Fills a given area within a char array with a given character
    private char[][] fill(char[][] arr, int x1, int y1, int x2, int y2, char character) {
        char[][] output = arr;
        for (int i = x1; i < x2; i++) {
            for (int j = y1; j < y2; j++) {
                output[i][j] = character;
            }
        }
        return output;
    }

    private void print(char[][] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                System.out.print(arr[i][j]);
            }
        }
    }
    
}
