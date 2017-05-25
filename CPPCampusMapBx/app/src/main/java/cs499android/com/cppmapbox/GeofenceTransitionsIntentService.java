package cs499android.com.cppmapbox;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

/**
 * Created by awing_000 on 5/24/2017.
 */

public class GeofenceTransitionsIntentService extends IntentService {

    private static final String TAG = "GeofenceTransitions";

    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TAG, "Goefencing Error " + geofencingEvent.getErrorCode());
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL){
            Intent geofenceIntent = new Intent(this, MarkerSelected.class);
            geofenceIntent//.putExtra("Title", "Test")
//                    .putExtra("Description", "Test Description***Bg_63.jpg")
//                    .putExtra("Type", "Nearby")
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(geofenceIntent);
        }
    }
}
