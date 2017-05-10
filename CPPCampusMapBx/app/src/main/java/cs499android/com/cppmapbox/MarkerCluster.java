package cs499android.com.cppmapbox;

import android.app.Activity;
import android.content.Context;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by awing_000 on 5/8/2017.
 */

public class MarkerCluster
{
    private ArrayList<Marker> markers;
    private ArrayList<MarkerOptions> markerOptions;
    private boolean visible;
    private String name;
    private Icon icon;
    private Activity activity;
    private String color;
    private MapboxMap map;

    public MarkerCluster(Activity activity, String name, String color, MapboxMap map)
    {
        this.activity = activity;
        this.name = name;
        this.color = color;
        this.map = map;
        setIcon(color);
        markers = new ArrayList<>();
        markerOptions = new ArrayList<>();
        visible = false;
    }

    public void createMarkers(String json)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray features = jsonObject.getJSONArray("features");
            for(int i = 0; i < features.length(); i++) {
                JSONObject feature = features.getJSONObject(i);
                JSONObject properties = feature.getJSONObject("properties");
                JSONObject geometry = feature.getJSONObject("geometry");
                JSONArray coords = geometry.getJSONArray("coordinates");
                MarkerOptions m = new MarkerOptions();
                m.setTitle(properties.getString("name"));
                m.setSnippet(properties.getString("description"));
                m.setPosition(new LatLng(coords.getDouble(1), coords.getDouble(0)));
                if (icon != null)
                    m.setIcon(icon);
                markerOptions.add(m);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addMarkers()
    {
        if(!visible)
            for (int i = 0; i < markerOptions.size(); i++)
                markers.add(map.addMarker(markerOptions.get(i)));
        visible = true;
    }

    public void removeMarkers(Marker marker)
    {
        if(visible)
            for(int i = 0; i < markers.size(); i++){
                if(!markers.get(i).equals(marker))
                    map.removeMarker(markers.get(i));
            }
        visible = false;
    }

    public void setIcon(String color)
    {
        IconFactory iconFactory = IconFactory.getInstance(activity);
        if(color.toLowerCase().equals("blue"))
            icon = iconFactory.fromResource(R.drawable.blue_marker);
        else if(color.equals("green"))
            icon = iconFactory.fromResource(R.drawable.green_marker);
        else if(color.equals("red"))
            icon = iconFactory.fromResource(R.drawable.red_marker);
        else if(color.equals("yellow"))
            icon = iconFactory.fromResource(R.drawable.yellow_marker);
    }

    public String getColor() {return color;}

    public ArrayList<Marker> getMarkers() {return markers;}

    public boolean isVisible() {return visible;}

    public String getName() {return name;}

    public void setColor(String color) {this.color = color;}

    public void setName(String name) {this.name = name;}

    public void setVisible(boolean visible) {this.visible = visible;}
}
