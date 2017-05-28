package cs499android.com.cppmapbox;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.services.commons.models.Position;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by pungi on 19-May-17.
 */

public class Admini extends AppCompatActivity{

    ArrayAdapter<String> adapter;
    TextView tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_listview);

       


       final ListView l = (ListView) findViewById(R.id.ListView);

       l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               Marker marker = ClusterHolder.getMarker(adapter.getItem(position));

               StaticVariables.destinationMarker = marker;
               StaticVariables.destination = Position.fromCoordinates(marker.getPosition().getLongitude(), marker.getPosition().getLatitude());
               Intent placeSelectedIntent = new Intent(Admini.this, MarkerSelected.class);
               placeSelectedIntent.putExtra("Title", marker.getTitle());
               placeSelectedIntent.putExtra("Description", marker.getSnippet());
               placeSelectedIntent.putExtra("Type", "Navigate");
               startActivity(placeSelectedIntent);
               finish();
           }
       });

       list();
   }


    public void list() {

        ListView lv = (ListView) findViewById(R.id.ListView);
        ArrayList<String> arrayAdmini = ListHolder.Admin;

        //admi is an array string of all the buildings. It's under value->strings
//        arrayAdmini.addAll(Arrays.asList(getResources()
//                .getStringArray(R.array.admi)));

        adapter = new ArrayAdapter<String>(
                Admini.this,
                R.layout.listview_items,
                R.id.textview,
                arrayAdmini);

        lv.setAdapter(adapter);
    }

    //implementing a search bar

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Handle the search bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        Log.w("myApp", "onCreateOptionsMenu -started- ");

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

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

                tv = (TextView) findViewById(R.id.textview);
                 tv.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         Intent placeSelectedIntent = new Intent(Admini.this, MarkerSelected.class);
                         //MarkerSelectedIntent.putExtra("Title", marker.getTitle());
                         //MarkerSelectedIntent.putExtra("Description", marker.getSnippet());
                         startActivity(placeSelectedIntent);
                     }
                 });

                Log.w("myApp", "onQueryTextChange ");
                return false;
            }
        });


        return true; //super.onCreateOptionsMenu(menu);
    }
}
