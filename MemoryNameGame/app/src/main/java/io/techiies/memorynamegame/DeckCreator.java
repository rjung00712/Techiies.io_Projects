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

//Activity to create a "Class" or "Deck" of students
public class DeckCreator extends AppCompatActivity
{
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Student student;    //Used to hold an instance of a new student
    private Deck deck;          //Used to hold all of the students in a class
    private MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_creator);     //Sets the view so there are new buttons
        student = new Student();    //Creates a new student (No picture or name yet)
        deck = new Deck();      //Creates an empty class
        createAlertClass();     //This alert will get the name of the class
    }

    //Method to get the picture of a new student. Executed when the "Add student" button is pressed
    public void makeNewStudent(View v)
    {
        dispatchTakePictureIntent();
    }

    //Save the "Class" and finish the DeckCreator activity to open up the main activity
    public void done(View v)
    {
        ///////////////////////Save the deck so that the game activities (hard and easy mode) can access it//////////////////////
        SaveLoad sv = new SaveLoad(deck.getClassName(), this);
        sv.save(deck);
        finish();
    }

    //Method to take a picture
    public void dispatchTakePictureIntent()
    {
        student = new Student();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    //Method to handle what happens when a picture is taken
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();

            student.setFace((Bitmap)extras.get("data"));    //Sets the students picture to the picture that was just taken
            //If the picture was taken and not cancelled, create an alert to get the name of the student
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

                        //Make sure that a name was entered
                        if(student.getName().equals("")) {
                            Toast.makeText(DeckCreator.this, "Must enter a student name", Toast.LENGTH_LONG).show();
                            createAlertStudent();
                        }
                        else
                        {
                            //Add the student to the "Class"
                            deck.addStudent(student, false, true);
                            Toast toast = Toast.makeText(DeckCreator.this, "Added new student. This class now has " + deck.getStudentsLength() + " students in it.", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                });
        //Create a cancel button so that the student is not added
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

                        //Make sure that a class name is entered
                        if(deck.getClassName().equals("")) {
                            Toast.makeText(DeckCreator.this, "Must enter a class name", Toast.LENGTH_LONG).show();
                            createAlertClass();
                        }
                    }
                });
        //Create a cancel button so that the user doesnt have to create the new "class"
        builder.setView(mView)
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        DeckCreator.super.finish();     //End the DeckCreator activity if the cancel button is pressed
                    }
                });
        builder.setCancelable(false);
        builder.show();
    }
}