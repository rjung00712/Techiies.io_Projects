package io.techies.com.puzzle_8;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Richard on 4/3/17.
 */

public class PuzzleTile {

    private Bitmap bitmap;  //Holds the picture that's on the tile
    private int number;     //Holds the number of the tile

    //Constructor for the tile
    public PuzzleTile(Bitmap bitmap, int number){
        this.bitmap = bitmap;
        this.number = number;
    }

    //Returns the number of the tile
    public int getNumber() {
        return number;
    }

    //Draws the tile
    public void draw(Canvas canvas, int x, int y) {
        canvas.drawBitmap(bitmap, x * bitmap.getWidth(), y * bitmap.getHeight(), null);
    }

    //Checks to see if the tile is clicked
    public boolean isClicked(float clickX, float clickY, int tileX, int tileY) {
        int tileX0 = tileX * bitmap.getWidth();
        int tileX1 = (tileX + 1) * bitmap.getWidth();

        int tileY0 = tileY * bitmap.getWidth();
        int tileY1 = (tileY + 1) * bitmap.getWidth();
        return (clickX >= tileX0) && (clickX < tileX1) && (clickY >= tileY0) && (clickY < tileY1);
    }
}