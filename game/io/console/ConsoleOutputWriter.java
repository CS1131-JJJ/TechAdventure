package game.io.console;

import game.io.OutputWriter;

public class ConsoleOutputWriter implements OutputWriter {

    @Override
    public void write(String write) {
        System.out.print(write);
    }
    
}
