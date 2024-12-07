package game;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import game.command.Command;
import game.command.commands.LookCommand;
import game.context.GameContext;
import game.context.GameContextLoader;
import game.io.InputParser;
import game.io.OutputWriter;
import game.map.ForceLeaveEvent;
import game.map.Room;
import game.player.Player;

/**
 * @author Jacob Herrmann
 * @author Jacob Charmley
 * @author Julie Truckenbrod
 * @author Braden McKenzie
 */
public class TechAdventure {
   final int PORT = 1234; // Server port 1234 is usually free
   private final String htmlTemplate;

   private final GameContextLoader loader;
   private GameContext context;
   private final InputParser parser;
   private final Map<String, Consumer<GameContext>> entryEventMap;
   private final OutputWriter writer;

   private Consumer<String> event;
   private boolean eventMode = false;

   private boolean joeEventFinished = false;

   public static void main(String[] args) throws Exception {
      TechAdventure webFormExample = new TechAdventure();
      webFormExample.launchServer();
   }

   public TechAdventure() throws Exception {
      htmlTemplate = loadHTML(new File("resources/main.html"));

      entryEventMap = new HashMap<>();
      populateEntryEventMap();

      writer = new OutputWriter();

      loader = new GameContextLoader(entryEventMap);
      loader.loadContext(writer);
      context = loader.getLoadedContext();
      parser = new InputParser(context);

      context.getPlayer().setRoom(
            context.getPlayer().getCurrentRoom(),
            context);
   }

   private String loadHTML(File file) throws FileNotFoundException {
      Scanner sc = new Scanner(file);
      StringBuilder builder = new StringBuilder();
      while (sc.hasNextLine()) {
         builder.append(sc.nextLine().trim());
      }
      sc.close();

      return builder.toString();
   }

   public void launchServer() throws IOException {
      HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
      HttpContext context = server.createContext("/");
      context.setHandler(this::handleRequest);
      server.start();
      System.out.printf("Server started on port %s\n", PORT);
   }

   private void handleRequest(HttpExchange httpExchange) throws IOException {
      String command = "";
      if (httpExchange.getRequestMethod().equals("POST")) {
         command = httpRequestToCommand(httpExchange.getRequestBody());
      }

      if (!command.isBlank()) {
         context.getOutputWriter().write(">" + command + "\n");
      }
      byte[] response;

      if (eventMode) {
         event.accept(command);
         response = generateEventPage();
      } else {
         response = generatePage(
               processCommand(command),
               game.map.Map.getMap(context.getRooms(), context.getPlayer().getCurrentRoom().getName()),
               context.getPlayer()).getBytes(StandardCharsets.UTF_8);
               
               if (eventMode) {
                  response = generateEventPage();
               }
      }

      Headers h = httpExchange.getResponseHeaders();
      h.set("Content-Type", "text/html");
      httpExchange.sendResponseHeaders(200, response.length);
      OutputStream os = httpExchange.getResponseBody();
      os.write(response);
      os.close();

      if (context.isGameOver()) {
         System.exit(0);
      }
   }

   private byte[] generateEventPage() {
       return generatePage(
         context.getOutputWriter().getOutput(),
         game.map.Map.getMap(context.getRooms(), context.getPlayer().getCurrentRoom().getName()),
         context.getPlayer()).getBytes(StandardCharsets.UTF_8);
   }

   private String generatePage(String output, String map, Player player) {
      Room room = player.getCurrentRoom();

      String rtn = htmlTemplate
            .replace("$NAME", room.getName())
            .replace("$MAP", map)
            .replace("$OUT", output)
            .replace("$RM_INV", listToHtmlList(room.getItems(), (i, b) -> b.append(i.name())))
            .replace("$CMDS", listToHtmlList(List.of(context.getAllCommands()), (i, b) -> b.append(i)))
            .replace("$INV", listToHtmlList(player.getInventory(), (i, b) -> b.append(i.name())));

      if (context.isGameOver()) {
         return rtn
               .replace("$DESC", "")
               .replace("$JS", context.hasWon() ? "alert('You win!')" : "alert('Game over.')")
               .replace("$GAMEOVER", "disabled");
      } else {
         return rtn
               .replace("$DESC", room.getDescription())
               .replace("$JS", "")
               .replace("$GAMEOVER", "");
      }
   }

   private <T> String listToHtmlList(List<T> list, BiConsumer<T, StringBuilder> func) {
      if (list.isEmpty()) {
         return "<li>Empty</li>";
      }
      StringBuilder builder = new StringBuilder();
      for (T i : list) {
         builder.append("<li>");
         func.accept(i, builder);
         builder.append("</li>");
      }
      return builder.toString();
   }

   private String processCommand(String input) {
      parser.processCommand(input);

      context = loader.getLoadedContext();
      parser.setContext(context);

      return context.getOutputWriter().getOutput();
   }

