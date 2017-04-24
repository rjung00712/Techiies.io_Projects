package io.techiies.memorynamegame;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by awing_000 on 4/23/2017.
 */

public class Deck
{
    String className;
    private ArrayList<Student> students;
    private ArrayList<Student> correct;
    private ArrayList<String> names;

    public Deck()
    {
        students = new ArrayList<>();
        correct = new ArrayList<>();
        names = new ArrayList<>();
        /////////////////////////////////////////////////////////Used for testing/////////////////////////////////////
//        for(int i = 0; i < 3; i++)
//        {
//            Student student = new Student();
//            student.setName("Name" + i);
//            students.add(student);
//            names.add(student.getName());
//        }
    }

    public Student getRandomStudent()
    {
        Random rand = new Random();
        return students.remove(rand.nextInt(students.size()));
    }

    public String getRandomName()
    {
        Random rand = new Random();
        return names.remove(rand.nextInt(names.size()));
    }

    public void addStudent(Student student, boolean correct)
    {
        if(correct)
            this.correct.add(student);
        else
            students.add(student);
    }

    public void addName(String name)
    {
        names.add(name);
    }

    public void setClassName(String name) {className = name;}

    public String getClassName() {return className;}

    public int getStudentsLength() {return students.size();}

    public int getNamesSize() {return names.size();}
}
