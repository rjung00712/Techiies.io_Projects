package cs499android.com.cppmapbox;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationSource;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.android.navigation.v5.MapboxNavigation;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsManager;
import com.mapbox.services.api.ServicesException;
import com.mapbox.services.api.directions.v5.DirectionsCriteria;
import com.mapbox.services.api.directions.v5.MapboxDirections;
import com.mapbox.services.api.directions.v5.models.DirectionsResponse;
import com.mapbox.services.api.directions.v5.models.DirectionsRoute;
import com.mapbox.services.commons.geojson.FeatureCollection;
import com.mapbox.services.commons.geojson.GeoJSON;
import com.mapbox.services.commons.geojson.LineString;
import com.mapbox.services.commons.models.Position;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static cs499android.com.cppmapbox.Constants.MAPBOX_ACCESS_TOKEN;

public class MainActivity extends AppCompatActivity implements PermissionsListener {

    private static final String TAG = "MainActivity";
    public static final int PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String BASE_URL = "https://api.mapbox.com";
    private static final String BUILDINGS_KML = "buildings.kml";


    private MapView mapView;
    private MapboxMap map;
    private DirectionsRoute currentRoute;

    private LocationEngine locationEngine;
    private LocationEngineListener locationEngineListener;
    private PermissionsManager permissionsManager;

    private FloatingActionButton floatingActionButton;

    private Marker featureMarker;

    private com.mapbox.mapboxsdk.annotations.Polygon selectedBuilding;

    private Position destination;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();


        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        ///////////////////////////////////
        final MapboxNavigation navigation = new MapboxNavigation(this, MAPBOX_ACCESS_TOKEN);
        //////////////////////////////////

        // This contains the MapView in XML and needs to be called after the account manager
        setContentView(R.layout.activity_main);


        // Get the location engine object for later use.
        locationEngine = LocationSource.getLocationEngine(this);
        locationEngine.activate();

//        locationEngine = LostLocationEngine.getLocationEngine(this);
//        navigation.setLocationEngine(locationEngine);


//        final Position origin = Position.fromCoordinates(-117.823601, 34.058800);

        final Position defaultPoint = Position.fromCoordinates(-117.823601, 34.058800);
        destination = Position.fromCoordinates(-117.823332, 34.058031);

//        navigation.getRoute(origin, destination, new Callback<DirectionsResponse>() {
//            @Override
//            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
//                response.body();
//
//                // You can get the generic HTTP info about the response
//                Log.d(TAG, "Response code: " + response.code());
//                if (response.body() == null) {
//                    Log.e(TAG, "No routes found, make sure you set the right user and access token.");
//                    return;
//                } else if (response.body().getRoutes().size() < 1) {
//                    Log.e(TAG, "No routes found");
//                    return;
//                }
//                // Print some info about the route
//                currentRoute = response.body().getRoutes().get(0);
//                Log.d(TAG, "Distance: " + currentRoute.getDistance());
//
//
//                System.out.println(response.body().getRoutes().get(0).getLegs().get(0).getSteps().get(0).getManeuver().getInstruction());
//                System.out.println(response.body().getRoutes().get(0).getLegs().get(0).getSteps().get(1).getManeuver().getInstruction());
//                System.out.println(response.body().getRoutes().get(0).getLegs().get(0).getSteps().get(2).getManeuver().getInstruction());
//
//
////                RouteProgress routeProgress = new RouteProgress(currentRoute, origin, currentRoute.getLegs().get(0), currentRoute.);
//
////                RouteProgress(DirectionsRoute route, Position userSnappedPosition, int currentLegIndex,
////                int currentStepIndex, int alertUserLevel)
//            }
//
//            @Override
//            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
//
//            }
//        });

//        navigation.addProgressChangeListener(new ProgressChangeListener() {
//            @Override
//            public void onProgressChange(Location location, RouteProgress routeProgress) {
//                System.out.println("yo" + routeProgress.getCurrentLegProgress().getUpComingStep().getManeuver().getInstruction());
//            }
//        });


        // Setup the MapView
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;

