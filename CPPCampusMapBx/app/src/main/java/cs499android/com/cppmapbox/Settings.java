package cs499android.com.cppmapbox;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ((CheckBox)findViewById(R.id.speakDescription)).setChecked(StaticVariables.speakDescriptions);
        ((CheckBox)findViewById(R.id.speakDirection)).setChecked(StaticVariables.speakDirections);
    }

    public void apply(View view)
    {
        StaticVariables.speakDescriptions = ((CheckBox)findViewById(R.id.speakDescription)).isChecked();
        StaticVariables.speakDirections = ((CheckBox)findViewById(R.id.speakDirection)).isChecked();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putBoolean("speakDescriptions", StaticVariables.speakDescriptions).commit();
        sharedPreferences.edit().putBoolean("speakDirections", StaticVariables.speakDirections).commit();
        finish();
    }

    public void cancel(View view)
    {
        finish();
    }
}
