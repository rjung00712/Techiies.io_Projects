package io.techiies.memorynamegame;

import android.graphics.Bitmap;

/**
 * Created by awing_000 on 4/23/2017.
 */

public class Student
{
    private Bitmap face;
    private String name;

    public Student()
    {
        face = null;
        name = "";
    }

    public void setFace(Bitmap face) {this.face = face;}

    public void  setName(String name) {this.name = name;}

    public Bitmap getFace() {return face;}

    public String getName() {return name;}
}