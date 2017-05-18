package cs499android.com.cppmapbox;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class MarkerSelected extends AppCompatActivity
{
    private TextToSpeech textToSpeech;
    private String title;
    private String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_selected);
        title = getIntent().getExtras().getString("Title");
        description = getIntent().getExtras().getString("Description");
        EditText editText = (EditText) findViewById(R.id.title);
        editText.setText(title);
        editText = (EditText) findViewById(R.id.description);
        editText.setText(getDescription(description));
        editText.setAutoLinkMask(Linkify.PHONE_NUMBERS);
        setPicture(description);
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.speak(getDescription(description), TextToSpeech.QUEUE_FLUSH, null);
            }
        });
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
        if(textToSpeech.isSpeaking()) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        finish();
    }

    public void navigate(View view)
    {
        if(textToSpeech.isSpeaking()) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        Intent NavigationIntent = new Intent(MarkerSelected.this, NavigationActivity.class);
        startActivity(NavigationIntent);
        finish();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if(textToSpeech.isSpeaking()) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.speak(getDescription(description), TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }
}
