package io.techiies.memorynamegame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void showGridView() {

        String[] classList = {"testing", "testing", "cs 480", "cs 499"};

        GridView gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(new GridViewAdapter(this, classList));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                System.out.println("whowoh");
            }
        });

        Intent easyModeIntent = new Intent(MainActivity.this, EasyGameActivity.class);
        startActivity(easyModeIntent);

    }

    //Method to create a new "Class" Executed when the "New Class" button is pressed
    public void createNewClass(View v)
    {
        Intent deckCreatorIntent = new Intent(MainActivity.this, DeckCreator.class);
        startActivity(deckCreatorIntent);
    }

    //Method to play the game in easy mode. Executed when the "Play on Easy" button is pressed
    public void playGameEasy(View v)
    {
        showGridView();
    }

    //Method to play the game in hard mode. Executed when the "Play on Hard" button is pressed
    public void playGameHard(View v)
    {
        showGridView();

        Intent hardModeIntent = new Intent(MainActivity.this, HardGameActivity.class);
        startActivity(hardModeIntent);
    }
}