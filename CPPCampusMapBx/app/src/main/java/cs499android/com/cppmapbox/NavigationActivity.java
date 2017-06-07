package cs499android.com.cppmapbox;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polygon;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.MyBearingTracking;
import com.mapbox.mapboxsdk.constants.MyLocationTracking;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.android.telemetry.permissions.PermissionsListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsManager;
import com.mapbox.services.api.ServicesException;
import com.mapbox.services.api.directions.v5.DirectionsCriteria;
import com.mapbox.services.api.directions.v5.MapboxDirections;
import com.mapbox.services.api.directions.v5.models.DirectionsResponse;
import com.mapbox.services.api.directions.v5.models.DirectionsRoute;
import com.mapbox.services.api.directions.v5.models.StepManeuver;
import com.mapbox.services.api.utils.turf.TurfJoins;
import com.mapbox.services.commons.geojson.LineString;
import com.mapbox.services.commons.models.Position;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static cs499android.com.cppmapbox.StaticVariables.TAG;

@SuppressWarnings( {"MissingPermission"})
public class NavigationActivity extends AppCompatActivity implements PermissionsListener,
        LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    private MapView mapView;        //Holds the Map view
    private MapboxMap map;          //Holds the map that the user sees. The Markers and route are added to this map
    private DirectionsRoute currentRoute;   //Holds the current route the user is following
    private Polyline currentLine;       //This is the polyline that corresponds to the currentRoute
    private Polyline oldLine;           //This is the last polyline for the route
    private Marker oldDestination;      //This is the destination that was originally set when the user started the navigation
    private PermissionsManager permissionsManager;  //This allows for us to make sure we have the correct permissions
    private TextToSpeech textToSpeech;      //Allows for the directions to be spoken
    private StepManeuver nextManeuver;      //This Allows for us to show the user what direction they will do next and in how many feet
    private boolean speak;      //Checks if the directions should be spoken
    private boolean overview;   //Checks if the user wants to see an overview of the map
    private Polygon polygon;    //Holds the polygon of the current destination

    private android.support.design.widget.FloatingActionButton cancelButton;    //This is the button to cancel navigation
    private android.support.design.widget.FloatingActionButton cameraView;      //This is the button to change the camera angle

    ///////////////////////////////////////////////////
    GoogleApiClient mGoogleApiClient;       //Used for the user's location
    LocationRequest mLocationRequest;       //Used for the user's location
    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));      //Sets up MapBox

        // Setup the MapView
        mapView = (MapView) findViewById(R.id.navigationMapView);   //Finds the view the map will be shown in
        mapView.onCreate(savedInstanceState);
        mLocationRequest = new LocationRequest();   //Allows us to continue to get the user's location
        mLocationRequest.setInterval(3000);         //Gets the user's location every 3 seconds
        mLocationRequest.setFastestInterval(1000);      //Cannot get the user's location faster than 1 second
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);   //To be as accurate as possible
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        //////////////////////////////////////////////

        overview = false;   //Initialize the view as the angled view
        setPermissions();   //Sets up the appropriate permissions
        CheckNearby.init();     //Sets up the list of nearby places

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;    //The map is set to the map that is ready to be used
                //Adds the destination marker to the map and sets this as the destination marker
                StaticVariables.destinationMarker = map.addMarker(new MarkerOptions().position(StaticVariables.destinationMarker.getPosition())
                        .title(StaticVariables.destinationMarker.getTitle())
                        .snippet(StaticVariables.destinationMarker.getSnippet())
                        .icon(IconFactory.getInstance(NavigationActivity.this).fromResource(R.drawable.destination_reached_24px)));
                oldDestination = StaticVariables.destinationMarker;     //This becomes the old destination incase the user goes to a nearby location
                colorDestination(); //Adds the green polygon to the map
                speak = true;       //Makes sure to speak the first direction
                //We do not want the marker to show any information when it is clicked
                map.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        map.setInfoWindowAdapter(new MapboxMap.InfoWindowAdapter() {
                            @Nullable
                            @Override
                            public View getInfoWindow(@NonNull Marker marker) {
                                return new LinearLayout(NavigationActivity.this);
                            }
                        });
                        return false;
                    }
                });
                map.setMyLocationEnabled(true);     //Show the user on the map
                map.getTrackingSettings().setDismissAllTrackingOnGesture(false);    //User cannot move around the screen
                map.getTrackingSettings().setMyLocationTrackingMode(MyLocationTracking.TRACKING_FOLLOW);    //Follows the user
                map.getTrackingSettings().setMyBearingTrackingMode(MyBearingTracking.COMPASS);      //Shows the direction the user is facing
                updateCamera((Position.fromCoordinates(map.getMyLocation().getLongitude(), map.getMyLocation().getLatitude())));    //updates the camera based on the user's location
                cancelButton = (android.support.design.widget.FloatingActionButton) findViewById(R.id.navigation_cancel_fab);
                //Ends the navigation when the user clicks the cancel button
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(StaticVariables.speakDescriptions) {
                            if (textToSpeech != null && textToSpeech.isSpeaking()) {
                                textToSpeech.stop();
                                textToSpeech.shutdown();
                            }
                        }
                        finish();
                    }
                });
                cameraView = (android.support.design.widget.FloatingActionButton) findViewById(R.id.view_fab);
                //Changes the view when the user clicks this button
                cameraView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view)
                    {
                        if(overview)
                        {
                            map.getTrackingSettings().setDismissAllTrackingOnGesture(false);
                            map.getTrackingSettings().setMyLocationTrackingMode(MyLocationTracking.TRACKING_FOLLOW);
                            map.getTrackingSettings().setMyBearingTrackingMode(MyBearingTracking.COMPASS);
                            updateCamera(Position.fromCoordinates(map.getMyLocation().getLongitude(), map.getMyLocation().getLatitude()));
                        }
                        else
                        {
                            CameraPosition position = new CameraPosition.Builder()
                                    .tilt(0)
                                    .zoom(16)
                                    .build();
                            map.moveCamera(CameraUpdateFactory.newCameraPosition(position));
                            map.getTrackingSettings().setDismissAllTrackingOnGesture(true);
                        }
                        overview = !overview;
                    }
                });
                CheckNearby.user = new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude());   //Updates the user's location in the CheckNearby class
            }
        });
    }

    private void colorDestination()
    {
        Marker temp = StaticVariables.destinationMarker;
        boolean within = false;     //Used to see if the destination is in a certain polygon
        int i;
        for(i = 0; i < StaticVariables.positions.size(); i++)   //Iterates through the list of polygons to find the one that needs to be drawn out
        {
            within = TurfJoins.inside(Position.fromCoordinates(temp.getPosition().getLongitude(),
                    temp.getPosition().getLatitude()), StaticVariables.positions.get(i));   //If the destination is in the polygon then it is the one selected
            if(within)
                break;
        }
        if(within)
            drawPolygon(i); //Draws the correct polygon
    }

    private void drawPolygon(int i)
    {
        if(this.polygon != null)
            map.removePolygon(this.polygon);    //Removes the old polygon
        List<LatLng> polygon = StaticVariables.polygons.get(i);     //Gets the correct polygon to draw
        this.polygon = map.addPolygon(new PolygonOptions().addAll(polygon).fillColor(Color.parseColor("#1CCC13"))); //Draws the polygon on the map
    }

    @Override
    //Called when the user's location is changed
    public void onLocationChanged(Location location) {
        if (location != null) {
            //Checks if the user is going to a new destination (Going to a nearby location)
            if(oldDestination != StaticVariables.destinationMarker){
                map.removeMarker(oldDestination);   //Removes the old destination
                //Adds the new destination marker
                StaticVariables.destinationMarker = map.addMarker(new MarkerOptions().position(StaticVariables.destinationMarker.getPosition())
                        .title(StaticVariables.destinationMarker.getTitle())
                        .snippet(StaticVariables.destinationMarker.getSnippet())
                        .icon(IconFactory.getInstance(NavigationActivity.this).fromResource(R.drawable.destination_reached_24px)));
                map.removePolygon(polygon); //Removes the old polygon
                colorDestination(); //Draws the new polygon
                oldDestination = StaticVariables.destinationMarker;     //Updates the old position
            }

            try {
                Position origin = Position.fromLngLat(location.getLongitude(), location.getLatitude()); //Find the origin point of the route
                //If the user is not in the overview mode then update the camera
                if(!overview) {
                    updateCamera(origin);
                }
                checkNearby(origin);    //Check for a nearby location
                getRoute(origin);       //Get a route for the user to follow
            } catch (ServicesException se) {
                se.printStackTrace();
            }
        }
    }

    private void updateCamera(Position p)
    {
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(p.getLatitude(), p.getLongitude()))  //Points to the user's location
                .zoom(18)       //Zoomed in to see the user and the immediate surroundings
                .tilt(50)       //Tilted to a 50 degree angle
                .build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(position));    //Updates the camera to the specified settings
    }

    private void checkNearby(Position origin)
    {
        CheckNearby.user = new LatLng(origin.getLatitude(), origin.getLongitude());
        Marker marker = CheckNearby.getNearby();    //Gets a marker from the CheckNearby Class (Can be null meaning nothing is there
        //If there is a marker nearby notify the user
        if(marker != null) {
            Intent nearbyIntent = new Intent(this, MarkerSelected.class);
            nearbyIntent.putExtra("Title", marker.getTitle())
                    .putExtra("Description", marker.getSnippet())
                    .putExtra("Type", "Nearby");
            startActivity(nearbyIntent);
        }
    }

    //Checks to see if the directions should be spoken
    private void updateSpeaking(StepManeuver newManeuver)
    {
        //Same direction
        //If the user is headed to the same waypoint
        if(equal(newManeuver))
        {
            double distance = currentRoute.getLegs().get(0).getSteps().get(1).getDistance();    //Check how far the user is from the next maneuver
            //if the user is within 5 meters let them know
            if(distance <= 5)
                speak = true;
            else
                speak = false;
        }
        else    //New direction
        {
            nextManeuver = newManeuver;
            speak = true;
        }
    }

    //Checks if two maneuvers are equal
    private boolean equal(StepManeuver newManeuver)
    {
        if(nextManeuver == null)
            return false;
        if((newManeuver.getLocation()[0] != nextManeuver.getLocation()[0]) || (newManeuver.getLocation()[1] != nextManeuver.getLocation()[1]))
            return false;
        if(!newManeuver.getInstruction().equals(nextManeuver.getInstruction()))
            return false;
        if(newManeuver.getBearingAfter() != nextManeuver.getBearingAfter())
            return false;
        return true;
    }

    //Gets a route based on the user's location and the set destination
    private void getRoute(Position origin) throws ServicesException
    {
        Position destination = StaticVariables.destination;
        //Creates a route based on walking directions from the origin to the destination, getting all of the steps
        MapboxDirections client = new MapboxDirections.Builder()
                .setOrigin(origin)
                .setDestination(destination)
                .setProfile(DirectionsCriteria.PROFILE_WALKING)
                .setAccessToken(Mapbox.getAccessToken())
                .setSteps(true)
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                // You can get the generic HTTP info about the response
                Log.d(TAG, "Response code: " + response.code());
                if (response.body() == null) {
                    Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().getRoutes().size() < 1) {
                    Log.e(TAG, "No routes found");
                    return;
                }
                // Print some info about the curRoute
                currentRoute = response.body().getRoutes().get(0);  //Sets the current route to the route that was found
                Log.d(TAG, "Distance: " + currentRoute.getDistance());

                // Draw the curRoute on the map
                drawRoute(currentRoute);    //Draws the route
                updateDirections();     //Updates the directions based on the new route
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Log.e(TAG, "Error: " + throwable.getMessage());
                Toast.makeText(NavigationActivity.this, "Error: " + throwable.getMessage() + ". Make sure that you have a network connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void drawRoute(DirectionsRoute route) {
        // Convert LineString coordinates into LatLng[]
        LineString lineString = LineString.fromPolyline(route.getGeometry(), 6);
        //Gets the coordinate points for each maneuver
        List<Position> coordinates = lineString.getCoordinates();
        LatLng[] points = new LatLng[coordinates.size()];
        for (int i = 0; i < coordinates.size(); i++) {
            points[i] = new LatLng(
                    coordinates.get(i).getLatitude(),
                    coordinates.get(i).getLongitude());
        }

        // Draw the route on the map as a polyline
        currentLine = map.addPolyline(new PolylineOptions()
                .add(points)
                .color(Color.parseColor("#009688"))
                .width(5));
        //Remove the old polyline (shortens the route as the user moves)
        if(oldLine != null)
            map.removePolyline(oldLine);
        oldLine = currentLine;
    }

    //Updates the directions that are shown to the user
    private void updateDirections()
    {
        try {
            StepManeuver newManeuver = currentRoute.getLegs().get(0).getSteps().get(1).getManeuver();   //Gets the next maneuver from the route
            updateSpeaking(newManeuver);    //Checks if it should be spoken or not
            String instruction = currentRoute.getLegs().get(0).getSteps().get(1).getManeuver().getInstruction();    //Gets the instruction (i.e. "Turn Left")
            double distance = currentRoute.getLegs().get(0).getSteps().get(0).getDistance();    //Gets the distance to the next maneuver
            int distanceInFeet = convertToFeet(distance);   //Converts this value from meters to feet
            final EditText directions = (EditText) findViewById(R.id.directionsText);
            directions.setText(instruction + " in " + distanceInFeet + " feet.");   //Sets the direction to the instruction
            ImageView imageView = (ImageView) findViewById(R.id.directionPic);
            //Speaks the direction if it should be spoken
            if(StaticVariables.speakDirections && speak) {
                textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        textToSpeech.setLanguage(Locale.US);
                        textToSpeech.speak(directions.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                    }
                });
            }

            //Sets the picture to the appropriate image for the instruction
            if(instruction.toLowerCase().contains("destination"))
                imageView.setImageResource(R.drawable.destination_reached);
            else if(instruction.toLowerCase().contains("straight"))
                imageView.setImageResource(R.drawable.straight);
            else if(instruction.toLowerCase().contains("left")) {
                if (instruction.toLowerCase().contains("turn left") || instruction.toLowerCase().contains("sharp left"))
                    imageView.setImageResource(R.drawable.left_turn);
                else
                    imageView.setImageResource(R.drawable.slight_left);
            }
            else if(instruction.toLowerCase().contains("right")) {
                if (instruction.toLowerCase().equals("turn right") || instruction.toLowerCase().contains("sharp right"))
                    imageView.setImageResource(R.drawable.right_turn);
                else
                    imageView.setImageResource(R.drawable.slight_right);
            }
        }catch (ArrayIndexOutOfBoundsException ex)
        {
            EditText directions = (EditText) findViewById(R.id.directionsText);
            directions.setText(currentRoute.getLegs().get(0).getSteps().get(0).getManeuver().getInstruction());
            ImageView imageView = (ImageView) findViewById(R.id.directionPic);
            imageView.setImageResource(R.drawable.destination_reached);
        }
    }

    private int convertToFeet(double distance)
    {
        //Converts the distance to feet and rounds appropriately to the nearest foot
        if((distance * 10) % 10 < 5)
            return (int)(distance * 3.28084);
        else
            return (int)(distance * 3.28084) + 1;
    }

    //Sets the permissions needed
    public void setPermissions() {
        permissionsManager = new PermissionsManager(this);
        if (!PermissionsManager.areLocationPermissionsGranted(this)) {
            permissionsManager.requestLocationPermissions(this);
        } else {
            StaticVariables.userLocationEnabled = true;
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "This app needs location permissions in order to show its functionality.",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            StaticVariables.userLocationEnabled = true;
        } else {
            Toast.makeText(this, "You didn't grant location permissions.",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        mGoogleApiClient.connect(); //Connect to get the user's location
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(NavigationActivity.this,
                            "permission was granted, :)",
                            Toast.LENGTH_LONG).show();
                    try{
                        LocationServices.FusedLocationApi.requestLocationUpdates(
                                mGoogleApiClient, mLocationRequest, this);
                    }catch(SecurityException e){
                        Toast.makeText(NavigationActivity.this,
                                "SecurityException:\n" + e.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(NavigationActivity.this,
                            "permission denied, ...:(",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        //Stops the directions from being spoken
        if(StaticVariables.speakDescriptions) {
            if (textToSpeech!= null && textToSpeech.isSpeaking()) {
                textToSpeech.stop();
                textToSpeech.shutdown();
            }
        }
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Gets the appropriate permissions
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(NavigationActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(NavigationActivity.this,
                "onConnectionFailed: \n" + connectionResult.toString(),
                Toast.LENGTH_LONG).show();
    }
}
