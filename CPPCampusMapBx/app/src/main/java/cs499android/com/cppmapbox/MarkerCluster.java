package cs499android.com.cppmapbox;

import android.app.Activity;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by awing_000 on 5/8/2017.
 */

public class MarkerCluster
{
    private ArrayList<Marker> markers;  //List of the markers that are on the map
    private ArrayList<Marker> markersList;  //List of the markers that can be on the map
    private ArrayList<MarkerOptions> markerOptions; //List of markerOptions to create the markers and add them to the map
    private boolean visible;    //If the markers are on the map or not
    private boolean selected;   //If the cluster is selected from the filter or not
    private String name;        //Name of the cluster (i.e. "Buildings")
    private Icon icon;          //Icon used for the cluster
    private Activity activity;
    private String color;       //Color the icon should be

    //Constructor to create a new Cluster of markers
    public MarkerCluster(Activity activity, String name, String color)
    {
        this.activity = activity;
        this.name = name;
        this.color = color;
        setIcon(color); //Sets the icon based on the color
        markers = new ArrayList<>();
        markersList = new ArrayList<>();
        markerOptions = new ArrayList<>();
        visible = false;
        selected = true;
    }

    //Gets the location and information from the json string passed into it
    public void createMarkers(String json)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray features = jsonObject.getJSONArray("features");
            //Gets all locations for the cluster
            for(int i = 0; i < features.length(); i++) {
                JSONObject feature = features.getJSONObject(i);
                JSONObject properties = feature.getJSONObject("properties");    //Object for the name, description and picture
                JSONObject geometry = feature.getJSONObject("geometry");    //Geometry to get coordinates
                JSONArray coords = geometry.getJSONArray("coordinates");    //Coordinates for the location of the marker
                MarkerOptions m = new MarkerOptions();
                m.setTitle(properties.getString("name"));   //Name of the location
                m.setSnippet(properties.getString("description") + "***" + properties.getString("picture"));    //Description and picture of the location
                m.setPosition(new LatLng(coords.getDouble(1), coords.getDouble(0)));    //Position of the marker
                if (icon != null)
                    m.setIcon(icon);    //Sets the icon if it is not null
                markerOptions.add(m);
                markersList.add(new Marker(m)); //Adds the marker to the list of possible markers
                ListHolder.addTo(properties.getString("category"), properties.getString("name"));   //Adds the location to the appropriate search list
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //Adds the markers from this cluster to the map if they should be visible
    public void addMarkers()
    {
        if(name.equals("nearby.geojson"))
        {
            if(markers.size() == 0) {
                for (int i = 0; i < markerOptions.size(); i++)
                    markers.add(new Marker(markerOptions.get(i)));
            }
        }
        else if(!visible) {
            for (int i = 0; i < markerOptions.size(); i++)
                markers.add(StaticVariables.map.addMarker(markerOptions.get(i)));
            visible = true;
        }
    }

    //Removes the visible markers in this cluster from the map
    public void removeMarkers(Marker marker)
    {
        if(visible)
            for(int i = 0; i < markers.size(); i++){
                if(!markers.get(i).equals(marker))
                    StaticVariables.map.removeMarker(markers.get(i));
            }
        visible = false;
    }

    //Sets the icon for the markers based on the color
    public void setIcon(String color)
    {
        IconFactory iconFactory = IconFactory.getInstance(activity);
        if(color.toLowerCase().equals("blue"))
            icon = iconFactory.fromResource(R.drawable.ic_location_on_blue_18dp);
        else if(color.equals("green"))
            icon = iconFactory.fromResource(R.drawable.ic_location_on_green_18dp);
        else if(color.equals("red"))
            icon = iconFactory.fromResource(R.drawable.ic_location_on_red_18dp);
        else if(color.equals("yellow"))
            icon = iconFactory.fromResource(R.drawable.ic_location_on_yellow_18dp);
        else if(color.equals("white"))
            icon = iconFactory.fromResource(R.drawable.ic_location_on_white_18dp);
        else
            icon = iconFactory.defaultMarker();
    }

    public String getColor() {return color;}

    public ArrayList<Marker> getMarkers() {return markers;}

    public ArrayList<Marker> getMarkersList() {return markersList;}

    public boolean isVisible() {return visible;}

    public boolean isSelected() {return selected;}

    public String getName() {return name;}

    public void setColor(String color) {this.color = color;}

    public void setName(String name) {this.name = name;}

    public void setVisible(boolean visible) {this.visible = visible;}

    public void setSelected(boolean selected) {this.selected = selected;}
}
