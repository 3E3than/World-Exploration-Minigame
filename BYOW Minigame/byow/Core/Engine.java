package byow.Core;
import byow.Core.RandomUtils;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.Random;
import java.util.*;

import static java.lang.Integer.valueOf;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 50;
    public static final int HEIGHT = 40;
    public static long SEED;

    private static Random RANDOM;
    private static Drawcanvas game;
    private static GenerateWorld world;
    private static TETile[][] tiles;
    private static String floor;
    private static String wall;
    private static String load = "";

    private static boolean gameover = false;



    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() throws IOException {
        StdDraw.setCanvasSize(WIDTH*16, HEIGHT*16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0,HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        Gamemenu();
        while (!gameover) {
            if (StdDraw.hasNextKeyTyped() == true) {
                char curr = StdDraw.nextKeyTyped();
                if (curr == 'N' || curr == 'n') {
                    String[] args = new String[6];
                    String seed = getseed();
                    args[0] = seed;
                    args[1] = Wallmenu();
                    args[2] = Floormenu();
                    Goalmenu();
                    args[3]= load;
                    args[4] = null;
                    args[5] = null;
                    GenerateWorld world = new GenerateWorld();
                    world.main(args);
                }
                if (curr == 'L' || curr == 'l') {
                    GenerateWorld world = new GenerateWorld();
                    String[] args = new String[6];
                    In graphfiles = new In("/Users/biancapoblano/Desktop/fa22-proj3-g316/proj3/byow/Core/Data.txt");
                    //find the seed
                    String digits = "";
                    int digit = 0;
                    while (!graphfiles.isEmpty()) {
                        String line = graphfiles.readString();
                        char[] loading = line.toCharArray();
                        for (int i = 0; i <loading.length; i++) {
                            if (Character.isDigit(loading[i])) {
                                digits = digits + loading[i];
                                digit += 1;
                            } else {
                            break;
                        }
                        }
                        String time  = "";
                        Integer tiempo = 0;
                        for (int i = digit; i <loading.length; i++) {
                            if (Character.isDigit(loading[i])|| loading[i] == '.') {
                                time = time + loading[i];
                                tiempo += 1;
                            }
                        }
                        args[0] = digits;
                        args[1] = String.valueOf(loading[digit]);
                        args[2] = String.valueOf(loading[digit+1]);
                        args[3] = line.substring(digit + 2, line.length() - tiempo);
                        args[4]= null;
                        args[5]= time;
                    }
                    world.main(args);
                }
                if (curr == 'R' || curr == 'r') {
                    GenerateWorld world = new GenerateWorld();
                    String[] args = new String[6];
                    In graphfiles = new In("/Users/biancapoblano/Desktop/fa22-proj3-g316/proj3/byow/Core/Data.txt");
                    //find the seed
                    String digits = "";
                    int digit = 0;
                    while (!graphfiles.isEmpty()) {
                        String line = graphfiles.readString();
                        char[] loading = line.toCharArray();
                        for (int i = 0; i <loading.length; i++) {
                            if (Character.isDigit(loading[i])) {
                                digits = digits + loading[i];
                                digit += 1;
                            } else {
                                break;
                            }
                        }
                        String time  = "";
                        Integer tiempo = 0;
                        for (int i = digit; i <loading.length; i++) {
                            if (Character.isDigit(loading[i]) || loading[i] == '.') {
                                time = time + loading[i];
                                tiempo += 1;
                                System.out.println(time);
                            }
                        }
                        args[0] = digits;
                        args[1] = String.valueOf(loading[digit]);
                        args[2] = String.valueOf(loading[digit+1]);
                        args[3] = line.substring(digit + 2, line.length() - tiempo);
                        args[4]= "t";
                        args[5]= time;
                    }
                    world.main(args);
                }
                if (curr == 'Q' || curr == 'q') {
                    gameover = true;
                    System.exit(0);
                }
            }
        }
    }
    //DRAW LINE
    public static void drawline(String s, Integer x, Integer y, Integer size, Boolean clear) {
        /* Take the input string S and display it at the center of the screen,
         * with the pen settings given below. */
        if (clear) {
            StdDraw.clear(Color.BLACK);
        }
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, size);
        StdDraw.setFont(fontBig);
        StdDraw.text(x, y, s);
        StdDraw.show();
    }

    public void Gamemenu() {
        drawline("CS61B: THE GAME",WIDTH/2, HEIGHT - 12, 60, true);
        drawline("New Game (N)", WIDTH/2, HEIGHT - 17, 20, false);
        drawline("Load Game (L)", WIDTH/2, HEIGHT - 19, 20, false);
        drawline("Replay Game (R)", WIDTH/2, HEIGHT - 21, 20, false);
        drawline("Quit (Q)", WIDTH/2,HEIGHT - 23, 20, false);
    }
        //“avatar” that can moved around using the W, A, S, and D keys.
    //gets the seed that people type in
    public String getseed() {
        drawline("TYPE INTEGERS TO CREATE WORLD", WIDTH/2, HEIGHT - 10, 40, true);
        drawline("type S when finished entering integersn", WIDTH/2, HEIGHT - 13, 15, false);
        drawline("type Q to quit", WIDTH/2, 5, 20, false);
        String a = "";
        boolean cont= true;
        int numdigits = 0;
        while (cont) {
            if (StdDraw.hasNextKeyTyped()== true) {
                char typedin = StdDraw.nextKeyTyped();
                if (typedin == 'Q'|| typedin == 'q') {
                    System.exit(0);
                }
                if (Character.isDigit(typedin)) {
                    a += String.valueOf(typedin);
                    drawline(String.valueOf(typedin), WIDTH/2 - 1 + numdigits, HEIGHT - 20, 30, false);
                    numdigits += 1;
                }
                if (typedin == 'S'|| typedin == 's') {
                    cont = false;
                }
            }
        }
        return a;
    }
    public static String Floormenu() {
        drawline("Select Floor Tile", WIDTH / 2, HEIGHT - 10, 40, true);
        drawline("Default (D)", WIDTH / 2, HEIGHT - 15, 20, false);
        drawline("Grass (G)", WIDTH / 2, HEIGHT - 17, 20, false);
        drawline("Sand (S)", WIDTH / 2, HEIGHT - 19, 20, false);
        drawline("Water (W)", WIDTH / 2, HEIGHT - 21, 20, false);
        while (true) {
            if (StdDraw.hasNextKeyTyped() == true) {
                char curr = StdDraw.nextKeyTyped();
                if (curr == 'D' || curr == 'd') {
                    return "D";
                }
                if (curr == 'G' || curr == 'g') {
                    return "G";
                }
                if (curr == 'S' || curr == 's') {
                    return "S";
                }
                if (curr == 'W' || curr == 'w') {
                    return "W";
                }
            }
        }
    }

    public static String Wallmenu() {
        drawline("Select Wall Tile", WIDTH / 2, HEIGHT - 10, 40, true);
        drawline("Default (D)", WIDTH/2, HEIGHT  - 15, 20, false);
        drawline("Mountain (M)", WIDTH/2, HEIGHT  - 17, 20, false);
        drawline("Tree (T)", WIDTH/2, HEIGHT  - 19, 20, false);
        drawline("Blue Wall (W)", WIDTH/2,HEIGHT  - 21, 20, false);
        while (true) {
            if (StdDraw.hasNextKeyTyped() == true) {
                char curr = StdDraw.nextKeyTyped();
                if (curr == 'D' || curr == 'd') {
                    return "D";
                }
                if (curr == 'M' || curr == 'm') {
                    return "M";
                }
                if (curr == 'T' || curr == 't') {
                    return "T";
                }
                if (curr == 'W' || curr == 'w') {
                    return "W";
                }
            }
        }
    }
    public static void Goalmenu() {
        drawline("Mission: Collect the flowers", WIDTH / 2, HEIGHT - 10, 40, true);
        drawline("as fast as possible", WIDTH / 2, HEIGHT - 13, 40, false);
        drawline("CONTROLS", WIDTH/2, HEIGHT  - 17, 30, false);
        drawline("Up (W)", WIDTH/2, HEIGHT  - 19, 20, false);
        drawline("Down (S)", WIDTH/2, HEIGHT  - 21, 20, false);
        drawline("Left (A)", WIDTH/2, HEIGHT  - 23, 20, false);
        drawline("Right (D)", WIDTH/2, HEIGHT  - 25, 20, false);
        drawline("Press :Q to save game progress during play", WIDTH / 2, 5, 20, false);
        StdDraw.pause(3000);
    }


    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, running both of these:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().

        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        String[] s = input.split("");
        String num = "";
        for (int i = 1; i < s.length - 1; i++) {
            num += s[i];
        }
        this.SEED =  Long.parseLong(num);
        GenerateWorld world = new GenerateWorld();
        TETile[][] finalWorldFrame = world.finaltiles;
        return finalWorldFrame;
    }
}
