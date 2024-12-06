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
 * Command to display the map
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
            int width = scanner.nextInt();
            int height = scanner.nextInt();
            String flush = scanner.nextLine();
            String input = scanner.useDelimiter("\\A").next(); // Read the whole input
            scanner.close();

            // Converts map file into array of chars
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

            context.getOutputWriter().write(charrArrToString(map));

        } catch (FileNotFoundException e) {
            System.out.println("Issues with the map file loading");
            e.printStackTrace();
        }
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
    
    // Collapses a 2D array of chars to a string
    private String charrArrToString(char[][] arr) {
        String output = "";
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                output += arr[i][j];
            }
        }
        return output;
    }

    // Prints char array to console
    private void print(char[][] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                System.out.print(arr[i][j]);
            }
        }
    }
    
}
