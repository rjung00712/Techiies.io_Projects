package io.techiies.memorynamegame;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by awing_000 on 4/25/2017.
 */

public class SaveLoad
{
    Activity activity;
    private String imageName;
    private String studentName;
    private ArrayList<byte[]> images;
    private ArrayList<String> names;
    private ArrayList<Student> students;

    public SaveLoad(String name, Activity activity)
    {
        this.activity = activity;
        this.imageName = name + " in";
        this.studentName = name + " sn";
    }

    public void save(Deck deck)
    {
        images = new ArrayList<>();
        names = new ArrayList<>();
        students = deck.getStudents();

        for(int i = 0; i < students.size(); i++)
        {
            Student student = students.get(i);
            byte[] face = BitmapUtility.getBytes(student.getFace());
            images.add(face);
            names.add(student.getName());
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        String json = gson.toJson(images);
        editor.putString(imageName, json);
        json = gson.toJson(names);
        editor.putString(studentName, json);
        editor.commit();
    }

    public Deck load (String name)
    {
        Deck deck = new Deck();
        deck.setClassName(name);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(imageName, "");
        Type type = new TypeToken<ArrayList<byte[]>>() {}.getType();
        if(json != null)
            images = gson.fromJson(json, type);
        if(images != null) {
            json = sharedPreferences.getString(studentName, "");
            type = new TypeToken<ArrayList<String>>() {}.getType();
            if (json != null)
                names = gson.fromJson(json, type);
            if (names != null) {
                for (int i = 0; i < images.size(); i++) {
                    Student student = new Student();
                    Bitmap face = BitmapUtility.getImage(images.get(i));
                    student.setFace(face);
                    student.setName(names.get(i));
                    deck.addStudent(student, false, true);
                }
            }
            else
                deck = null;
        }
        else
            deck = null;
        return deck;
    }
}
