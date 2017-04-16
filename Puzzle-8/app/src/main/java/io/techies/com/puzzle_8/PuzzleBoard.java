package io.techies.com.puzzle_8;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Richard on 4/3/17.
 */

public class PuzzleBoard {
    private static final int NUM_TILES = 3;     //Holds the number of tiles across the board
    private static final int[][] NEIGHBOUR_COORDS = {
            { -1, 0 },
            { 1, 0 },
            { 0, -1 },
            { 0, 1 }
    };

    //Holds the order of the tiles
    private ArrayList<PuzzleTile> tiles;

    private int steps;
    private PuzzleBoard previousBoard;

    //Constructor to create a new puzzle board
    PuzzleBoard(Bitmap bitmap, int parentWidth) {
        tiles = new ArrayList<>();
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, parentWidth, parentWidth, true);
        int chunkSizeWidth = parentWidth / NUM_TILES;   //The width of each tile
        int chunkSizeHeight = scaledBitmap.getHeight() / NUM_TILES; //The height of each tile
        int count = 0;  //Holds the number of tiles that have a picture on them
        //Creates the board so that 8 tiles have pictures and 1 is blank
        for(int x = 0; x < NUM_TILES; x++){
            for(int y = 0; y < NUM_TILES; y++){
                if((count) < NUM_TILES * NUM_TILES - 1) {
                    Bitmap tile = Bitmap.createBitmap(scaledBitmap, y * chunkSizeWidth, x * chunkSizeHeight, chunkSizeWidth, chunkSizeHeight);
                    PuzzleTile t = new PuzzleTile(tile, count);
                    tiles.add(t);   //Adds tile to list of tiles
                } else {
                    tiles.add(null);
                }
                count++;
            }
        }
    }

    //Copy Constructor///////////////////////////////////////////////////////////////////
    PuzzleBoard(PuzzleBoard otherBoard) {
        tiles = (ArrayList<PuzzleTile>) otherBoard.tiles.clone();

        steps = otherBoard.steps + 1;
        previousBoard = otherBoard;
    }

    //Resets the values of the board////////////////////////////////////////////////////
    public void reset() {
        // Nothing for now but you may have things to reset once you implement the solver.
        this.steps = 0;
        this.previousBoard = null;
    }

    //Checks to see if the boards are the same
    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        return tiles.equals(((PuzzleBoard) o).tiles);
    }

    //Draws the board
    public void draw(Canvas canvas) {
        if (tiles == null) {
            return;
        }
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                tile.draw(canvas, i % NUM_TILES, i / NUM_TILES);
            }
        }
    }

    //Checks to see if a tile was clicked
    public boolean click(float x, float y) {
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                if (tile.isClicked(x, y, i % NUM_TILES, i / NUM_TILES)) {
                    //Trys to move tile if it was clicked
                    return tryMoving(i % NUM_TILES, i / NUM_TILES);
                }
            }
        }
        return false;
    }

    //Trys to move tile if it was clicked
    private boolean tryMoving(int tileX, int tileY) {
        //Checks all adjacent tiles for the blank tile
        for (int[] delta : NEIGHBOUR_COORDS) {
            int nullX = tileX + delta[0];
            int nullY = tileY + delta[1];
            if (nullX >= 0 && nullX < NUM_TILES && nullY >= 0 && nullY < NUM_TILES &&
                    tiles.get(XYtoIndex(nullX, nullY)) == null) {
                //Swaps tiles if blank one is adjacent
                swapTiles(XYtoIndex(nullX, nullY), XYtoIndex(tileX, tileY));
                return true;
            }
        }
        return false;
    }

    //Checks to see if the  puzzle is solved
    public boolean resolved() {
        for (int i = 0; i < NUM_TILES * NUM_TILES - 1; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile == null || tile.getNumber() != i)
                return false;
        }
        return true;
    }

    //Turns the X- Y- coordinates of the tiles to the index of the tile
    private int XYtoIndex(int x, int y) {
        return x + y * NUM_TILES;
    }

    //Swaps the tiles
    protected void swapTiles(int i, int j) {
        PuzzleTile temp = tiles.get(i);
        tiles.set(i, tiles.get(j));
        tiles.set(j, temp);
    }

    public int getIndex(PuzzleTile tile)
    {
        for(int i = 0; i < 9; i++)
        {
            PuzzleTile temp = tiles.get(i);
            if(tile != null && temp != null)
                if(tile.getNumber() == temp.getNumber())
                    return i;
            if(temp == null)
                return i;
        }
        return -1;
    }

