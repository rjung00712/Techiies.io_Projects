package cs499android.com.cppmapbox;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
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
import com.mapbox.services.api.directions.v5.models.DirectionsRoute;
import com.mapbox.services.commons.models.Position;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static cs499android.com.cppmapbox.StaticVariables.BASE_URL;

@SuppressWarnings( {"MissingPermission"})
public class MainActivity extends AppCompatActivity implements PermissionsListener
{
    private MapView mapView;
    private DirectionsRoute currentRoute;

    private LocationEngine locationEngine;
    private LocationEngineListener locationEngineListener;
    private PermissionsManager permissionsManager;

    private android.support.design.widget.FloatingActionButton floatingActionButton;

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
        final MapboxNavigation navigation = new MapboxNavigation(this, StaticVariables.MAPBOX_ACCESS_TOKEN);
        //////////////////////////////////

        // This contains the MapView in XML and needs to be called after the account manager
        setContentView(R.layout.activity_main);

        // Get the location engine object for later use.
        locationEngine = LocationSource.getLocationEngine(this);
        locationEngine.activate();

        // Setup the MapView
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        ClusterHolder.activity = MainActivity.this;
        setPermissions();

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                StaticVariables.map = mapboxMap;

                ClusterHolder.createMarkers();
                ClusterHolder.buildings.setSelected(false);
                ClusterHolder.food.setSelected(false);
                ClusterHolder.parking.setSelected(false);
                ClusterHolder.addMarkers();
                StaticVariables.map.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        StaticVariables.destination = Position.fromCoordinates(marker.getPosition().getLongitude(), marker.getPosition().getLatitude());
                        StaticVariables.destinationMarker = marker;
                        Intent MarkerSelectedIntent = new Intent(MainActivity.this, MarkerSelected.class);
                        MarkerSelectedIntent.putExtra("Title", marker.getTitle());
                        MarkerSelectedIntent.putExtra("Description", marker.getSnippet());
                        startActivity(MarkerSelectedIntent);
                        StaticVariables.map.setInfoWindowAdapter(new MapboxMap.InfoWindowAdapter() {
                            @Nullable
                            @Override
                            public View getInfoWindow(@NonNull Marker marker) {
                                return new LinearLayout(MainActivity.this);
                            }
                        });
                        return false;
                    }
                });

                setFloatingButtons();
            }
        });
    }

    private void setFloatingButtons()
    {
        com.getbase.floatingactionbutton.FloatingActionButton toggleBuildingsFab = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_toggle_buildings);
        toggleBuildingsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClusterHolder.buildings.setSelected(!ClusterHolder.buildings.isSelected());
                ClusterHolder.updateMarkers();
            }
        });

        com.getbase.floatingactionbutton.FloatingActionButton toggleParkingFab = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_toggle_parking);
        toggleParkingFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClusterHolder.parking.setSelected(!ClusterHolder.parking.isSelected());
                ClusterHolder.updateMarkers();
            }
        });

        com.getbase.floatingactionbutton.FloatingActionButton toggleLandmarksFab = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_toggle_landmarks);
        toggleLandmarksFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClusterHolder.landmarks.setSelected(!ClusterHolder.landmarks.isSelected());
                ClusterHolder.updateMarkers();
            }
        });

        com.getbase.floatingactionbutton.FloatingActionButton toggleFoodFab = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_toggle_food);
        toggleFoodFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClusterHolder.food.setSelected(!ClusterHolder.food.isSelected());
                ClusterHolder.updateMarkers();
            }
        });

        floatingActionButton = (android.support.design.widget.FloatingActionButton) findViewById(R.id.location_toggle_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StaticVariables.map != null) {
                    toggleGps(!StaticVariables.map.isMyLocationEnabled());
                }
            }
        });
    }

