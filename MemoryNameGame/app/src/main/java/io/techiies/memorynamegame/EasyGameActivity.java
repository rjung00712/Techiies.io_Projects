package io.techiies.memorynamegame;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Random;

//Activity to play the game in easy mode
public class EasyGameActivity extends GameActivity
{
    private String[] names;         //Holds the three names that will be used for the buttons

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy_game);    //Sets the view to the easy game view
        ConstraintLayout container = (ConstraintLayout) findViewById(R.id.easy_game_container);

        names = new String[3];  //Initializes the names array
        gameView.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT));
        container.addView(gameView);    //Adds this GameView to the screen
        //Load a Deck from the saved games
        createAlertClass();
    }

    //Shows a student with the three names
    public void showStudent()
    {
        Random rand = new Random();
        student = deck.getRandomStudent();  //Gets a random student from the class
        names[0] = student.getName();       //Sets the name of this student to the first element of the names array
        //If there are at least 3 students in the class it makes sure that no duplicate names are selected
        if(deck.getNamesSize() >= 3)
        {
            names[1] = deck.getRandomName();    //Gets a random student's name from the class
            while (names[1] == names[0])        //Continues to get a name while it is the same as the first name
                names[1] = deck.getRandomName();
            names[2] = deck.getRandomName();    //Gets a random student's name from the class
            while (names[2] == names[0] || names[2] == names[1])  //Continues to get a name while it is the same as either of the first two names
                names[2] = deck.getRandomName();
        }
        //There are less than 3 students in the class
        else
        {
            names[1] = deck.getRandomName();    //Gets a random student's name from the class
            names[2] = deck.getRandomName();    //Gets a random student's name from the class
        }
        setButton(1);   //Sets the text for button 1
        setButton(2);   //Sets the text for button 2
        setButton(3);   //Sets the text for button 3
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
                    imageView.setImageBitmap(bitmap);   //Sets the picture to the student
                }
            }, 150);   //After 150 milliseconds do the run method above
        }
        imageView.setImageBitmap(bitmap);       //Sets the picture to the student
        gameView.invalidate();
    }

    //Sets the text of the buttons based off of the number passed to it
    public void setButton(int num)
    {
        Random rand = new Random();
        int x;
        Button btn = (Button) findViewById(R.id.option1);
        switch(num)
        {
            case 1:
                btn = (Button) findViewById(R.id.option1);  //Chooses button 1
                break;
            case 2:
                btn = (Button) findViewById(R.id.option2);  //Chooses button 2
                break;
            case 3:
                btn = (Button) findViewById(R.id.option3);  //Chooses button 3
                break;
        }
        //While there is no name in the current position of the names array, get a new position
        do
        {
            x = rand.nextInt(3);
        }while(names[x].equals(""));
        btn.setText(names[x]);  //Set the text of the button to the name at the given position
        btn.setBackgroundColor(Color.rgb(214, 215, 215));   //Set the button to the default color
        names[x] = "";  //Set the names array at the given position to nothing
    }

    //Called when the first option is selected
    public void selected1(View v)
    {
        check(1);
    }

    //Called when the second option is selected
    public void selected2(View v)
    {
        check(2);
    }

    //Called when the third option is selected
    public void selected3(View v)
    {
        check(3);
    }

    //Checks to see if the user guessed correctly based on the button they selected
    public void check(int num)
    {
        Button btn = (Button) findViewById(R.id.option1);
        switch(num)
        {
            case 1:
                btn = (Button) findViewById(R.id.option1);  //User chose option 1
                break;
            case 2:
                btn = (Button) findViewById(R.id.option2);  //User chose option 2
                break;
            case 3:
                btn = (Button) findViewById(R.id.option3);  //User chose option 3
                break;
        }
        String userGuess = btn.getText().toString();    //Gets the name the user selected

        //Checks if the user selected the name of the student shown
        if(userGuess.equals(student.getName()))
        {
            attempts++;    //Increases the number of tries by 1
            correct++;  //Increases the number of correct tries by 1
            deck.addStudent(student, true, false); //Adds the student back into the deck
            Toast.makeText(EasyGameActivity.this, "That is correct!", Toast.LENGTH_SHORT).show();
            btn.setBackgroundColor(Color.GREEN);    //Colors the button the user selected to green
            gameView.invalidate();  //Redraws the screen (for the buttons)
            android.os.Handler handler = new android.os.Handler();  //Used to pause the activity to give the user a chance to see they were correct
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Checks to see if there are still any students the user hasn't named correctly
                    if(deck.getStudentsLength() > 0)
                        showStudent();  //Chooses a new student if there are
                    else
                        finishGame();   //Finishes the game if there are no new students
                }
            }, 1500);   //After 1.5 seconds (1500 milliseconds) do the run method above
        }
        //The user guessed wrong
        else
        {
            attempts++;    //Increases the number of tries by 1
            deck.addStudent(student, false, false);    //Adds the student back into the class
            Toast.makeText(EasyGameActivity.this, "That is incorrect! The correct name is " + student.getName(), Toast.LENGTH_SHORT).show();
            btn.setBackgroundColor(Color.RED);  //Colors the button the user pressed red
            if(((Button) findViewById(R.id.option1)).getText().toString().equals(student.getName()))
                findViewById(R.id.option1).setBackgroundColor(Color.GREEN); //Colors button 1 green if it was correct
            if(((Button) findViewById(R.id.option2)).getText().toString().equals(student.getName()))
                findViewById(R.id.option2).setBackgroundColor(Color.GREEN); //Colors button 2 green if it was correct
            if(((Button) findViewById(R.id.option3)).getText().toString().equals(student.getName()))
                findViewById(R.id.option3).setBackgroundColor(Color.GREEN); //Colors button 3 green if it was correct
            gameView.invalidate();  //Redraws the buttons
            android.os.Handler handler = new android.os.Handler();  //Used to pause the activity to give the user a chance to see the correct name
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showStudent();
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
                            Toast.makeText(EasyGameActivity.this, "Must enter a class name", Toast.LENGTH_LONG).show();
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
                        EasyGameActivity.super.finish();     //End the DeckCreator activity if the cancel button is pressed
                    }
                });
        builder.setCancelable(false);
        builder.show();
    }

    //Used to load the deck that the user wants
    public void createDeck()
    {
        SaveLoad sv = new SaveLoad(deck.getClassName(), this);
        deck = sv.load(deck.getClassName());    //Loads the correct deck
        //Checks to see if the deck actually exists
        if(deck == null)
        {
            Toast.makeText(EasyGameActivity.this, "That class does not exist", Toast.LENGTH_LONG).show();
            finish();   //Ends the activity so the user must try again
        }
        else
            showStudent();
    }
}