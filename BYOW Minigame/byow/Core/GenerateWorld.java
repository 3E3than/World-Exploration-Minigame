package byow.Core;
//import packages needed

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.*;
import java.io.IOException;
import java.util.Random;

import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.Stopwatch;
import edu.princeton.cs.introcs.Out;


public class GenerateWorld {
    //width of tiles
    private static final int WIDTH = 50;
    //height of tiles
    private static final int HEIGHT = 40;
    //random seed 
    private static long SEED;
    private static Random RANDOM;
    //to render the world
    static TERenderer ter = new TERenderer();
    //tile for floor
    private static TETile floor;

    //tile for wall
    private static TETile wall;
    // the world
    public static TETile [][] finaltiles;
    //Position of avatar
    public static Position avatar;
    // number of challenges
    private static int challenges = 20;
    private static String last = "";
    public static String load ="";
    public static String finalload;
    public static Stopwatch sw;

   
    //position class to know positions of rooms,hallways,avatar, etc
    public static class Position {
        int x;
        int y;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Position shift(int dx, int dy) {
            return new Position((this.x + dx), (this.y + dy));
        }
    }

    //room class to create rooms
    private static class Room {
        
        //keep track of corners of room
        private Position top_right;
        private Position bot_right;
        private Position top_left;
        private Position bot_left;

        private TETile[][] tiles;

        //create room 
        Room(TETile[][] tiles, Position p, int height, int width) {
            Position position = p;
            //edge cases so it does not go out of bounds
            if (p.y - height < 0) {
               position = position.shift(0, -1 * (p.y - height));
            }
            if (height < 4 || width < 4) {
                height +=4 ;
                width += 4;
            }
            if ((p.x + width) > (WIDTH - 1)) {
                width -= (p.x + width) - (WIDTH - 1);
            }
            Position pos = position;
            for (int i = 0; i < height; i++) {
                if (i == 0 || i == height - 1) {
                    pos = pos.shift(0, -1);
                    //draw top or bottom of room
                    drawtoporbottom(tiles, pos, width);
                } else {
                    //draw edges of room
                    pos = pos.shift(0, -1);
                    drawedge(tiles, pos, width);
                }
            }

            //update new positions 
            top_left = position;
            top_right = position.shift(width, 0);
            bot_right = position.shift(width, -height);
            bot_left = position.shift(0, -height);
            this.tiles = tiles;
        }
    }

    //Hallway class (connect rooms )
    public static class Hallway {
        private Position top_right;
        private Position bot_right;
        private Position top_left;
        private Position bot_left;
        private TETile[][] tiles;

        Hallway(TETile[][] tiles, Position p, int height, int width) {
            if (p.y - height < 0) {
                height += p.y - height;
            }
            if ((p.x + width) > (WIDTH - 1)) {
                width -= (p.x + width) - (WIDTH - 1);
            }
            top_left = p;
            top_right = p.shift(width, 0);
            bot_right = p.shift(width, -height);
            bot_left = p.shift(0, -height);
            Position position = p;
            for (int i = 0; i < height; i++) {
                if (i == 0 || i == height - 1) {
                    position = position.shift(0, -1);
                    drawtoporbottom(tiles,position, width);
                } else {
                    position = position.shift(0, -1);
                    drawedge(tiles, position, width);
                }
            }

        }
    }

    //draw top of room/hallway
    public static void drawtoporbottom(TETile[][] tiles,Position p, int width) {
        for (int dx = 0; dx < width; dx++) {
            if (tiles[p.x + dx][p.y] == floor) {
                tiles[p.x + dx][p.y] = floor;
            } else {
                tiles[p.x + dx][p.y] = wall;
            }
        }
    }

