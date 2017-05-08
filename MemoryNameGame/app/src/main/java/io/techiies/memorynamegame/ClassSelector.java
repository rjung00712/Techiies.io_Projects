package io.techiies.memorynamegame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class ClassSelector extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_selector);
        String mode = getIntent().getExtras().getString("Mode");
        showGridView(mode);
    }

    public void showGridView(final String mode)
    {
        SaveLoad sv = new SaveLoad(null, this);
        final String[] classList = sv.loadClassesList();
        if(classList == null)
        {
            Toast.makeText(this, "There are no Classes to play the game!", Toast.LENGTH_SHORT).show();
            finish();
        }
        GridView gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(new GridViewAdapter(this, classList, mode));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {

            }
        });
    }
}
