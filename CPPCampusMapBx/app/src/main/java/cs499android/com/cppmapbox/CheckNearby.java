package cs499android.com.cppmapbox;

import android.util.Log;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.services.api.directions.v5.DirectionsCriteria;
import com.mapbox.services.api.directions.v5.MapboxDirections;
import com.mapbox.services.api.directions.v5.models.DirectionsResponse;
import com.mapbox.services.api.directions.v5.models.DirectionsRoute;
import com.mapbox.services.commons.models.Position;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static cs499android.com.cppmapbox.StaticVariables.TAG;

/**
 * Created by awing_000 on 5/19/2017.
 */

public abstract class CheckNearby
{
    private static ArrayList<Marker> nearby;
    private static ArrayList<Marker> checked;
    private static final int maxDistance = 40;
    private static DirectionsRoute route;
    private static Marker currentMarker;

    protected static void init()
    {
        nearby = ClusterHolder.nearby.getMarkers();
        checked = new ArrayList<>();
    }

    protected static Marker check(Position origin)
    {
        boolean found1 = false;
        double distance;
        if(nearby.size() > 0) {
            for (Marker m : nearby) {
                currentMarker = m;
                if (m.equals(StaticVariables.destinationMarker))
                    continue;
                distance = routeDistance(origin, Position.fromCoordinates(m.getPosition().getLongitude(), m.getPosition().getLatitude()));
                if (distance < maxDistance) {
                    found1 = true;
                    break;
                }
            }
            if(!found1)
                currentMarker = null;
        }
        return currentMarker;
    }

    protected static void updateLists()
    {
        nearby.remove(currentMarker);
        checked.add(currentMarker);
        currentMarker = null;
    }

    protected static Marker getCurrentMarker() {return currentMarker;}

    private static double routeDistance(Position origin, Position destination)
    {
        double distance = maxDistance + 1;
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
                else
                    route = response.body().getRoutes().get(0);
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Log.e(TAG, "Error: " + throwable.getMessage());
            }
        });

        if(route != null)
        {
            distance = route.getDistance();
            route = null;
        }

        return distance;
    }
}
