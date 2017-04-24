package io.techiies.memorynamegame;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class HardGameActivity extends AppCompatActivity
{
    private HardGameView hardGameView;
    private Deck deck;
    private Student student;
    private int tries;
    private int correct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hard_game);
        ConstraintLayout container = (ConstraintLayout) findViewById(R.id.hard_game_container);

        tries = 0;
        correct = 0;
        hardGameView = new HardGameView(this);
        hardGameView.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT));
        container.addView(hardGameView);
        ////////////////////////Load a Deck from the saved games////////////////////////////
//        deck = new Deck();
        showStudent();
    }

    public void check(View v)
    {
        final EditText editText = (EditText) findViewById(R.id.name_guess);
        String userGuess = editText.getText().toString();

        if(userGuess.equals(student.getName()))
        {
            tries++;
            correct++;
            deck.addStudent(student, true);
            Toast.makeText(HardGameActivity.this, "That is correct!", Toast.LENGTH_SHORT).show();
            editText.setText("");
            if(deck.getStudentsLength() > 0)
                showStudent();
            else
                finishGame();
        }
        else
        {
            tries++;
            deck.addStudent(student, false);
            Toast.makeText(HardGameActivity.this, "That is incorrect!", Toast.LENGTH_SHORT).show();
            showStudent();
        }
    }

    public void showStudent()
    {
        student = deck.getRandomStudent();
        hardGameView.setView(student);
    }

    public void finishGame()
    {
        Toast.makeText(HardGameActivity.this, "Congradulations, you completed the game with " + ((double)correct/ tries) * 100 + "% accuracy!", Toast.LENGTH_LONG).show();
        finish();
    }
}
