package cs499android.com.cppmapbox;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.mapbox.mapboxsdk.annotations.Marker;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by awing_000 on 5/23/2017.
 */

public class GeofenceTransitionsIntentService extends IntentService {
    protected static Marker marker;

    public GeofenceTransitionsIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            Geofence geofence = geofencingEvent.getTriggeringGeofences().get(0);
            String id = geofence.getRequestId();
            for(Marker m : ClusterHolder.nearby.getMarkers())
            {
                if(m.getTitle().equals(id))
                    marker = m;
            }
            if(marker != null) {
                Intent MarkerSelectedIntent = new Intent(GeofenceTransitionsIntentService.this, MarkerSelected.class);
                MarkerSelectedIntent.putExtra("Title", marker.getTitle());
                MarkerSelectedIntent.putExtra("Description", marker.getSnippet());
                MarkerSelectedIntent.putExtra("Type", "Nearby");
                startActivity(MarkerSelectedIntent);
            }
        }
    }
}
