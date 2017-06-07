package cs499android.com.cppmapbox;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.mapbox.services.commons.models.Position;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static cs499android.com.cppmapbox.StaticVariables.BASE_URL;

@SuppressWarnings( {"MissingPermission"})
public class MainActivity extends AppCompatActivity implements PermissionsListener
{
    private MapView mapView;    //View that shows the map

    private LocationEngine locationEngine;  //Used for the user's location
    private LocationEngineListener locationEngineListener;  //Used for when the user's location is updated
    private PermissionsManager permissionsManager;  //Used to get the permissions that are needed

    private android.support.design.widget.FloatingActionButton floatingActionButton;    //Toggles on and off the user's location

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

        final MapboxNavigation navigation = new MapboxNavigation(this, StaticVariables.MAPBOX_ACCESS_TOKEN);

        // This contains the MapView in XML and needs to be called after the account manager
        setContentView(R.layout.activity_main);

        // Get the location engine object for later use.
        locationEngine = LocationSource.getLocationEngine(this);
        locationEngine.activate();

        // Setup the MapView
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        ClusterHolder.activity = MainActivity.this;
        setPermissions();   //Set the permissions that are needed

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                StaticVariables.map = mapboxMap;
                initPolygons();     //Creates the list of polygons of all locations
                ClusterHolder.createMarkers();  //Gets all of the marker clusters created
                ClusterHolder.buildings.setSelected(false); //Do not show the buildings
                ClusterHolder.food.setSelected(false);  //Do not show the food places
                ClusterHolder.parking.setSelected(false);   //Do not show the parking lots/structures
                ClusterHolder.bathrooms.setSelected(false); //Do not show the bathrooms
                ClusterHolder.addMarkers(); //Add the markers that should be shown to the map
                //Start the markerSelected activity when a marker is clicked. Don't show any info window when a marker is clicked
                StaticVariables.map.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        //Sets the destination to the selected marker
                        StaticVariables.destination = Position.fromCoordinates(marker.getPosition().getLongitude(), marker.getPosition().getLatitude());
                        StaticVariables.destinationMarker = marker;
                        Intent MarkerSelectedIntent = new Intent(MainActivity.this, MarkerSelected.class);
                        MarkerSelectedIntent.putExtra("Title", marker.getTitle());  //Passes the name of the location selected
                        MarkerSelectedIntent.putExtra("Description", marker.getSnippet());  //Passes the snippet of the location selected
                        MarkerSelectedIntent.putExtra("Type", "Navigate");  //Passes navigation as the type
                        startActivity(MarkerSelectedIntent);
                        //No info windo will show up
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

