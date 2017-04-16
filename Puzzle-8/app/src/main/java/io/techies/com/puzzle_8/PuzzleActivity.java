package io.techies.com.puzzle_8;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class PuzzleActivity extends AppCompatActivity implements Serializable {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap imageBitmap = null;
    private PuzzleBoardView boardView;
    public TextView moveCounterText;
    public String userName;
    private ArrayList<Integer> order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        // This code programmatically adds the PuzzleBoardView to the UI.
        RelativeLayout container = (RelativeLayout) findViewById(R.id.puzzle_container);

        boardView = new PuzzleBoardView(this);
        moveCounterText = (TextView) findViewById(R.id.MoveCounter);
        moveCounterText.setEms(3);

        // Some setup of the view.
        boardView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        container.addView(boardView);

        // put number of moves into the text box
        moveCounterText.setText(Integer.toString(boardView.getMoveCounter()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_puzzle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement2
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void dispatchTakePictureIntent(View view) {  // handler for the "Take photo" button
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        // maybe add shuffle here when ever a new picture is taken (optional)
    }

    // calls puzzle board activity to handle the leader board display intent
    public void displayLeaderBoardIntent(View view) {
        Intent leaderBoardIntent = new Intent(this, LeaderBoard.class);
        startActivity(leaderBoardIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();

            imageBitmap = (Bitmap) extras.get("data");
            boardView.initialize(imageBitmap, userName);
//            imageView.setImageBitmap(imageBitmap);
            shuffleImage(boardView);
        }
    }

    public void shuffleImage(View view) {
        boardView.shuffle();
    }

    public void save(View v)
    {
        if(imageBitmap != null) {
            new ImageSaver(this).
                    setFileName("myImage.png").
                    setDirectoryName("images").
                    save(imageBitmap);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();

            ArrayList<PuzzleTile> list = boardView.getPuzzleBoard().getTiles();
            order = new ArrayList<>(10);

            for (int i = 0; i < 9; i++) {
                PuzzleTile tile = list.get(i);
                if (tile != null)
                    order.add(tile.getNumber());
                else
                    order.add(-1);
            }
            order.add(boardView.getMoveCounter());
            String json = gson.toJson(order);

            editor.putString("order", json);
            editor.commit();
        }
        else
        {
            Toast toast = Toast.makeText(this, "Nothing to save", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void load(View v)
    {
        imageBitmap = new ImageSaver(this).
                setFileName("myImage.png").
                setDirectoryName("images").
                load();
        if(imageBitmap != null)
        {
            boardView.initialize(imageBitmap, userName);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            Gson gson = new Gson();
            String json = sharedPreferences.getString("order", null);
            Type type = new TypeToken<ArrayList<Integer>>() {
            }.getType();
            if (json != null) {
                order = gson.fromJson(json, type);
                ArrayList<PuzzleTile> tiles = new ArrayList<>(9);
                ArrayList<PuzzleTile> Tiles = boardView.getPuzzleBoard().getTiles();
                for (int i = 0; i < 9; i++) {
                    if (order.get(i) == -1)
                        tiles.add(Tiles.get(boardView.getPuzzleBoard().getIndex(null)));
                    else
                        tiles.add(Tiles.get(boardView.getPuzzleBoard().getIndex(new PuzzleTile(null, order.get(i)))));
                    //boardView.getPuzzleBoard().swapTiles(i, boardView.getPuzzleBoard().getIndex(new PuzzleTile(null, order.get(i))));
                }
                boardView.getPuzzleBoard().setTiles(tiles);
                boardView.setMoveCounter(order.get(9));
                moveCounterText.setText(Integer.toString(boardView.getMoveCounter()));
                boardView.invalidate();
            }
        }
        else
        {
            Toast toast = Toast.makeText(this, "No Save Game", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public int getMoveCounter(View view) { return boardView.getMoveCounter();}

    // creates custom alert dialog box for username input
    public void createAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        View mView = inflater.inflate(R.layout.user_name, null);
        final EditText editText = (EditText) mView.findViewById(R.id.username);

        // inflate and set the layout for the dialog
        // pass null as a parent view because its going in the dialog layout
        builder.setView(mView)
            .setPositiveButton("enter", new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    userName = editText.getText().toString();

                    if(userName.equals("")) {
                        Toast.makeText(PuzzleActivity.this, "must enter a username", Toast.LENGTH_SHORT).show();
                        createAlert();
                    } else {
                        boardView.listOfPlayers.add(new Player(userName, boardView.getMoveCounter()));
                        Collections.sort(boardView.listOfPlayers);
                        boardView.setMoveCounter(0);
                    }
                }
            });
        builder.setCancelable(false);
        builder.show();
    }
}