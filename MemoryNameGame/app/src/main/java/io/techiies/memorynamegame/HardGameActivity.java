package io.techiies.memorynamegame;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

//Activity to play the game in hard mode
public class HardGameActivity extends GameActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hard_game);    //Sets the view to the hard game view
        ConstraintLayout container = (ConstraintLayout) findViewById(R.id.hard_game_container);

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
            attempts++;    //Increases the number of tries by 1
            correct++;  //Increases the number of correct tries by 1
            deck.addStudent(student, true, false); //Adds the student back to the deck
            Toast.makeText(HardGameActivity.this, "That is correct!", Toast.LENGTH_SHORT).show();
            //Checks to see if there are still students the user hasn't named

            android.os.Handler handler = new android.os.Handler();  //Used to pause the activity to give the user a chance to see the correct name
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    editText.setText("");   //Resets the text box where the user enters names
                    if(deck.getStudentsLength() > 0)
                        showStudent();  //Shows a new student the user has yet to name
                    else
                        finishGame();   //Ends the Hard Mode activity
                }
            }, 1500);   //After 1.5 seconds (1500 milliseconds) do the run method above

        }
        //The user entered the wrong name
        else
        {
            attempts++;    //Increases the number of tries by 1
            deck.addStudent(student, false, false);    //Adds the student back to the deck
            Toast.makeText(HardGameActivity.this, "That is incorrect! The correct name is " + student.getName(), Toast.LENGTH_SHORT).show();    //Tells the user the student's name

            android.os.Handler handler = new android.os.Handler();  //Used to pause the activity to give the user a chance to see the correct name
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    editText.setText("");   //Resets the text box where the user enters names
                    showStudent();  //Shows a new student the user has yet to name
                }
            }, 1500);   //After 1.5 seconds (1500 milliseconds) do the run method above
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
        //Create a cancel button so that the user doesn't have to create the new "class"
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

    //Loads a deck specified by the user
    public void createDeck()
    {
        SaveLoad sv = new SaveLoad(deck.getClassName(), this);
        deck = sv.load(deck.getClassName());    //Gets the deck the user wants
        //If the deck doesn't exist then it lets the user know
        if(deck == null)
        {
            Toast.makeText(HardGameActivity.this, "That class does not exist", Toast.LENGTH_LONG).show();
            finish();   //Ends the activity so the user has to try again
        }
        else
            showStudent();
    }

    //Shows a student that the user has yet to name properly
    public void showStudent()
    {
        student = deck.getRandomStudent();  //Gets a new student
        final Bitmap bitmap = student.getFace();
        final ImageView imageView = (ImageView)findViewById(R.id.imageView);
        imageView.setScaleX(.01f);
        imageView.setScaleY(.01f);
        for(int i = 0; i < 1100; i++)   //Used to make the image look as if it is growing (make it "animated")
        {
            android.os.Handler handler = new android.os.Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    imageView.setScaleX(imageView.getScaleX() + 0.001f);
                    imageView.setScaleY(imageView.getScaleY() + 0.001f);
                    imageView.setImageBitmap(bitmap);
                }
            }, 150);   //After 150 milliseconds do the run method above
        }
        imageView.setImageBitmap(bitmap);   //Used to print the image to the screen
        gameView.invalidate();
    }
}