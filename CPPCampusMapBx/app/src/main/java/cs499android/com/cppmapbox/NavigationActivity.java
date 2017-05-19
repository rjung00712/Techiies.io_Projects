package cs499android.com.cppmapbox;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationSource;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsManager;
import com.mapbox.services.api.ServicesException;
import com.mapbox.services.api.directions.v5.DirectionsCriteria;
import com.mapbox.services.api.directions.v5.MapboxDirections;
import com.mapbox.services.api.directions.v5.models.DirectionsResponse;
import com.mapbox.services.api.directions.v5.models.DirectionsRoute;
import com.mapbox.services.commons.geojson.LineString;
import com.mapbox.services.commons.models.Position;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static cs499android.com.cppmapbox.StaticVariables.*;

@SuppressWarnings( {"MissingPermission"})
public class NavigationActivity extends AppCompatActivity implements PermissionsListener
{
    private MapView mapView;
    private MapboxMap map;
    private DirectionsRoute currentRoute;
    private Polyline currentLine;
    private Polyline oldLine;
    private LocationEngine locationEngine;
    private LocationEngineListener locationEngineListener;
    private PermissionsManager permissionsManager;
    private TextToSpeech textToSpeech;
    private boolean navigating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        // Setup the MapView
        mapView = (MapView) findViewById(R.id.navigationMapView);
        mapView.onCreate(savedInstanceState);

        locationEngine = LocationSource.getLocationEngine(NavigationActivity.this);

        setPermissions();
        CheckNearby.init();

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;
                map.addMarker(new MarkerOptions().position(StaticVariables.destinationMarker.getPosition())
                        .title(StaticVariables.destinationMarker.getTitle())
                        .snippet(StaticVariables.destinationMarker.getSnippet())
                        .icon(StaticVariables.destinationMarker.getIcon()));
                navigating = true;
                getPermissions();
            }
        });
    }

    private void updateRoute()
    {
        if(!navigating)
            return;
        else
        {
            android.os.Handler handler = new android.os.Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startRouteCalc();
                    updateRoute();
                }
            }, 5000);   //After 5 seconds do the run method above
        }
    }

    private void getPermissions() {
        if (!StaticVariables.userLocationEnabeld) {
            permissionsManager.requestLocationPermissions(this);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, PERMISSIONS_REQUEST_LOCATION);
                //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
            } else {
                updateRoute();
            }
        }
        map.setMyLocationEnabled(true);
    }

    private void startRouteCalc()
    {
        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation), 16));

            try {
                Position origin = Position.fromLngLat(lastLocation.getLongitude(), lastLocation.getLatitude());
//                        List<Polyline> list = map.getPolylines();
//                        for (int i = 0; i < list.size(); i++)
//                            map.removePolyline(list.get(i));
                getRoute(origin);
            } catch (ServicesException se) {
                se.printStackTrace();
            }
        }

        locationEngineListener = new LocationEngineListener() {
            @Override
            public void onConnected() {
                locationEngine.requestLocationUpdates();
            }

            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    // Move the map camera to where the user location is and then remove the
                    // listener so the camera isn't constantly updating when the user location
                    // changes. When the user disables and then enables the location again, this
                    // listener is registered again and will adjust the camera once again.
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location), 16));
                    locationEngine.removeLocationEngineListener(this);
                    try {
                        Position origin = Position.fromLngLat(location.getLongitude(), location.getLatitude());
//                                List<Polyline> list = map.getPolylines();
//                                for (int i = 0; i < list.size(); i++)
//                                    map.removePolyline(list.get(i));
                        getRoute(origin);
                    } catch (ServicesException se) {
                        se.printStackTrace();
                    }
                }
            }
        };
        locationEngine.addLocationEngineListener(locationEngineListener);
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
                .setContinueStraight(true)
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
//            String[] instructions = new String[currentRoute.getLegs().get(0).getSteps().size()];
//            for(int i = 0; i < currentRoute.getLegs().get(0).getSteps().size(); i++) {
//                instructions[i] = currentRoute.getLegs().get(0).getSteps().get(i).getManeuver().getInstruction();
//            }
            String instruction = currentRoute.getLegs().get(0).getSteps().get(1).getManeuver().getInstruction();
            double distance = currentRoute.getLegs().get(0).getSteps().get(0).getDistance();
            final EditText directions = (EditText) findViewById(R.id.directionsText);
            directions.setText(instruction + " in " + distance + " feet.");
            ImageView imageView = (ImageView) findViewById(R.id.directionPic);
            if(StaticVariables.speakDirections) {
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
                if (instruction.toLowerCase().contains("turn left"))
                    imageView.setImageResource(R.drawable.left_turn);
                else
                    imageView.setImageResource(R.drawable.slight_left);
            }
            else if(instruction.toLowerCase().contains("right")) {
                if (instruction.toLowerCase().equals("turn right"))
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

    public void setPermissions() {
        permissionsManager = new PermissionsManager(this);
        if (!PermissionsManager.areLocationPermissionsGranted(this)) {
            permissionsManager.requestLocationPermissions(this);
        } else {
            StaticVariables.userLocationEnabeld = true;
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "This app needs location permissions in order to show its functionality.",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            StaticVariables.userLocationEnabeld = true;
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
        if (locationEngine != null && locationEngineListener != null) {
            locationEngine.activate();
            locationEngine.requestLocationUpdates();
            locationEngine.addLocationEngineListener(locationEngineListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        if (locationEngine != null && locationEngineListener != null) {
            locationEngine.removeLocationEngineListener(locationEngineListener);
            locationEngine.removeLocationUpdates();
            locationEngine.deactivate();
        }
    }
}
