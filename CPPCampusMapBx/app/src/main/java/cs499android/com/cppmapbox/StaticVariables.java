package cs499android.com.cppmapbox;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.services.commons.models.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Richard on 5/3/17.
 */

public abstract class StaticVariables {
    //Token to use MapBox
    protected static final String MAPBOX_ACCESS_TOKEN = "pk.eyJ1IjoicmljaGFyZGp1bmciLCJhIjoiY2oyOTdma3EwMDA2cTJxbXgwMGt1MWI1aCJ9.d2pGP-GfbVszdIzT-CdJHA";
    protected static final String TAG = "MainActivity";
    protected static final int PERMISSIONS_REQUEST_LOCATION = 99;
    protected static final String BASE_URL = "https://api.mapbox.com";
    protected static Position destination;  //The destination for the route
    protected static Marker destinationMarker;  //The marker for the destination
    protected static boolean userLocationEnabled;   //Checks if the user's location is enabled
    protected static boolean speakDescriptions = true;  //Used to check if Descriptions should be spoken
    protected static boolean speakDirections = true;    //Used to check if the directions should be spoken
    protected static MapboxMap map;     //Map used in the main activity
    protected static List<List<LatLng>> polygons = new ArrayList<>();   //List of polygons
    protected static List<List<Position>> positions = new ArrayList<>();    //List of positions
}
