package io.techiies.memorynamegame;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class DeckCreator extends AppCompatActivity
{
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Student student;
    private Deck deck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createAlertClass();
        setContentView(R.layout.activity_deck_creator);
        student = new Student();
        deck = new Deck();
    }

    public void makeNewStudent(View v)
    {
        dispatchTakePictureIntent();
    }

    public void done(View v)
    {
        ///////////////////////Save the deck so that the game activities (hard and easy mode) can access it//////////////////////
        finish();
    }

    public void dispatchTakePictureIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();

            student.setFace((Bitmap)extras.get("data"));
            if(student.getFace() != null)
                createAlertStudent();
        }
    }

    // creates custom alert dialog box for student name input
    public void createAlertStudent() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        View mView = inflater.inflate(R.layout.student_name, null);
        final EditText editText = (EditText) mView.findViewById(R.id.name);

        // inflate and set the layout for the dialog
        // pass null as a parent view because its going in the dialog layout
        builder.setView(mView)
                .setPositiveButton("enter", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        student.setName(editText.getText().toString());

                        if(student.getName().equals("")) {
                            Toast.makeText(DeckCreator.this, "Must enter a student name", Toast.LENGTH_LONG).show();
                            createAlertStudent();
                        }
                        else
                        {
                            deck.addStudent(student, false);
                            Toast toast = Toast.makeText(DeckCreator.this, "Added new student. This class now has " + deck.getStudentsLength() + " students in it.", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                });
        builder.setView(mView)
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.setCancelable(false);
        builder.show();
    }

    // creates custom alert dialog box for class name input
    public void createAlertClass() {
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

                        if(deck.getClassName().equals("")) {
                            Toast.makeText(DeckCreator.this, "Must enter a class name", Toast.LENGTH_LONG).show();
                            createAlertClass();
                        }
                    }
                });
        builder.setView(mView)
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        DeckCreator.super.finish();
                    }
                });
        builder.setCancelable(false);
        builder.show();
    }
}