    //draw sides of room/hallway; draw wall if at sides and draw floor in middle
    public static void drawedge(TETile[][] tiles,Position p, int width) {
        for (int dx = 0; dx < width; dx++) {
            if (dx == 0 || dx == width - 1) {
                if (tiles[p.x + dx][p.y] == floor) {
                    tiles[p.x + dx][p.y] = floor;
                } else {
                    tiles[p.x + dx][p.y] = wall;
                }
            } else {
                tiles[p.x + dx][p.y] = floor;
            }
        }
    }

    // checks if two rooms overlap horizontally; returns coordinate from where horizontal hallway should be created(floor tile)
    public static void link_rooms(Room a, Room b) {
        //check overlap vertical/horizontal first
        Position p = new Position(a.top_right.x - b.top_right.x, a.top_right.y - b.top_right.y);
        Hallway horizontal = null;
        if (p.x >= 0) {
            horizontal = new Hallway (a.tiles, b.top_right.shift(-2,0),3, a.bot_right.x  +  2  -  b.top_right.x );
        }
        if (p.x < 0) {
            Position left = new Position(a.bot_left.x, b.top_left.y );
            horizontal = new Hallway (a.tiles, left,3, b.top_left.x - left.x + 4 );
        }

        if (p.y > 0) {
            Position top = new Position(a.top_left.x, a.bot_right.y + 2 );
            Hallway vertical = new Hallway( a.tiles, top, top.y - horizontal.bot_right.y , 3);
        }
        if (p.y <=  0) {
            Position bot = new Position(a.top_left.x, horizontal.top_right.y );
            Hallway vertical = new Hallway( a.tiles, bot, bot.y - a.top_left.y + 2 , 3);
        }
    }

    //needed to instantiate tiles in order to have a "background"
    public static void fillBoardWithNothing(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }
    
    // draw world when the seed is inputed in main methods
    public static TETile[][]  drawWorld(TETile [][] tiles, Position p, int height, int width, int seed) {
        fillBoardWithNothing(tiles);
        Random m = new Random(seed);
        Position C = new Position(WIDTH / 2, HEIGHT / 2);
        Room center = new Room(tiles, C, height, width);
        //random number of rooms made
        int num_rooms = m.nextInt(5) + 10;
        Room previous = center;
        for (int i = 1; i < num_rooms; i++) {
            Position pcurrent = new Position(m.nextInt(WIDTH - 5), m.nextInt(HEIGHT - 1));
            Room rcurrent = new Room(tiles, pcurrent, m.nextInt(3) + 5, m.nextInt(3) + 5);
            link_rooms(rcurrent, previous);
            previous = rcurrent;
        }
        return tiles;
    }

