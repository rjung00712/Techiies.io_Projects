package io.techies.com.puzzle_8;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Richard on 4/3/17.
 */

public class PuzzleBoardView extends View implements Serializable {
    public static final int NUM_SHUFFLE_STEPS = 120;    //Used to shuffle the board 120 times

    private Activity activity;
    private PuzzleBoard puzzleBoard;    //Holds the board
    private ArrayList<PuzzleBoard> animation;
    private int moveCounter;    //Keeps track of the number of moves the user takes
    private PuzzleActivity puzzleActivity = (PuzzleActivity) getContext();  //Used to update and print the moveCounter

    private String userName;    //Used to store a high score in the leaderboard

    public static List<Player> listOfPlayers;    //List of all players

    public PuzzleBoardView(Context context) {
        super(context);
        activity = (Activity) context;
        animation = null;
        moveCounter = 0;        //Initialize counter to 0
        listOfPlayers = new ArrayList<>(10);    //Only holding 10 values
        retrieveLeaderboardList(context);
    }


    public void initialize(Bitmap imageBitmap, String userName) {
        Log.i("this is width", String.valueOf(getWidth()));

        int width = getWidth();

        this.userName = userName;

        puzzleBoard = new PuzzleBoard(imageBitmap, width);
        invalidate();
    }

/////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (puzzleBoard != null) {
            if (animation != null && animation.size() > 0) {
                puzzleBoard = animation.remove(0);
                puzzleBoard.draw(canvas);
                if (animation.size() == 0) {
                    animation = null;
                    puzzleBoard.reset();
                    Toast toast = Toast.makeText(activity, "Solved! ", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    this.postInvalidateDelayed(500);
                }
            } else {
                puzzleBoard.draw(canvas);
            }
        }
    }

    //Shuffles the tiles on the board
    public void shuffle() {
        puzzleBoard.shuffle(NUM_SHUFFLE_STEPS);
        puzzleBoard.reset();    //Resets the values
        setMoveCounter(0);      //Sets moveCounter to 0
        puzzleActivity.moveCounterText.setText("" + moveCounter);   //Prints out the move counter
        invalidate();       //////////////////////////////////////////////////////////////////
    }

    //When screen is touched
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (animation == null && puzzleBoard != null) {
            switch (event.getAction()) {
                //Screen is touched
                case MotionEvent.ACTION_DOWN:
                    //Checks to see if a tile is clicked
                    if (puzzleBoard.click(event.getX(), event.getY())) {
                        moveCounter++;  //increments counter
                        puzzleActivity.moveCounterText.setText("" + moveCounter);   //Reprints value
                        invalidate();
                        //Checks to see if the puzzle is solved
                        if (puzzleBoard.resolved()) {
                            //Prints out a Congratulations
                            Toast toast = Toast.makeText(activity, "Congratulations You solved it!", Toast.LENGTH_LONG);
                            toast.show();
                            //Checks to see if score makes it to the leaderboard (top 10 only)
                            if(listOfPlayers.size() < 10 || listOfPlayers.get(9).getMoves() > moveCounter)
                            {
                                //Removes the lowest score to make room for the new score
                                if(listOfPlayers.size() == 10)
                                    listOfPlayers.remove(9);
                                puzzleActivity.createAlert();   //Gets the user's name and adds the score
                            }
                        }
                        return true;
                    }
            }
        }
        return super.onTouchEvent(event);
    }

    public void retrieveLeaderboardList(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("list", null);
        Type type = new TypeToken<ArrayList<Player>>() {}.getType();
        if(json != null) {
            listOfPlayers = gson.fromJson(json, type);
        }
    }

    //Gets the moveCounter value
    public int getMoveCounter(){return moveCounter;}

    //Sets the moveCounter value
    public void setMoveCounter(int i) { moveCounter = i;}

    public PuzzleBoard getPuzzleBoard() {return puzzleBoard;}

    public void setPuzzleBoard(PuzzleBoard p) {puzzleBoard = p;}
}
