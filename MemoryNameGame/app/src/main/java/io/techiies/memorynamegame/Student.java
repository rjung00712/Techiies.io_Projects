package io.techiies.memorynamegame;

import android.graphics.Bitmap;

/**
 * Created by awing_000 on 4/23/2017.
 */

//Class to hold an image and a name of a student
public class Student
{
    private Bitmap face;    //Image of the student
    private String name;    //Name of the student

    //Constructor to create a blank new student
    public Student()
    {
        face = null;
        name = "";
    }

    //Sets the image of the student to the image passed in
    public void setFace(Bitmap face) {this.face = face;}

    //Sets the name of the student to the name passed in
    public void  setName(String name) {this.name = name;}

    //Returns the image of the student
    public Bitmap getFace() {return face;}

    //Returns the name of the student
    public String getName() {return name;}
}