    /** AVATAR METHODS*/
    //  make entrance 
    public static Position entrance(TETile[][] tiles) {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y <HEIGHT; y += 1) {
                if (tiles[x][y] == wall) {
                    return new Position(x,y);
                }
            }
        }
        return null;
    }
    
    //put avatar on tile entrance
    public static void putavatar(TETile[][] tiles, boolean loading) {
        Position p = entrance(tiles);
        int x = p.x;
        int y =p.y;
        tiles[x][y+1] = Tileset.UNLOCKED_DOOR;
        tiles[x+1][y+1] = Tileset.AVATAR;
        avatar = new Position(x+1, y+1);
        if (loading == false) {
            ter.renderFrame(tiles);
        }
    }
    
    //move avatar left
    public static void avatarleft(TETile[][] tiles, boolean loading) {
        if (tiles[ avatar.x - 1][avatar.y] == floor|| tiles[avatar.x - 1][avatar.y] == Tileset.FLOWER) {
            if (tiles[avatar.x - 1][avatar.y] == Tileset.FLOWER) {
                challenges =  challenges - 1;
                drawline(challenges + " "+ "flowers" + " " + "left" ,5, 1, 15, false);
                cleardraw(challenges + " "+ "flowers" + " " + "left", 5, 1, 15);
            }
            tiles[ avatar.x][avatar.y] = floor;
            avatar = avatar.shift(-1, 0);
            tiles[avatar.x][avatar.y] = Tileset.AVATAR;
            if (loading == false) {
                ter.renderFrame(tiles);
            }
            drawline(challenges + " "+ "flowers" + " " + "left",5, 1, 15, false);
        }
    }
    
    //move avatar right
    public static void avatarright(TETile[][] tiles, boolean loading) {
        if (tiles[avatar.x + 1][avatar.y] == floor|| tiles[avatar.x + 1][avatar.y] == Tileset.FLOWER) {
            if (tiles[avatar.x + 1][avatar.y] == Tileset.FLOWER) {
                challenges =  challenges - 1;
                drawline(challenges + " "+ "flowers" + " " + "left",5, 1, 15, false);
                cleardraw(challenges + " "+ "flowers" + " " + "left", 5, 1, 15);
            }
            tiles[avatar.x][avatar.y] = floor;
            avatar = avatar.shift(1, 0);
            tiles[avatar.x][avatar.y] = Tileset.AVATAR;
            if (loading == false) {
                ter.renderFrame(tiles);
            }
            drawline(challenges + " "+ "flowers" + " " + "left",5, 1, 15, false);
        }
    }
    
    //move avatar up
    public static void avatarup(TETile[][] tiles, boolean loading) {
        if (tiles[avatar.x][avatar.y + 1] == floor|| tiles[avatar.x][avatar.y + 1] == Tileset.FLOWER) {
            if (tiles[avatar.x][avatar.y + 1] == Tileset.FLOWER) {
                challenges =  challenges - 1;
                drawline(challenges + " "+ "flowers" + " " + "left",5, 1, 15, false);
                cleardraw(challenges + " "+ "flowers"+ " " + "left", 5, 1, 15);
            }
            tiles[avatar.x][avatar.y] = floor;
            avatar = avatar.shift(0,1);
            tiles[avatar.x][avatar.y] = Tileset.AVATAR;
            if (loading == false) {
                ter.renderFrame(tiles);
            }
            drawline(challenges + " "+ "flowers" + " " + "left",5, 1, 15, false);
        }
    }
    
    //move avatar down 
    public static void avatardown(TETile[][] tiles, boolean loading) {
        if (tiles[avatar.x][avatar.y - 1] == floor || tiles[avatar.x][avatar.y - 1] == Tileset.FLOWER) {
            if (tiles[avatar.x][avatar.y - 1] == Tileset.FLOWER) {
                challenges =  challenges - 1;
                drawline(challenges + " "+ "flowers" + " " + "left",5, 1, 15, false);
                cleardraw(challenges + " "+ "flowers" + " " + "left", 5, 1, 15);
            }
            tiles[avatar.x][avatar.y] = floor;
            avatar = avatar.shift(0,-1);
            tiles[avatar.x][avatar.y] = Tileset.AVATAR;
            if (loading == false) {
                ter.renderFrame(tiles);
            }
            //this is to have the words on bottom to see how many flowers are left
            drawline(challenges + " "+ "flowers"+ " " + "left",5, 1, 15, false);
        }
    }

    //this is to be able to display words
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
    
    //clear the words drawn beforehand
    public static void cleardraw(String current, Integer x, Integer y, Integer size) {
        /* Take the input string S and display it at the center of the screen,
         * with the pen settings given below. */
        StdDraw.setPenColor(Color.BLACK);
        Font fontBig = new Font("Monaco", Font.BOLD, size);
        StdDraw.text(x, y, last);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBi = new Font("Monaco", Font.BOLD, size);
        StdDraw.setFont(fontBi);
        StdDraw.text(x, y, current);
        last = current;
        StdDraw.show();
    }
    
    /** MENU ITEMS*/
    // draw menu items for the begining screen 
    public static void floormenu() {
        drawline("Select Floor Tile", WIDTH / 2, HEIGHT - 5, 35, true);
        drawline("Default (D)", WIDTH / 2, HEIGHT - 13, 20, false);
        drawline("Grass (G)", WIDTH / 2, HEIGHT - 15, 20, false);
        drawline("Sand (S)", WIDTH / 2, HEIGHT - 17, 20, false);
        drawline("Water (W)", WIDTH / 2, HEIGHT - 19, 20, false);
        while (true) {
            if (StdDraw.hasNextKeyTyped() == true) {
                char curr = StdDraw.nextKeyTyped();
                if (curr == 'D' || curr == 'd') {
                    floor = Tileset.FLOOR;
                    load = load + "D";
                    break;
                }
                if (curr == 'G' || curr == 'g') {
                    floor = Tileset.GRASS;
                    load = load + "G";
                    break;
                }
                if (curr == 'S' || curr == 's') {
                    floor = Tileset.SAND;
                    load += "S";
                    break;
                }
                if (curr == 'W' || curr == 'w') {
                    floor = Tileset.WATER;
                    load += "W";
                    break;
                }
            }
        }
    }
    
    //Set wall menu with options for World building and then make walls with the desired aesthetic option
    public static void wallmenu() {
        drawline("Select Wall Tile", WIDTH / 2, HEIGHT - 5, 35, true);
        drawline("Default (D)", WIDTH/2, HEIGHT  - 13, 20, false);
        drawline("Mountain (M)", WIDTH/2, HEIGHT  - 15, 20, false);
        drawline("Tree (T)", WIDTH/2, HEIGHT  - 17, 20, false);
        drawline("Blue Wall (W)", WIDTH/2,HEIGHT  - 19, 20, false);
        while (true) {
            if (StdDraw.hasNextKeyTyped() == true) {
                char curr = StdDraw.nextKeyTyped();
                if (curr == 'D' || curr == 'd') {
                    wall = Tileset.WALL;
                    load = load + "D";
                    break;
                }
                if (curr == 'M' || curr == 'm') {
                    wall = Tileset.MOUNTAIN;
                    load += "M";
                    break;
                }
                if (curr == 'T' || curr == 't') {
                    wall = Tileset.TREE;
                    load += "T";
                    break;
                }
                if (curr == 'W' || curr == 'w') {
                    wall = Tileset.BLUEWALL;
                    load += "W";
                    break;
                }
            }
        }
    }

    //place flower 
    public static void place(TETile[][] tiles) {
        int placed = 0;
        while (placed < challenges) {
            int width = RANDOM.nextInt(WIDTH);
            int height = RANDOM.nextInt(HEIGHT);
            if (tiles[width][height] == floor) {
                tiles[width][height] = Tileset.FLOWER;
                placed += 1;
            }
        }
    }

    //this allows to move the avatar by using the WASD keys
    public static void keyboardmove(TETile[][] tiles, char curr, boolean loading) throws IOException {
        if (curr == 'W'|| curr== 'w') {
            avatarup(tiles, loading);
            load = load + "W";
        }
        if (curr == 'A'|| curr == 'a') {
            avatarleft(tiles, loading);
            load = load + "A";
        }
        if (curr == 'S'|| curr == 's') {
            avatardown(tiles, loading);
            load = load + "S";
        }
        if (curr == 'D'|| curr == 'd') {
            avatarright(tiles, loading);
            load = load + "D";
        }
        if (curr == ':') {
            boolean cont = true;
            while (cont) {
                if (StdDraw.hasNextKeyTyped()== true) {
                    char next = StdDraw.nextKeyTyped();
                    if (next == 'Q'|| next == 'q') {
                        load = load + sw.elapsedTime();
                        String DATA = "/Users/biancapoblano/Desktop/fa22-proj3-g316/proj3/byow/Core/Data.txt";
                        Out out = new Out(DATA);
                        out.println(load);
                        out.close();
                        System.exit(0);
                    }
                    cont = false;
                }
            }
        }
    }
    
    // when pressing on mouse, it gives a description of what it is pressing , ex. wall,floor, avatar 
    public static void mousepressed (TETile[][] tiles) {
        String curr;
        Integer x = Integer.valueOf((int) StdDraw.mouseX());
        Integer y = Integer.valueOf((int) StdDraw.mouseY());
        if( x > WIDTH - 1|| x < 0 || y < 2 || y > HEIGHT - 1) {
            curr = "nothing";
        } else {
            curr = tiles[x][y - 2].description();
        }
        cleardraw(curr, WIDTH - 5, 1, 15);
    }

    // makes the wall tile the chosen aesthetic
    public static TETile convertwall(Character wall)  {
        if (wall == 'D') {
            return Tileset.WALL;
        }
        if (wall == 'M') {
            return Tileset.MOUNTAIN;
        }
        if (wall == 'T') {
            return Tileset.TREE;
        }
        if (wall == 'W') {
            return Tileset.BLUEWALL;
        }
        return null;
    }
    
    // makes the floor  tile the chosen aesthetic
    public static TETile convertfloor(char floor)  {
        if (floor == 'D') {
            return Tileset.FLOOR;
        }
        if (floor == 'G') {
            return Tileset.GRASS;
        }
        if (floor == 'S') {
            return Tileset.SAND;
        }
        if (floor == 'W') {
            return Tileset.WATER;
        }
        return null;
    }

    //main makes the actual world
    public static void main(String [] args) throws IOException {

        //INTIALIZE THE CANVAS AND VARIABLES
        ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT, 0, 2);
        load = args[0];
        Integer seed = Integer.valueOf(args[0]);
        RANDOM = new Random(seed);
        challenges = 20;

        //INTIALIZE WALL AND FLOOR TILES
        //find the wall and floor
        wall = convertwall(args[1].charAt(0));
        load = load + args[1];
        floor = convertfloor(args[2].charAt(0));
        load = load + args[2];

        //DRAW THE WORLD
        TETile[][] tiles = new TETile[WIDTH][HEIGHT];
        fillBoardWithNothing(tiles);
        Position anchor = new Position(RANDOM.nextInt(WIDTH - 2), RANDOM.nextInt(HEIGHT));
        TETile[][] ans =  drawWorld(tiles, anchor, RANDOM.nextInt(6) + 4, RANDOM.nextInt(6) + 4, seed);

        //LOAD IF NEED TO LOAD : (if the world was saved previously
        if (args[4] != null && args[3] != "") {
            putavatar(tiles, false);
            place(tiles);
            char[] loading = args[3].toCharArray();
            for (int i = 0; i < loading.length; i++) {
                StdDraw.pause(100);
                keyboardmove(tiles, loading[i], false);
            }
        }
         else if (args[4] == null && args[3] != "" ) {
            putavatar(tiles, true);
            place(tiles);
            char[] loading = args[3].toCharArray();
            for (int i = 0; i < loading.length; i++) {
                keyboardmove(tiles, loading[i], true);
            }
            ter.renderFrame(tiles);
             
        // RENDER NEW WORLD IF NOT LOADING
        } else {
            putavatar(tiles, false);
            place(tiles);
            ter.renderFrame(tiles);
        }
        sw = new Stopwatch();
        
        //PLAYING GAME AFTER LOADING/NOTLOADING
        while (true) {
            if (StdDraw.hasNextKeyTyped()== true) {
                char current = StdDraw.nextKeyTyped();
                keyboardmove(tiles, current, false);
                if (challenges == 0) {
                    double timeInSeconds = 0.00;
                    if (args[5] != "" &&  args[5] != null) {
                        timeInSeconds = Double.valueOf(args[5]) + sw.elapsedTime();
                    } else {
                        timeInSeconds = sw.elapsedTime();
                    }
                    drawline("Completed in " + timeInSeconds +" seconds" , WIDTH/2, HEIGHT/2, 40, true);
                    StdDraw.pause(1000);
                    System.exit(0);
                }
            }
            mousepressed(tiles);
        }
    }
}
