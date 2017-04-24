package io.techiies.memorynamegame;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class HardGameActivity extends AppCompatActivity
{
    private HardGameView hardGameView;
    private Deck deck;
    private Student student;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hard_game);
        ConstraintLayout container = (ConstraintLayout) findViewById(R.id.hard_game_container);

        hardGameView = new HardGameView(this);
        hardGameView.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT));
        container.addView(hardGameView);
        //Load a Deck from the saved games
//        student = deck.getRandomStudent();
//        hardGameView.setView(student);
//        playGame();
    }

    public void check(View v)
    {

    }

    public void playGame()
    {

    }
}
