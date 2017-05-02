package io.techiies.memorynamegame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

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
        GridView gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(new GridViewAdapter(this, classList));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                if(mode.equals("Easy")) {
                    Intent easyModeIntent = new Intent(ClassSelector.this, EasyGameActivity.class);
                    easyModeIntent.putExtra("Class Name", classList[position]);
                    startActivity(easyModeIntent);
                }
                else
                {
                    Intent hardModeIntent = new Intent(ClassSelector.this, HardGameActivity.class);
                    hardModeIntent.putExtra("Class Name", classList[position]);
                    startActivity(hardModeIntent);
                }
            }
        });
    }
}
