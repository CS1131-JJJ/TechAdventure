package game.map;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

/**
 * @author Jacob Herrmann
 * @author Jacob Charmley
 * @author Julie Truckenbrod
 * @author Braden McKenzie
 * 
 */
public class Map {

    public static String getMap(List<Room> rooms, String currentRoom) {
        
        if (
            currentRoom.equals("Admin Building") || 
            currentRoom.equals("DHH") || 
            currentRoom.equals("MEEM") || 
            currentRoom.equals("Plaza") ||
            currentRoom.equals("Rehki") ||
            currentRoom.equals("Rozsa") ||
            currentRoom.equals("Tech Trails") ||
            currentRoom.equals("Wadsworth Hall") ||
            currentRoom.equals("Walker Lawn")
        ){
            try {
                // Reads map file   
                File mapFile = new File("resources/map.txt");
                Scanner scanner = new Scanner(mapFile);
                int width = scanner.nextInt();
                int height = scanner.nextInt();
                scanner.nextLine();
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
                //char space = 'O';
    
                for (int i = 0; i < rooms.size(); i++) {
                    if (!rooms.get(i).getIsVisible()) {
                        map = fill(
                            map, 
                            rooms.get(i).getMapCoordFirst().x,
                            rooms.get(i).getMapCoordFirst().y,
                            rooms.get(i).getMapCoordSecond().x,
                            rooms.get(i).getMapCoordSecond().y,
                            space
                        );
                    }else if (!rooms.get(i).getName().equals(currentRoom)) {
                        map = removeX(
                            map,
                            rooms.get(i).getMapCoordFirst().x,
                            rooms.get(i).getMapCoordFirst().y,
                            rooms.get(i).getMapCoordSecond().x,
                            rooms.get(i).getMapCoordSecond().y
                        );
                    }
                }
    
                return charrArrToString(map);
    
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return "Cannot load map file. ";
            }
        }
        return "You cannot find where you are on the map.";
    }

    // Fills a given area within a char array with a given character
    private static char[][] fill(char[][] arr, int x1, int y1, int x2, int y2, char character) {
        char[][] output = arr;
        for (int i = x1; i < x2; i++) {
            for (int j = y1; j < y2; j++) {
                output[i][j] = character;
            }
        }
        return output;
    }

    // Remove 'X' character from the given region
    private static char[][] removeX(char[][] arr, int x1, int y1, int x2, int y2) {
        for (int i = x1; i < x2; i++) {
            for (int j = y1; j < y2; j++) {
                if (arr[i][j] == 'X') {
                    arr[i][j] = ' ';
                    return arr;
                }
            }
        }
        return arr;
    }

    // Collapses a 2D array of chars to a string
    private static String charrArrToString(char[][] arr) {
        String output = "";
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                output += arr[i][j];
            }
        }
        return output;
    }

}
