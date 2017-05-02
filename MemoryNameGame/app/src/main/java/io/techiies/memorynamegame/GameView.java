package io.techiies.memorynamegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

/**
 * Created by awing_000 on 4/23/2017.
 */

//Used to print the picture to the screen
public class GameView extends View
{
    public GameView(Context context)
    {
        super(context);
    }

    //Draws the picture to the screen if the image is not null
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
    }
}