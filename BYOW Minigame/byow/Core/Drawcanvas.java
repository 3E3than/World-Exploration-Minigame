package byow.Core;

import byow.Core.RandomUtils;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class Drawcanvas {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    private long SEED;
    private Random rand;

    private StdDraw rworld;

    public Drawcanvas(int width, int height, long seed, StdDraw rworld) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.rworld = rworld;
        this.width = width;
        this.height = height;
        rworld.setCanvasSize(this.width*16, this.height*16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        rworld.setFont(font);
        rworld.setXscale(0, this.width);
        rworld.setYscale(0, this.height);
        rworld.clear(Color.BLACK);
        rworld.enableDoubleBuffering();
    }

    public void drawFrame(String s, Integer x, Integer y, Integer size, Boolean clear) {
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
    public String solicitNCharsInput() {
        String result = "";
        if (rworld.hasNextKeyTyped()== true) {
                result += String.valueOf(rworld.nextKeyTyped());
        }
        return result;
    }
    //draw frame of menu for game
    public void Gamemenu() {
        drawFrame("CS61B: THE GAME", width/2, height - 5, 35, true);
        drawFrame("New Game (N)", width/2, height - 15, 20, false);
        drawFrame("Load Game (L)", width/2, height - 17, 20, false);
        drawFrame("Quit (Q)", width/2,height - 19, 20, false);
    }
    //draw menu for floor
    public void floormenu() {
        drawFrame("Select Key Wall Tile", width/2, height - 5, 35, true);
        drawFrame("Default (D)", width/2, height - 13, 20, false);
        drawFrame("Grass (G)", width/2, height - 15, 20, false);
        drawFrame("Sand (S)", width/2, height - 17, 20, false);
        drawFrame("Water (W)", width/2,height - 19, 20, false);
    }
    //draw menu for menu
    public void wallmenu() {
        drawFrame("Select Key Wall Tile", width/2, height - 5, 35, true);
        drawFrame("Default (D)", width/2, height - 13, 20, false);
        drawFrame("Mountain (M)", width/2, height - 15, 20, false);
        drawFrame("Tree (T)", width/2, height - 17, 20, false);
        drawFrame("Blue Wall (W)", width/2,height - 19, 20, false);
    }
    public void main(long seed) {
        Drawcanvas game = new Drawcanvas(this.width, this.height, seed, rworld);
    }

}
