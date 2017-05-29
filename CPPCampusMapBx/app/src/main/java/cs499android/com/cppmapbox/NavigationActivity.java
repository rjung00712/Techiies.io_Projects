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
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
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
    private MapView mapView;
    private MapboxMap map;
    private DirectionsRoute currentRoute;
    private Polyline currentLine;
    private Polyline oldLine;
    private Marker oldDestination;
    private PermissionsManager permissionsManager;
    private TextToSpeech textToSpeech;
    private StepManeuver nextManeuver;
    private boolean speak;
    private boolean overview;
    private Polygon polygon;

    private android.support.design.widget.FloatingActionButton cancelButton;
    private android.support.design.widget.FloatingActionButton cameraView;

    ///////////////////////////////////////////////////
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        // Setup the MapView
        mapView = (MapView) findViewById(R.id.navigationMapView);
        mapView.onCreate(savedInstanceState);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        //////////////////////////////////////////////

        overview = false;
        setPermissions();
        CheckNearby.init();

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;
                StaticVariables.destinationMarker = map.addMarker(new MarkerOptions().position(StaticVariables.destinationMarker.getPosition())
                        .title(StaticVariables.destinationMarker.getTitle())
                        .snippet(StaticVariables.destinationMarker.getSnippet())
                        .icon(IconFactory.getInstance(NavigationActivity.this).fromResource(R.drawable.ic_location_on_red_18dp)));
                oldDestination = StaticVariables.destinationMarker;
                colorDestination();
                speak = true;
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
                map.setMyLocationEnabled(true);
                map.getTrackingSettings().setDismissAllTrackingOnGesture(false);
                map.getTrackingSettings().setMyLocationTrackingMode(MyLocationTracking.TRACKING_FOLLOW);
                map.getTrackingSettings().setMyBearingTrackingMode(MyBearingTracking.COMPASS);
                updateCamera((Position.fromCoordinates(map.getMyLocation().getLongitude(), map.getMyLocation().getLatitude())));
                cancelButton = (android.support.design.widget.FloatingActionButton) findViewById(R.id.navigation_cancel_fab);
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
//                            LatLngBounds bounds = new LatLngBounds.Builder()
//                                    .include(new LatLng(StaticVariables.destination.getLatitude(), StaticVariables.destination.getLongitude()))
//                                    .include(new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude()))
//                                    .build();
//                            map.easeCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
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
                CheckNearby.user = new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude());
            }
        });
    }

    private void colorDestination()
    {
        Marker temp = StaticVariables.destinationMarker;
        boolean within = false;
        int i;
        for(i = 0; i < StaticVariables.positions.size(); i++)
        {
            within = TurfJoins.inside(Position.fromCoordinates(temp.getPosition().getLongitude(),
                    temp.getPosition().getLatitude()), StaticVariables.positions.get(i));
            if(within)
                break;
        }
        if(within)
            drawPolygon(i);
    }

    private void drawPolygon(int i)
    {
        if(this.polygon != null)
            map.removePolygon(this.polygon);
        List<LatLng> polygon = StaticVariables.polygons.get(i);
        this.polygon = map.addPolygon(new PolygonOptions().addAll(polygon).fillColor(Color.parseColor("#1CCC13")));
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            if(oldDestination != StaticVariables.destinationMarker){
                map.removeMarker(oldDestination);
                StaticVariables.destinationMarker = map.addMarker(new MarkerOptions().position(StaticVariables.destinationMarker.getPosition())
                        .title(StaticVariables.destinationMarker.getTitle())
                        .snippet(StaticVariables.destinationMarker.getSnippet())
                        .icon(IconFactory.getInstance(NavigationActivity.this).fromResource(R.drawable.ic_location_on_red_18dp)));
                map.removePolygon(polygon);
                colorDestination();
                oldDestination = StaticVariables.destinationMarker;
            }
            // Move the map camera to where the user location is and then remove the
            // listener so the camera isn't constantly updating when the user location
            // changes. When the user disables and then enables the location again, this
            // listener is registered again and will adjust the camera once again.
            try {
                Position origin = Position.fromLngLat(location.getLongitude(), location.getLatitude());
                if(!overview) {
                    updateCamera(origin);
                }
                checkNearby(origin);
                getRoute(origin);
            } catch (ServicesException se) {
                se.printStackTrace();
            }
        }
    }

    private void updateCamera(Position p)
    {
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(p.getLatitude(), p.getLongitude()))
                .zoom(18)
                .tilt(50)
                .build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(position));
    }

    private void checkNearby(Position origin)
    {
        CheckNearby.user = new LatLng(origin.getLatitude(), origin.getLongitude());
        Marker marker = CheckNearby.getNearby();
        if(marker != null) {
            Intent nearbyIntent = new Intent(this, MarkerSelected.class);
            nearbyIntent.putExtra("Title", marker.getTitle())
                    .putExtra("Description", marker.getSnippet())
                    .putExtra("Type", "Nearby");
            startActivity(nearbyIntent);
        }
    }

    private void updateSpeaking(StepManeuver newManeuver)
    {
        //Same direction
        if(equal(newManeuver))
        {
            double distance = currentRoute.getLegs().get(0).getSteps().get(1).getDistance();
            //Check how far the user is from the next maneuver
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

    private void getRoute(Position origin) throws ServicesException
    {
        Position destination = StaticVariables.destination;
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
                currentRoute = response.body().getRoutes().get(0);
                Log.d(TAG, "Distance: " + currentRoute.getDistance());

                // Draw the curRoute on the map
                drawRoute(currentRoute);
                updateDirections();
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Log.e(TAG, "Error: " + throwable.getMessage());
                Toast.makeText(NavigationActivity.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void drawRoute(DirectionsRoute route) {
        // Convert LineString coordinates into LatLng[]
        LineString lineString = LineString.fromPolyline(route.getGeometry(), 6);
        List<Position> coordinates = lineString.getCoordinates();
        LatLng[] points = new LatLng[coordinates.size()];
        for (int i = 0; i < coordinates.size(); i++) {
            points[i] = new LatLng(
                    coordinates.get(i).getLatitude(),
                    coordinates.get(i).getLongitude());
        }

        // Draw Points on MapView
        currentLine = map.addPolyline(new PolylineOptions()
                .add(points)
                .color(Color.parseColor("#009688"))
                .width(5));
        if(oldLine != null)
            map.removePolyline(oldLine);
        oldLine = currentLine;
    }

    private void updateDirections()
    {
        try {
            StepManeuver newManeuver = currentRoute.getLegs().get(0).getSteps().get(1).getManeuver();
            updateSpeaking(newManeuver);
            String instruction = currentRoute.getLegs().get(0).getSteps().get(1).getManeuver().getInstruction();
            double distance = currentRoute.getLegs().get(0).getSteps().get(0).getDistance();
            int distanceInFeet = convertToFeet(distance);
            final EditText directions = (EditText) findViewById(R.id.directionsText);
            directions.setText(instruction + " in " + distanceInFeet + " feet.");
            ImageView imageView = (ImageView) findViewById(R.id.directionPic);
            if(StaticVariables.speakDirections && speak) {
                textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        textToSpeech.setLanguage(Locale.US);
                        textToSpeech.speak(directions.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                    }
                });
            }

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
        if((distance * 10) % 10 < 5)
            return (int)(distance * 3.28084);
        else
            return (int)(distance * 3.28084) + 1;
    }

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
        mGoogleApiClient.connect();
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