//    private void getRoute(Position origin, Position destination) throws ServicesException
//    {
//        MapboxDirections client = new MapboxDirections.Builder()
//                .setOrigin(origin)
//                .setDestination(destination)
//                .setProfile(DirectionsCriteria.PROFILE_WALKING)
//                .setAccessToken(Mapbox.getAccessToken())
//                .setSteps(true)
//                .build();
//
//        client.enqueueCall(new Callback<DirectionsResponse>() {
//            @Override
//            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
//                // You can get the generic HTTP info about the response
//                Log.d(TAG, "Response code: " + response.code());
//                if (response.body() == null) {
//                    Log.e(TAG, "No routes found, make sure you set the right user and access token.");
//                    return;
//                } else if (response.body().getRoutes().size() < 1) {
//                    Log.e(TAG, "No routes found");
//                    return;
//                }
//                // Print some info about the curRoute
//                currentRoute = response.body().getRoutes().get(0);
//                Log.d(TAG, "Distance: " + currentRoute.getDistance());
//
//                // Draw the curRoute on the map
//                drawRoute(currentRoute);
//            }
//
//            @Override
//            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
//                Log.e(TAG, "Error: " + throwable.getMessage());
//                Toast.makeText(MainActivity.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void drawRoute(DirectionsRoute route) {
//        // Convert LineString coordinates into LatLng[]
//        LineString lineString = LineString.fromPolyline(route.getGeometry(), 6);
//        List<Position> coordinates = lineString.getCoordinates();
//        LatLng[] points = new LatLng[coordinates.size()];
//        for (int i = 0; i < coordinates.size(); i++) {
//            points[i] = new LatLng(
//                    coordinates.get(i).getLatitude(),
//                    coordinates.get(i).getLongitude());
//        }
//
//        // Draw Points on MapView
//        StaticVariables.map.addPolyline(new PolylineOptions()
//                .add(points)
//                .color(Color.parseColor("#009688"))
//                .width(5));
//    }

    private void toggleGps(boolean enableGps) {
        if (enableGps) {
            // Check if user has granted location permission
            if (!StaticVariables.userLocationEnabled) {
                permissionsManager.requestLocationPermissions(this);
            } else {
                //ClusterHolder.removeMarkers(destinationMarker);
                enableLocation(true);
            }
        } else {
//            List<Polyline> list = StaticVariables.map.getPolylines();
//            for(int i = 0; i < list.size(); i++)
//                StaticVariables.map.removePolyline(list.get(i));
            StaticVariables.map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(34.058800, -117.823601), 14));
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

                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, StaticVariables.PERMISSIONS_REQUEST_LOCATION);
                //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
            } else {
                Location lastLocation = locationEngine.getLastLocation();
                if (lastLocation != null) {
                    StaticVariables.map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation), 16));

                    try {
//                        List<Polyline> list = StaticVariables.map.getPolylines();
//                        for(int i = 0; i < list.size(); i++)
//                            StaticVariables.map.removePolyline(list.get(i));
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
//                        if (location != null)
//                        {
                            // Move the map camera to where the user location is and then remove the
                            // listener so the camera isn't constantly updating when the user location
                            // changes. When the user disables and then enables the location again, this
                            // listener is registered again and will adjust the camera once again.
                            StaticVariables.map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location), 16));
//                            Toast.makeText(MainActivity.this, "Position Updated", Toast.LENGTH_SHORT).show();
                            locationEngine.removeLocationEngineListener(this);
//                            try {
//                                Position origin = Position.fromLngLat(location.getLongitude(), location.getLatitude());
//                                List<Polyline> list = StaticVariables.map.getPolylines();
//                                for(int i = 0; i < list.size(); i++)
//                                    StaticVariables.map.removePolyline(list.get(i));
//                                getRoute(origin, StaticVariables.destination);
//                            } catch (ServicesException se) {
//                               se.printStackTrace();
//                            }
//                        }
                    }
                };
                locationEngine.addLocationEngineListener(locationEngineListener);
                floatingActionButton.setImageResource(R.drawable.ic_location_disabled_24dp);
            }
        } else {
            floatingActionButton.setImageResource(R.drawable.ic_my_location_24dp);
        }
        // Enable or disable the location layer on the map
        StaticVariables.map.setMyLocationEnabled(enabled);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch(item.getItemId()) {
            case R.id.settings:
                Intent SettingsIntent = new Intent(this, Settings.class);
                startActivity(SettingsIntent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
