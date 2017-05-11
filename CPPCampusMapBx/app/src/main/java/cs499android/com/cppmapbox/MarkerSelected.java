package cs499android.com.cppmapbox;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

public class MarkerSelected extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_selected);
        String title = getIntent().getExtras().getString("Title");
        String description = getIntent().getExtras().getString("Description");
        EditText editText = (EditText) findViewById(R.id.title);
        editText.setText(title);
        editText = (EditText) findViewById(R.id.description);
        editText.setText(getDescription(description));
        setPicture(description);
    }

    public void setPicture(String description)
    {
        String filename = description.substring(description.indexOf("***") + 3);
        try {
            InputStream is = getAssets().open(filename);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();
            ((ImageView)findViewById(R.id.imageView)).setImageBitmap(bitmap);
        }catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public String getDescription(String description)
    {
        return description.substring(0, description.indexOf("***"));
    }

    public void cancel(View view)
    {
        finish();
    }

    public void navigate(View view)
    {

    }
}
