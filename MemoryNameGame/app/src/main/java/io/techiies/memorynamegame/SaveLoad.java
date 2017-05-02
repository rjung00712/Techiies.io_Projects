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
    private String imageName;   //Used to name the file for the images
    private String studentName;     //Used to name the file for the names
    private ArrayList<byte[]> images;   //Holds the images as byte[] in an ArrayList
    private ArrayList<String> names;    //Holds the names in an ArrayList
    private ArrayList<Student> students;    //Holds the students of the deck in an ArrayList
    private ArrayList<String> classList;

    //Constructor to make a SaveLoad object
    public SaveLoad(String name, Activity activity)
    {
        this.activity = activity;
        if(name != null) {
            this.imageName = name + " in";  //makes the file name for the images with the name of the class
            this.studentName = name + " sn";    //makes the file name for the names with the name of the class
        }
    }

    //Saves the deck
    public void save(Deck deck)
    {
        images = new ArrayList<>(); //Initializes the images
        names = new ArrayList<>();  //Initializes the names
        classList = new ArrayList<>();  //Initializes the list of classes
        students = deck.getStudents();  //Gets the students of the deck

        //Saves all of teh students in the appropriate ArrayLists
        for(int i = 0; i < students.size(); i++)
        {
            Student student = students.get(i);
            byte[] face = BitmapUtility.getBytes(student.getFace());
            images.add(face);
            names.add(student.getName());
        }

        //Used to save
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = sharedPreferences.getString("classesList", "");
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        if (json != null)     //Checks to see if it exists
            classList = gson.fromJson(json, type);
//        } else {
//            classList.add(deck.getClassName());
//        }
        if(classList == null)
            classList = new ArrayList<>();
        if(classList != null && !classList.contains(deck.getClassName()))
            classList.add(deck.getClassName());

        json = gson.toJson(classList);
        editor.putString("classesList", json);
        //Saves the images
        json = gson.toJson(images);
        editor.putString(imageName, json);
        //Saves the names
        json = gson.toJson(names);
        editor.putString(studentName, json);
        editor.commit();
    }

    //Used to load a class
    public Deck load (String name)
    {
        Deck deck = new Deck(); //Creates a new class
        deck.setClassName(name);    //Sets the name of the class

        //Used to load
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        Gson gson = new Gson();
        //Loads the images
        String json = sharedPreferences.getString(imageName, "");
        Type type = new TypeToken<ArrayList<byte[]>>() {}.getType();
        if(json != null)    //Checks to see if it exists
            images = gson.fromJson(json, type);
        if(images != null) {
            //Loads the names
            json = sharedPreferences.getString(studentName, "");
            type = new TypeToken<ArrayList<String>>() {}.getType();
            if (json != null)   //Checks to see if it exists
                names = gson.fromJson(json, type);
            if (names != null) {
                //Creates students based off the images and names and adds them to the deck
                for (int i = 0; i < images.size(); i++) {
                    Student student = new Student();
                    Bitmap face = BitmapUtility.getImage(images.get(i));
                    student.setFace(face);
                    student.setName(names.get(i));
                    deck.addStudent(student, false, true);
                }
            }
            else
                deck = null;    //The deck doesn't exist
        }
        else
            deck = null;    //The deck doesn't exist
        return deck;
    }

    public String[] loadClassesList()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("classesList", "");
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        if(json != null)    //Checks to see if it exists
            classList = gson.fromJson(json, type);

        if(classList != null) {
            String[] list = classList.toArray(new String[classList.size()]);
            return list;
        }
        return null;
    }
}
