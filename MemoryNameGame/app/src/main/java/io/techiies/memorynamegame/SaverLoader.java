package io.techiies.memorynamegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.FileOutputStream;

/**
 * Created by Richard on 4/26/17.
 */

public class SaverLoader {

    public static final int MODE_PRIVATE = 0;

    public SaverLoader() {

    }


    public boolean saveImageToInternalStorage(Context context, Student student) {

        try {
    // Use the compress method on the Bitmap object to write image to
    // the OutputStream
            FileOutputStream fos = context.openFileOutput(student.getName() + ".png", Context.MODE_PRIVATE);

    // Writing the bitmap to the output stream
            Bitmap image = student.getFace();
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            Log.i("savemessage", "was saved successfully");

            return true;
        } catch (Exception e) {
            Log.e("saveToInternalStorage()", e.getMessage());
            return false;
        }
    }



}
