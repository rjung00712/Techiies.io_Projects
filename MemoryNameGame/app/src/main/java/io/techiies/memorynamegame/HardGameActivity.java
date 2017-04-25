package io.techiies.memorynamegame;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

//Activity to play the game in hard mode
public class HardGameActivity extends AppCompatActivity
{
    private GameView gameView;      //Used to print the picture of a student
    private Deck deck;              //Used to hold the students in the selected "Class"
    private Student student;        //Used to hold the current student the user should be guessing
    private int tries;              //Keeps track of how many times it takes the user to complete the "Class"
    private int correct;            //Keeps track of how many times the user gets a student's name correct

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hard_game);    //Sets the view to the hard game view
        ConstraintLayout container = (ConstraintLayout) findViewById(R.id.hard_game_container);

        tries = 0;      //Initializes the number of tries to 0
        correct = 0;    //Initializes the number of correct tries to 0
        gameView = new GameView(this);  //Creates a new gameView instance
        gameView.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT));
        container.addView(gameView);    //Adds this view to the screen
        ////////////////////////Load a Deck from the saved games////////////////////////////
        deck = new Deck();
        showStudent();      //Shows the first student that the user needs to guess
    }

    //Method to check if the user entered the correct name
    public void check(View v)
    {
        final EditText editText = (EditText) findViewById(R.id.name_guess);
        String userGuess = editText.getText().toString();   //Gets the name the user entered

        //Checks to see if the user entered the students name
        if(userGuess.equals(student.getName()))
        {
            tries++;    //Increases the number of tries by 1
            correct++;  //Increases the number of correct tries by 1
            deck.addStudent(student, true); //Adds the student back to the deck
            Toast.makeText(HardGameActivity.this, "That is correct!", Toast.LENGTH_SHORT).show();
            editText.setText("");   //Resets the text box where the user enters names
            //Checks to see if there are still students the user hasn't named
            if(deck.getStudentsLength() > 0)
                showStudent();  //Shows a new student the user has yet to name
            else
                finishGame();   //Ends the Hard Mode activity
        }
        //The user entered the wrong name
        else
        {
            tries++;    //Increases the number of tries by 1
            deck.addStudent(student, false);    //Adds the student back to the deck
            Toast.makeText(HardGameActivity.this, "That is incorrect! The correct name is " + student.getName(), Toast.LENGTH_SHORT).show();    //Tells the user the student's name
            showStudent();  //Shows a new student the user has yet to name
        }
    }

    //Shows a student that the user has yet to name properly
    public void showStudent()
    {
        student = deck.getRandomStudent();  //Gets a new student
        gameView.setView(student);  //Adds this student to the gameView so the picture prints
    }

    //Ends the Hard Mode activity and returns to the main screen letting the user know how well they did
    public void finishGame()
    {
        deck.resetClass();
        Toast.makeText(HardGameActivity.this, "Congradulations, you completed the game with " + ((double)correct/ tries) * 100 + "% accuracy!", Toast.LENGTH_LONG).show();
        finish();
    }
}