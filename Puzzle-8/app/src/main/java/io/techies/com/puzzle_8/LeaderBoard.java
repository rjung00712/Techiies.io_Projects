package io.techies.com.puzzle_8;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class LeaderBoard extends ListActivity {

    private List<Player> listOfLeaders;    //Holds a list of the top ten scores
    private ListView listView;
    private ArrayList<String> leaderList;   //Converts the list of players into a list of Strings for save and load later

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        listOfLeaders = PuzzleBoardView.listOfPlayers;  //Gets the list created from the PuzzleBoardView class
        leaderList = new ArrayList<>(); //Creates a new list

        listView = (ListView) findViewById(android.R.id.list);

        //Turns all players to strings of their name and number of moves
        for(Player p : listOfLeaders) {
            leaderList.add(p.getUserName() + "\t\t\t\t\t" + p.getMoves());
        }

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.list_item, leaderList);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        String json = gson.toJson(listOfLeaders);

        editor.putString("list", json);
        editor.commit();
    }
}
