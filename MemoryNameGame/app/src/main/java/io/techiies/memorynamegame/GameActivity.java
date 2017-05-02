package io.techiies.memorynamegame;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
//import android.R;


public class GameActivity extends AppCompatActivity
{
    protected GameView gameView;      //Used to print the picture of a student
    protected Deck deck;              //Used to hold the students in the selected "Class"
    protected Student student;        //Used to hold the current student the user should be guessing
    protected int attempts;           //Keeps track of how many times it takes the user to complete the "Class"
    protected int correct;            //Keeps track of how many times the user gets a student's name correct
    protected Student lastStudent;    //Keeps track of the last student shown

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        String name = getIntent().getExtras().getString("Class Name");
        createDeck(name);
        attempts = 0;      //Initializes the number of attempts to 0
        correct = 0;    //Initializes the number of correct attempts to 0
        gameView = new GameView(this);  //Creates a new gameView instance
        lastStudent = null;
    }

    public Deck getDeck() {
        return deck;
    }

    public GameView getGameView() {
        return gameView;
    }

    public int getCorrect() {
        return correct;
    }

    public int getAttempts() {
        return attempts;
    }

    public Student getStudent() {
        return student;
    }

    public Student getLastStudent() {
        return lastStudent;
    }

    public void setLastStudent(Student lastStudent) {
        this.lastStudent = lastStudent;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public void setGameView(GameView gameView) {
        this.gameView = gameView;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    //Ends the game and returns back to the main activity, letting the user know how well they did
    public void finishGame()
    {
        setContentView(R.layout.activity_end_game);
        updateCorrect();
        updateAttempts();
        updatePercent();
    }

    public void updateCorrect()
    {
        final EditText et = (EditText) findViewById(R.id.CorrectText);
        final String temp = et.getText().toString();

        android.os.Handler handler = new android.os.Handler();  //Used to pause the activity to give the user a chance to see the correct name
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            et.setText(temp + correct);
            }
        }, 500);   //After 500 milliseconds do the run method above
    }

    public void updateAttempts()
    {
        final EditText et = (EditText) findViewById(R.id.AttemptsText);
        final String temp = et.getText().toString();

        android.os.Handler handler = new android.os.Handler();  //Used to pause the activity to give the user a chance to see the correct name
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                et.setText(temp + attempts);
            }
        }, 1000);   //After 1000 milliseconds do the run method above
    }

    public void updatePercent()
    {
        final EditText et = (EditText) findViewById(R.id.PercentText);
        final String temp = et.getText().toString();
        float percent = ((float) correct / attempts) * 100;
        final String percentage = String.format("%.02f", percent);

        android.os.Handler handler = new android.os.Handler();  //Used to pause the activity to give the user a chance to see the correct name
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                et.setText(temp + percentage);
            }
        }, 1500);   //After 500 milliseconds do the run method above
    }

    public void endGame(View view)
    {
        finish();
    }

    //Used to load the deck that the user wants
    public void createDeck(String name)
    {
        SaveLoad sv = new SaveLoad(name, this);
        deck = sv.load(name);    //Loads the correct deck
    }
}
