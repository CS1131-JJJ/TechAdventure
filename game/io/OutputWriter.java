package game.io;

/**
 * @author Jacob Herrmann
 * @author Jacob Charmley
 * @author Julie Truckenbrod
 * @author Braden McKenzie
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