////////////////////////////////////////////////////////////////////////////////////////////////
    public ArrayList<PuzzleBoard> neighbors() {
        ArrayList<PuzzleBoard> boards = new ArrayList<>();
        PuzzleBoard t;
        PuzzleTile nullTile = null;

        int nullTileIndex = 0;

        int nullR = nullTileIndex % 3;
        int nullC = nullTileIndex / 3;

        for (int[] delta : NEIGHBOUR_COORDS) {
            int nX = nullC + delta[0];
            int nY = nullR + delta[1];

            if (nX >= 0 && nX < NUM_TILES && nY >= 0 && nY < NUM_TILES) {
                t = new PuzzleBoard(this);
                t.tryMoving(nX, nY);
                boards.add(t);
            }
        }

        return boards;
    }

//////////////////////////////////////////////////////////////////////////////////////
    public int priority() {
        int mPriority = 0;
        for(int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            if(tiles.get(i) != null) {
                int originalPlace = tiles.get(i).getNumber();
                int x = i / NUM_TILES;
                int y = i % NUM_TILES;
                int xOriginal = originalPlace / NUM_TILES;
                int yOriginal = originalPlace % NUM_TILES;

                mPriority += (Math.abs(x - xOriginal) + Math.abs(y - yOriginal));
            }
        }
        int manhattanDistance = mPriority + steps;
        return manhattanDistance;
    }

    //Shuffles the tiles so that the board is mixed up
    public void shuffle(int moves)
    {
        Random rand = new Random(); //Used to get random numbers
        int tilePos = 0;    //Holds the index of the blank tile
        boolean swappable = false;  //Indicates if two tiles can be swapped
        //Swaps tiles based on the number of moves
        for(int i = 0; i < moves; i++)
        {
            tilePos = 0;    //Starts at the first tile
            PuzzleTile tile = tiles.get(tilePos);
            //Looks for the blank tile
            while(tile != null)
            {
                tilePos++;
                tile = tiles.get(tilePos);
            }
            int newPos = 0; //Holds the index of the tile it will be swapped with
            //Continues to look for a tile to swap with until it finds one
            while(!swappable)
            {
                int nextTo = rand.nextInt(4);
                switch(nextTo)
                {
                    //Tile that is above the blank tile
                    case 0:
                        newPos = tilePos - 3;
                        //If there is a tile above the blank tile then they can be swapped
                        if(newPos >= 0)
                            swappable = true;
                        break;
                    //Tile to the right of the blank tile
                    case 1:
                        newPos = tilePos + 1;
                        //If there is a tile to the right of the blank tile then they can be swapped
                        if(newPos % 3 != 0 && newPos <= 8)
                            swappable = true;
                        break;
                    //Tile that is below the blank tile
                    case 2:
                        newPos = tilePos + 3;
                        //If there is a tile below the blank tile then they can be swapped
                        if(newPos <= 8)
                            swappable = true;
                        break;
                    //Tile that is to the left of the blank tile
                    case 3:
                        newPos = tilePos - 1;
                        //If there is a tile to the left of the blank tile then they can be swapped
                        if(newPos % 3 != 2 && newPos >= 0)
                            swappable = true;
                        break;
                }
            }
            swapTiles(tilePos, newPos); //Swap the tiles
            swappable = false;  //Reset to false to look for a new tile
        }
    }

    public ArrayList<PuzzleTile> getTiles() {return tiles;}

    public void setTiles(ArrayList<PuzzleTile> t) {tiles = t;}
}