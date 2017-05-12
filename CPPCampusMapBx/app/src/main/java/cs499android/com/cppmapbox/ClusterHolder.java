package cs499android.com.cppmapbox;

import android.app.Activity;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import java.io.InputStream;

/**
 * Created by awing_000 on 5/11/2017.
 */

public abstract class ClusterHolder
{
    protected static Activity activity;
    protected static MarkerCluster buildings;
    protected static MarkerCluster landmarks;
    protected static MarkerCluster parking;
    protected static MarkerCluster food;
    //private MarkerCluster bathrooms;

    protected static void createMarkers()
    {
        buildings = new MarkerCluster(activity, "cpp_buildings.geojson", "red");
        landmarks = new MarkerCluster(activity, "landmarks.geojson", "green");
        parking = new MarkerCluster(activity, "parking.geojson", "blue");
        food = new MarkerCluster(activity, "food_places.geojson", "yellow");
        createMarkers(buildings);
        createMarkers(landmarks);
        createMarkers(parking);
        createMarkers(food);
    }
    private static void createMarkers(MarkerCluster markerCluster) {
        String json = null;
        try {

            InputStream is = activity.getAssets().open(markerCluster.getName());

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");

            if(json != null)
            {
                markerCluster.createMarkers(json);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected static void addMarkers()
    {
        try{
            if(buildings.isSelected())
                buildings.addMarkers();
            if(landmarks.isSelected())
                landmarks.addMarkers();
            if(parking.isSelected())
                parking.addMarkers();
            if(food.isSelected())
                food.addMarkers();
        }catch (NullPointerException ex)
        {
            ex.printStackTrace();
        }
    }

    protected static void removeMarkers(Marker marker)
    {
        try{
            buildings.removeMarkers(marker);
            landmarks.removeMarkers(marker);
            parking.removeMarkers(marker);
            food.removeMarkers(marker);
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    protected static void updateMarkers()
    {
        removeMarkers(null);
        addMarkers();
    }
}
