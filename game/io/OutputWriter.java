package game.io;

/**
 * @author Jacob Herrmann
 * 
 * Write output to the screen for the user to see. 
 */
public class OutputWriter {

    private StringBuilder buffer = new StringBuilder();

    public void write(String text) {
        buffer.append(text);
    }

    public String getOutput() {
        return buffer.toString();
    }

} 
