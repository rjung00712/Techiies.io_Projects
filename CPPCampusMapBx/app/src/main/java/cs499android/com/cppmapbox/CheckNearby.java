package cs499android.com.cppmapbox;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

/**
 * Created by awing_000 on 5/25/2017.
 */

public abstract class CheckNearby
{
    private static final double TO_RADIANS = Math.PI / 180;
    private static final double RADIUS_OF_EARTH = 6378037.0;
    protected static ArrayList<Marker> nearby;
    protected static ArrayList<Marker> checked;
    protected static LatLng user;
    protected static Marker marker;
    private static final double MAX_DISTANCE = 100;

    protected static void init()
    {
        nearby = ClusterHolder.nearby.getMarkers();
        checked = new ArrayList<>();
    }

    protected static Marker getNearby()
    {
        for(Marker m : nearby)
        {
            if(checked.contains(m))
                break;
            double distance = getDistance(m.getPosition());
            if(distance <= MAX_DISTANCE) {
                if (!equals(m, StaticVariables.destinationMarker)) {
                    marker = m;
                    return marker;
                }
            }
        }
        return null;
    }

    private static boolean equals(Marker one, Marker two)
    {
        if(!one.getTitle().equals(two.getTitle()))
            return false;
        if (!one.getPosition().equals(two.getPosition()))
            return false;
        if(!one.getSnippet().equals(two.getSnippet()))
            return false;
        return true;
    }

    private static double getDistance(LatLng latLng)
    {
        double desLatRad = latLng.getLatitude() * TO_RADIANS;
        double userLatRad = user.getLatitude() * TO_RADIANS;
        double deltaLat = (user.getLatitude() - latLng.getLatitude()) * TO_RADIANS;
        double deltaLong = (user.getLongitude() - latLng.getLongitude()) * TO_RADIANS;

        double var = sin(deltaLat / 2) *  sin(deltaLat / 2) + (cos(desLatRad) * cos(userLatRad)) * (sin(deltaLong / 2) * sin(deltaLong / 2));
        double c = 2 * atan2(sqrt(var), sqrt(1 - var));
        return RADIUS_OF_EARTH * c;
    }

    protected static void update()
    {
        checked.add(marker);
        marker = null;
    }
}
