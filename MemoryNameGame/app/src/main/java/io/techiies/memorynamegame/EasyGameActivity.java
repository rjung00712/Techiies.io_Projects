package io.techiies.memorynamegame;

import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Random;
import java.util.logging.Handler;

public class EasyGameActivity extends AppCompatActivity
{
    private GameView gameView;
    private Deck deck;
    private Student student;
    private int tries;
    private int correct;
    private String[] names;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy_game);
        ConstraintLayout container = (ConstraintLayout) findViewById(R.id.easy_game_container);

        names = new String[3];
        tries = 0;
        correct = 0;
        gameView = new GameView(this);
        gameView.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT));
        container.addView(gameView);
        ////////////////////////Load a Deck from the saved games////////////////////////////
        //deck = new Deck();
        showStudent();
    }

    public void showStudent()
    {
        Random rand = new Random();
        student = deck.getRandomStudent();
        names[0] = student.getName();
        if(deck.getNamesSize() >= 3)
        {
            names[1] = deck.getRandomName();
            while (names[1] == names[0]) {
                deck.addName(names[1]);
                names[1] = deck.getRandomName();
            }
            names[2] = deck.getRandomName();
            while (names[2] == names[0] || names[2] == names[1]) {
                deck.addName(names[2]);
                names[2] = deck.getRandomName();
            }
        }
        else
        {
            names[1] = deck.getRandomName();
            names[2] = deck.getRandomName();
        }
        deck.addName(names[1]);
        deck.addName(names[2]);
        setButton(1);
        setButton(2);
        setButton(3);
        gameView.setView(student);
    }

    public void setButton(int num)
    {
        Random rand = new Random();
        int x;
        Button btn = (Button) findViewById(R.id.option1);
        switch(num)
        {
            case 1:
                btn = (Button) findViewById(R.id.option1);
                break;
            case 2:
                btn = (Button) findViewById(R.id.option2);
                break;
            case 3:
                btn = (Button) findViewById(R.id.option3);
                break;
        }
        do
        {
            x = rand.nextInt(3);
        }while(names[x].equals(""));
        btn.setText(names[x]);
        btn.setBackgroundColor(Color.rgb(214, 215, 215));
        names[x] = "";
    }

    public void selected1(View v)
    {
        check(1);
    }

    public void selected2(View v)
    {
        check(2);
    }

    public void selected3(View v)
    {
        check(3);
    }

    public void check(int num)
    {
        Button btn = (Button) findViewById(R.id.option1);
        switch(num)
        {
            case 1:
                btn = (Button) findViewById(R.id.option1);
                break;
            case 2:
                btn = (Button) findViewById(R.id.option2);
                break;
            case 3:
                btn = (Button) findViewById(R.id.option3);
                break;
        }
        String userGuess = btn.getText().toString();

        if(userGuess.equals(student.getName()))
        {
            tries++;
            correct++;
            deck.addStudent(student, true);
            Toast.makeText(EasyGameActivity.this, "That is correct!", Toast.LENGTH_SHORT).show();
            btn.setBackgroundColor(Color.GREEN);
            gameView.invalidate();
            android.os.Handler handler = new android.os.Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(deck.getStudentsLength() > 0)
                        showStudent();
                    else
                        finishGame();
                }
            }, 3000);
        }
        else
        {
            tries++;
            deck.addStudent(student, false);
            Toast.makeText(EasyGameActivity.this, "That is incorrect! The correct name is " + student.getName(), Toast.LENGTH_SHORT).show();
            btn.setBackgroundColor(Color.RED);
            if(((Button) findViewById(R.id.option1)).getText().toString().equals(student.getName()))
                findViewById(R.id.option1).setBackgroundColor(Color.GREEN);
            if(((Button) findViewById(R.id.option2)).getText().toString().equals(student.getName()))
                findViewById(R.id.option2).setBackgroundColor(Color.GREEN);
            if(((Button) findViewById(R.id.option3)).getText().toString().equals(student.getName()))
                findViewById(R.id.option3).setBackgroundColor(Color.GREEN);
            gameView.invalidate();
            android.os.Handler handler = new android.os.Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showStudent();
                }
            }, 3000);
        }
    }

    public void finishGame()
    {
        Toast.makeText(EasyGameActivity.this, "Congradulations, you completed the game with " + ((double)correct/ tries) * 100 + "% accuracy!", Toast.LENGTH_LONG).show();
        finish();
    }
}
