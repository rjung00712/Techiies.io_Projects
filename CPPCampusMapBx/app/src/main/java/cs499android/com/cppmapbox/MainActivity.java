package cs499android.com.cppmapbox;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
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
                initPolygons();
                ClusterHolder.createMarkers();
                ClusterHolder.buildings.setSelected(false);
                ClusterHolder.food.setSelected(false);
                ClusterHolder.parking.setSelected(false);
                ClusterHolder.bathrooms.setSelected(false);
                ClusterHolder.addMarkers();
                StaticVariables.map.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        StaticVariables.destination = Position.fromCoordinates(marker.getPosition().getLongitude(), marker.getPosition().getLatitude());
                        StaticVariables.destinationMarker = marker;
                        Intent MarkerSelectedIntent = new Intent(MainActivity.this, MarkerSelected.class);
                        MarkerSelectedIntent.putExtra("Title", marker.getTitle());
                        MarkerSelectedIntent.putExtra("Description", marker.getSnippet());
                        MarkerSelectedIntent.putExtra("Type", "Navigate");
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

    private void initPolygons()
    {
        parseJSONFile();
    }

    private void parseJSONFile()
    {
        String json = null;
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
                    JSONArray features = jsonObject.getJSONArray("features");
                    for(int i = 0; i < features.length(); i++) {
                        JSONObject feature = features.getJSONObject(i);
                        JSONObject geometry = feature.getJSONObject("geometry");
                        JSONArray coordinates = geometry.getJSONArray("coordinates");
                        JSONArray coords = (JSONArray)coordinates.get(0);
                        List<LatLng> polygon = new ArrayList<>();
                        List<Position> positions = new ArrayList<>();
                        for(int j = 0; j < coords.length(); j++)
                        {
                            JSONArray latLng = coords.getJSONArray(j);
                            polygon.add(new LatLng(latLng.getDouble(1), latLng.getDouble(0)));
                            positions.add(Position.fromCoordinates(latLng.getDouble(0), latLng.getDouble(1)));
                        }
                        StaticVariables.polygons.add(polygon);
                        StaticVariables.positions.add(positions);
                        //StaticVariables.map.addPolygon(new PolygonOptions().fillColor(Color.parseColor("#FF8DD0FF")).addAll(polygon));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

        com.getbase.floatingactionbutton.FloatingActionButton toggleBathroomsFab = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_toggle_bathrooms);
        toggleBathroomsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClusterHolder.bathrooms.setSelected(!ClusterHolder.bathrooms.isSelected());
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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        StaticVariables.speakDescriptions = sharedPreferences.getBoolean("speakDescriptions", true);
        StaticVariables.speakDirections = sharedPreferences.getBoolean("speakDirections", true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
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
        getMenuInflater().inflate(R.menu.main_menu, menu);

        /*SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item); //item.getActionView();

        searchView.setSearchableInfo( searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getResources().getString(R.string.hint));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.w("myApp", "onQueryTextSubmit ");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                adapter.getFilter().filter(newText);
                Log.w("myApp", "onQueryTextChange ");
                return false;
            }
        });*/


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

      /*  switch(item.getItemId()) {

            case R.id.nav_admin:
                Intent AdminIntent = new Intent(MainActivity.this, Admini.class);
                startActivity(AdminIntent);


            case R.id.nav_build:
                Intent BuildIntent = new Intent(MainActivity.this, Buildings.class);
                startActivity(BuildIntent);

            case R.id.nav_dorm:
                Intent DormIntent = new Intent(MainActivity.this, Dorm.class);
                startActivity(DormIntent);

            case R.id.nav_food:
                Intent FoodIntent = new Intent(MainActivity.this, Food.class);
                startActivity(FoodIntent);

            case R.id.nav_land:
                Intent LandIntent = new Intent(MainActivity.this, Land.class);
                startActivity(LandIntent);

            case R.id.nav_park:
                Intent ParkingIntent = new Intent(MainActivity.this, Park.class);
                startActivity(ParkingIntent);

            case R.id.settings:
                Intent SettingsIntent = new Intent(this, Settings.class);
                startActivity(SettingsIntent);

            default:*/
                return super.onOptionsItemSelected(item);
        //}
    }


}
