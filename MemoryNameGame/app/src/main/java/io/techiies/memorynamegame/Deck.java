package io.techiies.memorynamegame;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by awing_000 on 4/23/2017.
 */

//Class to hold the students in a class
public class Deck
{
    String className;   //Holds the name of the class
    private ArrayList<Student> students;    //Holds the students for when the user is guessing their names
    private ArrayList<Student> correct;     //Holds the students that the user named correctly
    private ArrayList<String> names;        //Holds a list of the names of all of the students

    //Deck Constructor
    public Deck()
    {
        students = new ArrayList<>();   //Sets students to a new ArrayList
        correct = new ArrayList<>();    //Sets correct to a new ArrayList
        names = new ArrayList<>();      //Sets names to a new ArrayList
        /////////////////////////////////////////////////////////Used for testing/////////////////////////////////////
//        for(int i = 0; i < 3; i++)
//        {
//            Student student = new Student();
//            student.setName("Name" + i);
//            students.add(student);
//            names.add(student.getName());
//        }
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    }

    //Removes a student from the students ArrayList and returns it
    public Student getRandomStudent()
    {
        Random rand = new Random();
        return students.remove(rand.nextInt(students.size()));
    }

    //Returns a random name from the list of names
    public String getRandomName()
    {
        Random rand = new Random();
        return names.get(rand.nextInt(names.size()));
    }

    //Adds a student to the corresponding list based on if the user guessed correctly
    public void addStudent(Student student, boolean correct, boolean newStudent)
    {
        if(correct)
            this.correct.add(student);
        else
            students.add(student);
        if(newStudent)
            names.add(student.getName());
    }

    //Sets the "class" name to the name passed to it
    public void setClassName(String name) {className = name;}

    //Sets the list of students
    public void setStudents(ArrayList<Student> students) {this.students = students;}

    //Returns the list of students
    public ArrayList<Student> getStudents() {return students;}

    //Returns the name of the "class"
    public String getClassName() {return className;}

    //Gets the number of students in the students list
    public int getStudentsLength() {return students.size();}

    //Gets the number of names in the "class"
    public int getNamesSize() {return names.size();}
}