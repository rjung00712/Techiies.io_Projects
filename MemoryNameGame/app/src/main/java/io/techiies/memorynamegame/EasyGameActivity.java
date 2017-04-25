package io.techiies.memorynamegame;

import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Random;

//Activity to play the game in easy mode
public class EasyGameActivity extends AppCompatActivity
{
    private GameView gameView;      //Used to print the picture of a student
    private Deck deck;              //Used to hold the students in the selected "Class"
    private Student student;        //Used to hold the current student the user should be guessing
    private int tries;              //Keeps track of how many times it takes the user to complete the "Class"
    private int correct;            //Keeps track of how many times the user gets a student's name correct
    private String[] names;         //Holds the three names that will be used for the buttons

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy_game);    //Sets the view to the easy game view
        ConstraintLayout container = (ConstraintLayout) findViewById(R.id.easy_game_container);

        names = new String[3];  //Initializes the names array
        tries = 0;              //Initializes the number of tries to 0
        correct = 0;            //Initializes the number of correct responses to 0
        gameView = new GameView(this);  //Creates a new Game View
        gameView.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT));
        container.addView(gameView);    //Adds this GameView to the screen
        ////////////////////////Load a Deck from the saved games////////////////////////////
        deck = new Deck();
        showStudent();      //Shows a student with the three names
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
        gameView.setView(student);  //Adds the student to the gameView so the picture can be drawn
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
            tries++;    //Increases the number of tries by 1
            correct++;  //Increases the number of correct tries by 1
            deck.addStudent(student, true); //Adds the student back into the deck
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
            }, 3000);   //After 3 seconds (3000 milliseconds) do the run method above
        }
        //The user guessed wrong
        else
        {
            tries++;    //Increases the number of tries by 1
            deck.addStudent(student, false);    //Adds the student back into the class
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
            }, 3000);   //After 3 seconds (3000 millisecconds) do the run method above
        }
    }

    //Ends the game and returns back to the main activity, letting the user know how well they did
    public void finishGame()
    {
        deck.resetClass();
        Toast.makeText(EasyGameActivity.this, "Congradulations, you completed the game with " + ((double)correct/ tries) * 100 + "% accuracy!", Toast.LENGTH_LONG).show();
        finish();
    }
}