                // Add origin and destination to the map
//                map.addMarker(new MarkerOptions()
//                        .position(new LatLng(defaultPoint.getLatitude(), defaultPoint.getLongitude()))
//                        .title("Origin")
//                        .snippet("University Quad"));
//                map.addMarker(new MarkerOptions()
//                        .position(new LatLng(destination.getLatitude(), destination.getLongitude()))
//                        .title("Destination")
//                        .snippet("Panda Express"));
                addMarkers("parking.geojson");
                addMarkers("landmarks.geojson");
                addMarkers("cpp_buildings.geojson");
                // Get route from API
//                try {
//                    getRoute(origin, destination);
//                } catch (ServicesException servicesException) {
//                    servicesException.printStackTrace();
//                }
            }
        });


        floatingActionButton = (FloatingActionButton) findViewById(R.id.location_toggle_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (map != null) {
                    toggleGps(!map.isMyLocationEnabled());
                }
            }
        });
    }


    private void getRoute(Position origin, Position destination) throws ServicesException {

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
                // Print some info about the route
                currentRoute = response.body().getRoutes().get(0);
                Log.d(TAG, "Distance: " + currentRoute.getDistance());


//                currentRoute.getLegs().get(0).getSteps().get(0).getManeuver().getInstruction();

//                System.out.println(currentRoute.getLegs().get(0).getSteps().get(0).getManeuver().getInstruction());
//
//                StepManeuver stepManeuver = new StepManeuver();

                Toast.makeText(
                        MainActivity.this,
                        currentRoute.getLegs().get(0).getSteps().get(0).getManeuver().getInstruction(),
                        Toast.LENGTH_SHORT).show();

                // Draw the route on the map
                drawRoute(currentRoute);
            }



            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Log.e(TAG, "Error: " + throwable.getMessage());
                Toast.makeText(MainActivity.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
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
        map.addPolyline(new PolylineOptions()
                .add(points)
                .color(Color.parseColor("#009688"))
                .width(5));
    }

    private void toggleGps(boolean enableGps) {
        if (enableGps) {
            // Check if user has granted location permission
            permissionsManager = new PermissionsManager(this);
            if (!PermissionsManager.areLocationPermissionsGranted(this)) {
                permissionsManager.requestLocationPermissions(this);
            } else {
                enableLocation(true);
            }
        } else {
            enableLocation(false);
        }
    }

    private void enableLocation(boolean enabled) {
        if (enabled) {
            // If we have the last location of the user, we can move the camera to that position.
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
                Location lastLocation = locationEngine.getLastLocation();
                if (lastLocation != null) {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation), 16));

                    try {
                        Position origin = Position.fromLngLat(lastLocation.getLongitude(), lastLocation.getLatitude());
                        getRoute(origin, destination);
                    } catch (ServicesException se) {
                        se.printStackTrace();
                    }

                }

                locationEngineListener = new LocationEngineListener() {
                    @Override
                    public void onConnected() {
                        // No action needed here.
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
                                getRoute(origin, destination);
                            } catch (ServicesException se) {
                                se.printStackTrace();
                            }
                        }
                    }
                };
                locationEngine.addLocationEngineListener(locationEngineListener);
                floatingActionButton.setImageResource(R.drawable.ic_location_disabled_24dp);
            }
        } else {
            floatingActionButton.setImageResource(R.drawable.ic_my_location_24dp);
        }
        // Enable or disable the location layer on the map
        map.setMyLocationEnabled(enabled);
    }

    private void addMarkers(String name) {
        String json = null;
        try {

            InputStream is = getAssets().open(name);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");

            if(json != null)
            {
                IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
                Icon icon = null;
                if(name.equals("parking.geojson"))
                    icon = iconFactory.fromResource(R.drawable.blue_marker);
                else if(name.equals("landmarks.geojson"))
                    icon = iconFactory.fromResource(R.drawable.green_marker);
                JSONObject jsonObject = new JSONObject(json);
                JSONArray features = jsonObject.getJSONArray("features");
                for(int i = 0; i < features.length(); i++)
                {
                    JSONObject feature = features.getJSONObject(i);
                    JSONObject properties = feature.getJSONObject("properties");
                    JSONObject geometry = feature.getJSONObject("geometry");
                    JSONArray coords = geometry.getJSONArray("coordinates");
                    MarkerOptions m = new MarkerOptions();
                    m.setTitle(properties.getString("name"));
                    m.setSnippet(properties.getString("description"));
                    m.setPosition(new LatLng(coords.getDouble(1), coords.getDouble(0)));
                    if(icon != null)
                        m.setIcon(icon);
                    map.addMarker(m);
                }
            }
//
//            String inputFileContents = ""; // find a way to read the file and store it in a string
//            String xmlContent = inputFileContents;
//            Document doc = Jsoup.parse(BUILDINGS_KML, "", Parser.xmlParser());
//
//            for(Element e : doc.select("LinearRing").select("coordinates")) {
//                // the contents
//                System.out.println(e.text());
//            }


        } catch (Exception ex) {
            ex.printStackTrace();
//                return null;
        }
//        return kmlData;
    }


    // Add the mapView lifecycle to the activity's lifecycle methods
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();

        // Ensure no memory leak occurs if we register the location listener but the call hasn't
        // been made yet.
        if (locationEngineListener != null) {
            locationEngine.removeLocationEngineListener(locationEngineListener);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
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
            enableLocation(true);
        } else {
            Toast.makeText(this, "You didn't grant location permissions.",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
