package io.techiies.memorynamegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

/**
 * Created by awing_000 on 4/23/2017.
 */

public class HardGameView extends View
{
    private Bitmap bitmap;

    public HardGameView(Context context)
    {
        super(context);
        bitmap = null;
    }

    public void setView(Student student)
    {
        if(student != null)
            bitmap = student.getFace();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if(bitmap != null)
            canvas.drawBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), null);
    }
}