   private String httpRequestToCommand(InputStream input) throws IOException {
      StringBuilder builder = new StringBuilder();
      int i = input.read();
      while (i != -1) {
         builder.append((char) i);
         i = input.read();
      }
      return builder.toString().substring(8).replace('+', ' '); // remove header info
   }

   private boolean containsNonDigit(String str) {
      for (char c : str.toCharArray()) {
         if (!Character.isDigit(c)) {
            return true;
         }
      }
      return false;
   }

   private void populateEntryEventMap() {

      entryEventMap.put("Tech Trails", (context) -> {
         new ForceLeaveEvent(
               "KEY",
               "You have made it to the tech trails, your key clicks smoothly unlocking the door. The room smells of dust and old wood. Snowshoes and skis are propped up neatly against the wall.\n",
               "You're facing an old building that reads Tech Trails. You try the door but it is locked tight. Snowshoes and skis are visible inside, but without the key, you need to turn back to wads.\n",
               "Wadsworth Hall", () -> {}
               ).apply(context);
               
      });

      entryEventMap.put("Under Statue", (context) -> {
         System.out.println(joeEventFinished);
         if (!joeEventFinished) {
            new ForceLeaveEvent(
               "CELLO",
               "“Yes... this'll do“ Joe says, plucking the cello from your hands. Before you can say a word, he claps his hands, disappearing in a puff of smoke. You are alone under the husky with the Mini Cooper, your only means of escape.",
               "You see Joe leaning against a Mini Cooper, his eyes sharp, hypnotically swirling his keys around his finger. “You want these keys? Then I'll need something in return,” he says. He examines you momentarily, then says: “Sorry kid, but we've got no deal today. You're missing… something” He waves his hand dismissively, and before you can protest, the world seems to twist around you. The next thing you know, you're standing back in the plaza, Joe's laughter still ringing in your ears.\n",
               "Plaza", 
               () -> {
                  joeEventFinished = true; 
                  context.getPlayer().getItem("CELLO");
               }
            ).apply(context);
         }
      });

      entryEventMap.put("Admin Building", (context) -> {
         eventMode = true;

         Player player = context.getPlayer();
         context.getOutputWriter().write(
               "Standing at the entrance of the admin building key room, you try the door, but it's locked. The keypad blinks mockingly at you, demanding the correct input.\n");
         context.getOutputWriter().write("ENTER CODE (exit to leave)");

         Runnable end = () -> {
            eventMode = false;
            if (player.getCurrentRoom().getName().equals("Admin Building")) {
               Command c = new LookCommand();
               try {
                  c.setArguments(new String[0]);
                  c.run(context);
               } catch (Exception e) {
                  e.printStackTrace();
               }
            }
         };
         
         event = (input) -> {
            input = input.trim().toUpperCase();

            if (input.equals("EXIT")) {
               context.getOutputWriter().write("Defeated, you return to the plaza.\n");
               player.setRoom(ForceLeaveEvent.getRoom(context.getRooms(), "Plaza"), context);
               end.run();
            } else if (input.equals("59923")) {
               context.getOutputWriter().write(
                     "The keypad beeps as you input the code. The key room door creaks open, revealing a golden key suspended from the ceiling. You grip your weapon tightly as you scan the room for threats\n");
               end.run();
            } else {
               if (containsNonDigit(input)) {
                  context.getOutputWriter().write("\"ERROR: DIGITS ONLY\"\n");
               } else if (input.length() != 5) {
                  context.getOutputWriter().write("\"ERROR: MUST BE 5 DIGITS\"\n");
               } else {
                  context.getOutputWriter().write("\"ERROR: INCORRECT CODE\"\n");
               }
               context.getOutputWriter().write("ENTER CODE (exit to leave)");
            }
         };
      });

      entryEventMap.put("Mt. Ripley", (context) -> {
         context.getOutputWriter().write("\nYOU WIN!!\n");
         context.win();
      });

      entryEventMap.put("DHH", (context) -> {

         Player player = context.getPlayer();

         if (!ForceLeaveEvent.containsName(player.getInventory(), "SHOVEL")) {
            context.getOutputWriter().write(
               "A man holding a shovel grins from a corner, offering a riddle: \"Answer this, and you may pass. Fail, and you won't leave...\" \n");
               context.getOutputWriter().write(" \"What... do computers eat?\" ");
               eventMode = true;
         }
         
         Runnable end = () -> {
            context.getOutputWriter().write(" \"Correct!\"\n");
            eventMode = false;
            Command c = new LookCommand();
            try {
               c.setArguments(new String[0]);
               c.run(context);
            } catch (Exception e) {
               e.printStackTrace();
            }
         };

         event = (input) -> {
            input = input.trim().toUpperCase();
            if (input.equals("CHIPS")) {
               end.run();
            }else {
               context.getOutputWriter().write(" \"WRONG!\"\n");
               context.getOutputWriter().write(" \"What... do computers eat?\" ");
            }
         };

      });
   }
}
