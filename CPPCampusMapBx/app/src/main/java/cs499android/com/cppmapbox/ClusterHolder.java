package cs499android.com.cppmapbox;

import android.app.Activity;

import com.mapbox.mapboxsdk.annotations.Marker;

import java.io.InputStream;

/**
 * Created by awing_000 on 5/11/2017.
 */

public abstract class ClusterHolder
{
    protected static Activity activity;
    //Clusters for the specific filters
    protected static MarkerCluster buildings;
    protected static MarkerCluster landmarks;
    protected static MarkerCluster parking;
    protected static MarkerCluster food;
    protected static MarkerCluster bathrooms;
    protected static MarkerCluster nearby;

    //Creates all of the clusters
    protected static void createMarkers()
    {
        buildings = new MarkerCluster(activity, "cpp_buildings.geojson", "red");
        landmarks = new MarkerCluster(activity, "landmarks.geojson", "green");
        parking = new MarkerCluster(activity, "parking.geojson", "blue");
        food = new MarkerCluster(activity, "food_places.geojson", "yellow");
        bathrooms = new MarkerCluster(activity, "bathrooms.geojson", "white");
        nearby = new MarkerCluster(activity, "nearby.geojson", "");
        createMarkers(buildings);
        createMarkers(landmarks);
        createMarkers(parking);
        createMarkers(food);
        createMarkers(bathrooms);
        createMarkers(nearby);
    }

    //Creates a json string from the geojson files
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

    //Adds the markers to the map if the cluster is selected from the filter
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
            if(bathrooms.isSelected())
                bathrooms.addMarkers();
            nearby.addMarkers();
        }catch (NullPointerException ex)
        {
            ex.printStackTrace();
        }
    }

    //Removes the clusters from the map
    protected static void removeMarkers(Marker marker)
    {
        try{
            buildings.removeMarkers(marker);
            landmarks.removeMarkers(marker);
            parking.removeMarkers(marker);
            food.removeMarkers(marker);
            bathrooms.removeMarkers(marker);
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    //Removes and then adds the marker cluster to the map
    protected static void updateMarkers()
    {
        removeMarkers(null);
        addMarkers();
    }

    //Gets a specific marker based on the name
    protected static Marker getMarker(String name, String type)
    {
        if(type.equals("building"))
            for(Marker m : buildings.getMarkersList())
                if(m.getTitle().equals(name))
                    return m;
        if(type.equals("landmark"))
            for(Marker m : landmarks.getMarkersList())
                if(m.getTitle().equals(name))
                    return m;
        if(type.equals("parking"))
            for(Marker m : parking.getMarkersList())
                if(m.getTitle().equals(name))
                    return m;
        if(type.equals("food"))
            for(Marker m : food.getMarkersList())
                if(m.getTitle().equals(name))
                    return m;
        if(type.equals("bathroom"))
            for(Marker m : bathrooms.getMarkersList())
                if(m.getTitle().equals(name))
                    return m;
        return null;
    }
}
