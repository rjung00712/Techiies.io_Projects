package cs499android.com.cppmapbox;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.HashMap;

/**
 * Created by awing_000 on 5/23/2017.
 */

public abstract class Geofences
{
    protected static final long EXPIRATION_TIME = 1000 * 60 * 60 * 3;
    protected static final float RADIUS = 40;
    protected static HashMap<String, LatLng> nearby = new HashMap<>();
    protected static void init()
    {
        for(Marker marker : ClusterHolder.nearby.getMarkers())
            nearby.put(marker.getTitle(), marker.getPosition());
    }
}
