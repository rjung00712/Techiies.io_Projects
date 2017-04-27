package io.techiies.memorynamegame;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

        deck = new Deck();
        tries = 0;      //Initializes the number of tries to 0
        correct = 0;    //Initializes the number of correct tries to 0
        gameView = new GameView(this);  //Creates a new gameView instance
        gameView.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT));
        container.addView(gameView);    //Adds this view to the screen
        //Load a Deck from the saved games
        createAlertClass();
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
            deck.addStudent(student, true, false); //Adds the student back to the deck
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
            deck.addStudent(student, false, false);    //Adds the student back to the deck
            Toast.makeText(HardGameActivity.this, "That is incorrect! The correct name is " + student.getName(), Toast.LENGTH_SHORT).show();    //Tells the user the student's name
            showStudent();  //Shows a new student the user has yet to name
        }
    }

    // creates custom alert dialog box for class name input
    public void createAlertClass()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        View mView = inflater.inflate(R.layout.class_name, null);
        final EditText editText = (EditText) mView.findViewById(R.id.name);

        // inflate and set the layout for the dialog
        // pass null as a parent view because its going in the dialog layout
        builder.setView(mView)
                .setPositiveButton("enter", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        deck.setClassName(editText.getText().toString());

                        //Make sure that a class name is entered
                        if(deck.getClassName().equals("")) {
                            Toast.makeText(HardGameActivity.this, "Must enter a class name", Toast.LENGTH_LONG).show();
                            createAlertClass();
                        }
                        createDeck();
                    }
                });
        //Create a cancel button so that the user doesnt have to create the new "class"
        builder.setView(mView)
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        HardGameActivity.super.finish();     //End the DeckCreator activity if the cancel button is pressed
                    }
                });
        builder.setCancelable(false);
        builder.show();
    }

    public void createDeck()
    {
        SaveLoad sv = new SaveLoad(deck.getClassName(), this);
        deck = sv.load(deck.getClassName());
        if(deck == null)
        {
            Toast.makeText(HardGameActivity.this, "That class does not exist", Toast.LENGTH_LONG).show();
            finish();
        }
        else
            showStudent();
    }

    //Shows a student that the user has yet to name properly
    public void showStudent()
    {
        student = deck.getRandomStudent();  //Gets a new student
        Bitmap bitmap = student.getFace();
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);
        gameView.setView(student);  //Adds this student to the gameView so the picture prints
    }

    //Ends the Hard Mode activity and returns to the main screen letting the user know how well they did
    public void finishGame()
    {
        Toast.makeText(HardGameActivity.this, "Congradulations, you completed the game with " + ((double)correct/ tries) * 100 + "% accuracy!", Toast.LENGTH_LONG).show();
        finish();
    }
}