                setFloatingButtons();   //Sets up all of the floatingActionButtons
            }
        });
    }

    //Initializes the list of polygons
    private void initPolygons()
    {
        parseJSONFile();
    }

    //Parses the geojson file for the polygons
    private void parseJSONFile()
    {
        String json;
        try {

            InputStream is = this.getAssets().open("polygons.geojson");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");

            if(json != null)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray features = jsonObject.getJSONArray("features");   //Gets each location as a feature from the geojson file
                    //iterates through each location
                    for(int i = 0; i < features.length(); i++) {
                        JSONObject feature = features.getJSONObject(i);
                        JSONObject geometry = feature.getJSONObject("geometry");    //Gets the geometry of the location
                        JSONArray coordinates = geometry.getJSONArray("coordinates");   //Gets the list of coordinates of the location
                        JSONArray coords = (JSONArray)coordinates.get(0);
                        List<LatLng> polygon = new ArrayList<>();
                        List<Position> positions = new ArrayList<>();
                        //Iterates through the list of coordinates
                        for(int j = 0; j < coords.length(); j++)
                        {
                            JSONArray latLng = coords.getJSONArray(j); //Gets the latLng points at the current position in the array of coordinates
                            polygon.add(new LatLng(latLng.getDouble(1), latLng.getDouble(0)));  //Adds the point to the polygon list
                            positions.add(Position.fromCoordinates(latLng.getDouble(0), latLng.getDouble(1)));  //Creates a position from the point and adds it to the position list
                        }
                        StaticVariables.polygons.add(polygon);  //Adds the polygon
                        StaticVariables.positions.add(positions);   //Adds the list of positions
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //Sets the FloatingActionButtons to the appropriate onClickListeners
    private void setFloatingButtons()
    {
        //Toggles the buildings markers
        com.getbase.floatingactionbutton.FloatingActionButton toggleBuildingsFab = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_toggle_buildings);
        toggleBuildingsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClusterHolder.buildings.setSelected(!ClusterHolder.buildings.isSelected());
                ClusterHolder.updateMarkers();
            }
        });

        //Toggles the parking lot markers
        com.getbase.floatingactionbutton.FloatingActionButton toggleParkingFab = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_toggle_parking);
        toggleParkingFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClusterHolder.parking.setSelected(!ClusterHolder.parking.isSelected());
                ClusterHolder.updateMarkers();
            }
        });

        //Toggles the landmark markers
        com.getbase.floatingactionbutton.FloatingActionButton toggleLandmarksFab = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_toggle_landmarks);
        toggleLandmarksFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClusterHolder.landmarks.setSelected(!ClusterHolder.landmarks.isSelected());
                ClusterHolder.updateMarkers();
            }
        });

        //Toggles the food places markers
        com.getbase.floatingactionbutton.FloatingActionButton toggleFoodFab = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_toggle_food);
        toggleFoodFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClusterHolder.food.setSelected(!ClusterHolder.food.isSelected());
                ClusterHolder.updateMarkers();
            }
        });

        //Toggles the bathroom markers
        com.getbase.floatingactionbutton.FloatingActionButton toggleBathroomsFab = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_toggle_bathrooms);
        toggleBathroomsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClusterHolder.bathrooms.setSelected(!ClusterHolder.bathrooms.isSelected());
                ClusterHolder.updateMarkers();
            }
        });

        //Toggles the user's location
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

    //Toggles the user's location
    private void toggleGps(boolean enableGps) {
        if (enableGps) {
            // Check if user has granted location permission
            if (!StaticVariables.userLocationEnabled) {
                permissionsManager.requestLocationPermissions(this);
            } else {
                enableLocation(true);
            }
        } else {
            StaticVariables.map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(34.058800, -117.823601), 14));
            enableLocation(false);
        }
    }

    //Enables the location of the user if passed true
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
                            // Move the map camera to where the user location is and then remove the
                            // listener so the camera isn't constantly updating when the user location
                            // changes. When the user disables and then enables the location again, this
                            // listener is registered again and will adjust the camera once again.
                            StaticVariables.map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location), 16));
                            locationEngine.removeLocationEngineListener(this);
                    }
                };
                locationEngine.addLocationEngineListener(locationEngineListener);
                floatingActionButton.setImageResource(R.drawable.ic_location_disabled_24dp);    //Change the icon for the location button
            }
        } else {
            floatingActionButton.setImageResource(R.drawable.ic_my_location_24dp);  //Change the icon for the location button
        }
        // Enable or disable the location layer on the map
        StaticVariables.map.setMyLocationEnabled(enabled);  //Show the user as a marker on the map
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
        //Load the settings from SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        StaticVariables.speakDescriptions = sharedPreferences.getBoolean("speakDescriptions", true);
        StaticVariables.speakDirections = sharedPreferences.getBoolean("speakDirections", true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        //Save the settings to SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putBoolean("speakDescriptions", StaticVariables.speakDescriptions).commit();
        sharedPreferences.edit().putBoolean("speakDirections", StaticVariables.speakDirections).commit();
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

        // Ensure no memory leak occurs if we register the location listener but the call hasn't been made yet.
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
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.nav_admin) {
            Intent intent = new Intent(MainActivity.this, Admini.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.nav_build) {
            Intent intent = new Intent(MainActivity.this, Buildings.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.nav_dorm) {
            Intent intent = new Intent(MainActivity.this, Dorm.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.nav_food) {
            Intent intent = new Intent(MainActivity.this, Food.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.nav_land) {
            Intent intent = new Intent(MainActivity.this, Land.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.nav_park) {
            Intent intent = new Intent(MainActivity.this, Park.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.nav_bath) {
            Intent intent = new Intent(MainActivity.this, Bathroom.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.settings){
            Intent SettingsIntent = new Intent(this, Settings.class);
            startActivity(SettingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
