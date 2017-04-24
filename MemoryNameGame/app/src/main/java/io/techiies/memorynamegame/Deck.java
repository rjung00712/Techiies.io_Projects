package io.techiies.memorynamegame;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by awing_000 on 4/23/2017.
 */

public class Deck
{
    String className;
    private ArrayList<Student> Students;
    private ArrayList<Student> Correct;

    public Deck()
    {
        Students = new ArrayList<>();
        Correct = new ArrayList<>();
//        /////////////////////////////////////////////////////////Used for testing/////////////////////////////////////
//        for(int i = 0; i < 3; i++)
//        {
//            Student student = new Student();
//            student.setName("Name");
//            Students.add(student);
//        }
    }

    public Student getRandomStudent()
    {
        Random rand = new Random();
        return Students.remove(rand.nextInt(Students.size()));
    }

    public void addStudent(Student student, boolean correct)
    {
        if(correct)
            Correct.add(student);
        else
            Students.add(student);
    }

    public void setClassName(String name) {className = name;}

    public String getClassName() {return className;}

    public int getStudentsLength() {return Students.size();}
}
