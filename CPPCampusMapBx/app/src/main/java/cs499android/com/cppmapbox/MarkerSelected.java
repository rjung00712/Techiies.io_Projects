package cs499android.com.cppmapbox;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mapbox.services.commons.models.Position;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class MarkerSelected extends AppCompatActivity
{
    private TextToSpeech textToSpeech;  //Allows for the descriptions to be spoken
    private String title;   //Name of the location
    private String description;     //Description of the location
    private String snippet; //Holds the description and the name of the picture that will be shown for the location
    private String type;    //Whether the marker being shown was a marker clicked by the user, or a nearby marker

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_selected);
        title = getIntent().getExtras().getString("Title");
        snippet = getIntent().getExtras().getString("Description");
        type = getIntent().getExtras().getString("Type");
        EditText editText = (EditText) findViewById(R.id.title);
        editText.setText(title);    //Shows the name of the location
        editText = (EditText) findViewById(R.id.description);
        description = getDescription(snippet);  //Get just the desription from the snippet
        //If the type is of a nearby location, then add a couple extra lines to the description
        if(type.equals("Nearby"))
        {
            description = "You are nearby " + title + "! " + description + "\nWould you like to go here?";
        }
        editText.setText(description);  //Show the description
        editText.setAutoLinkMask(Linkify.PHONE_NUMBERS);    //Makes the phone numbers clickable
        setPicture();   //Sets the picture of the location
        setButtons();   //Sets the correct button labels based on the type
        //Speaks the description if the user has that setting on
        if(StaticVariables.speakDescriptions) {
            textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.speak(description, TextToSpeech.QUEUE_FLUSH, null);
                }
            });
        }
    }

    //Sets the picture of the location
    public void setPicture()
    {
        String filename = snippet.substring(snippet.indexOf("***") + 3);    //Gets the name of the image from the snippet
        try {
            InputStream is = getAssets().open(filename);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();
            ((ImageView)findViewById(R.id.imageView)).setImageBitmap(bitmap);   //Sets the image to the view
        }catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    //Sets the labels of the buttons according to the type
    public void setButtons()
    {
        Button buttonL = (Button)findViewById(R.id.buttonL);
        Button buttonR = (Button)findViewById(R.id.buttonR);
        //User clicked on a marker and has not started navigating yet
        if(type.equals("Navigate"))
        {
            buttonL.setText("Get Directions");
            buttonR.setText("Cancel");
            //Starts the navigation when the button is clicked
            buttonL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigate(v);
                }
            });
            //Goes back to the map when the button is clicked
            buttonR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancel(v);
                }
            });
        }
        //A nearby location is shown to the user
        else if(type.equals("Nearby"))
        {
            buttonL.setText("Go Here Instead");
            buttonR.setText("Go Back");
            //Sets this location as the new destination
            buttonL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goHere(v);
                }
            });
            //Goes back to the navigation with the old destination
            buttonR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goBack(v);
                }
            });
        }
    }

    //Returns just the description of the location from the snippet
    public String getDescription(String description)
    {
        return description.substring(0, description.indexOf("***"));    //Removes the image name
    }

    //Stops the text that is being spoken (if any)
    private void stopSpeaking()
    {
        if(StaticVariables.speakDescriptions) {
            if (textToSpeech.isSpeaking()) {
                textToSpeech.stop();
                textToSpeech.shutdown();
            }
        }
    }

    //Returns the main map
    public void cancel(View view)
    {
        //Stops the text that is being spoken
        stopSpeaking();
        finish();
    }

    //Checks if the user has Location Setting enabled
    public void navigate(View view)
    {
        //Stops the text that is being spoken
        stopSpeaking();
        checkSettings();
    }

    //Checks if the user has Location Settings enabled
    //Starts navigation it the settings are enabled
    //Notifies the user if the settings are disabled
    private void checkSettings()
    {
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = false;

        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);    //Gets the Location settings
        } catch(Exception ex) {}

        //Starts navigation if the settings are enabled
        if(gpsEnabled)
        {
            Intent NavigationIntent = new Intent(MarkerSelected.this, NavigationActivity.class);
            startActivity(NavigationIntent);
            finish();
        }
        //Notifies the user if the settings are disabled
        else
        {
            Toast.makeText(this, "Location settings are off. Please turn them on to get directions.", Toast.LENGTH_LONG).show();
        }
    }

    //Returns to the navigation using the old destination
    public void goBack(View v)
    {
        //Stops the text that is being spoken
        stopSpeaking();
        CheckNearby.update();   //Updates the nearby list
        finish();
    }

    //Starts the navigation to the new locatioon
    public void goHere(View v)
    {
        //Stops the text that is being spoken
        stopSpeaking();
        //Sets the new location to the destination
        StaticVariables.destinationMarker = CheckNearby.marker;
        StaticVariables.destination = Position.fromCoordinates(CheckNearby.marker.getPosition().getLongitude(),
                                                               CheckNearby.marker.getPosition().getLatitude());
        CheckNearby.init(); //Resets the nearby list
        finish();
    }

    //Occurs when the activity is paused
    @Override
    public void onPause()
    {
        super.onPause();
        //Stops the text that is being spoken
        stopSpeaking();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //Starts speaking the description
        if(StaticVariables.speakDescriptions) {
            textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.speak(description, TextToSpeech.QUEUE_FLUSH, null);
                }
            });
        }
    }
}
