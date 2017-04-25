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
    private Bitmap bitmap;  //Holds the image of the student being the shown

    public GameView(Context context)
    {
        super(context);
        bitmap = null;  //Initializes the image to null
    }

    //Sets the image to teh student's image and prints the screen
    public void setView(Student student)
    {
        if(student != null) //Make sure that the student is not null
        {
            bitmap = student.getFace();
            invalidate();
        }
    }

    //Draws the picture to the screen if the image is not null
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if(bitmap != null)
            canvas.drawBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), null);
    }
}