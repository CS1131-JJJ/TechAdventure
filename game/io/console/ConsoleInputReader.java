package game.io.console;

import java.util.Scanner;

import game.io.InputReader;

public class ConsoleInputReader implements InputReader {

    private final Scanner sc = new Scanner(System.in);

    @Override
    public String getInput() {
        return sc.nextLine();
    }

    public void close() {
        sc.close();
    }
    
}
