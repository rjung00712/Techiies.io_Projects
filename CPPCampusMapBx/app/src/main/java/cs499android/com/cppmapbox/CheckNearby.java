package cs499android.com.cppmapbox;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.services.commons.models.Position;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by awing_000 on 5/19/2017.
 */

public abstract class CheckNearby
{
    private static ArrayList<Marker> nearby;
    private static ArrayList<Marker> checked;
    private static final int maxDistance = 50;

    protected static void init()
    {
        nearby = ClusterHolder.nearby.getMarkers();
        checked = new ArrayList<>();
    }

    protected static void check(Position origin)
    {
        double distance;
        for(Marker m : nearby)
        {
            if(m.equals(StaticVariables.destinationMarker))
                continue;
            distance = routeDistance(origin, Position.fromCoordinates(m.getPosition().getLongitude(), m.getPosition().getLatitude()));
            if(distance < maxDistance)
            {
                //Start Activity to show the nearby location
                break;
            }
        }
    }

    private static double routeDistance(Position origin, Position destination)
    {

        return 0.0;
    }
}
