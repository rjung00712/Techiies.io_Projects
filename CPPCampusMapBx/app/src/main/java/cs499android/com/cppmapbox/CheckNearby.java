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
    private static final double TO_RADIANS = Math.PI / 180;     //Used in calculating the distance between two LatLng Points
    private static final double RADIUS_OF_EARTH = 6378037.0;    //Used in calculating the distance between two LatLng Points
    protected static ArrayList<Marker> nearby;      //List of the nearby locations
    protected static ArrayList<Marker> checked;     //List of the locations that have already been shown to the user
    protected static LatLng user;       //The user's location
    protected static Marker marker;     //Marker that will hold the information that is shown to the user when they are close to it
    private static final double MAX_DISTANCE = 100; //Distance is how close to a location the user is considered to be nearby (in meters)

    //Initializes the two lists
    protected static void init()
    {
        nearby = ClusterHolder.nearby.getMarkers();
        checked = new ArrayList<>();
    }

    //Returns a marker of the location that is close to the user
    //Will return null if there are no locations nearby
    protected static Marker getNearby()
    {
        //Iterates through the nearby list
        for(Marker m : nearby)
        {
            //Moves on if the location has already been shown to the user
            if(checkedContains(m))
                continue;
            double distance = getDistance(m.getPosition()); //Gets the distance to the point
            if(distance <= MAX_DISTANCE) {
                //Returns the location if it is not the user's destination
                if (!equals(m, StaticVariables.destinationMarker)) {
                    marker = m;
                    return marker;
                }
            }
        }
        return null;
    }

    //Looks to see it the location has been shown to the user
    private static boolean checkedContains(Marker m)
    {
        for(Marker mark : checked)
        {
            if(equals(m, mark))
                return true;
        }
        return false;
    }

    //Looks to see if two markers are the same
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

    //Calculates the distance between two LatLng points in meters
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

    //Adds the marker to the checked list
    protected static void update()
    {
        checked.add(marker);
        marker = null;
    }
}
