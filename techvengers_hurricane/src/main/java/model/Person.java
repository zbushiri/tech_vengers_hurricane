package model;
import java.util.ArrayList;

public class Person {
    private String firstName;
    private String lastName;
    private int age;
    private ArrayList<String> specialNeeds;

    public Person(String firstName, String lastName, int age, ArrayList<String> specialNeeds) {
        setFirstName(firstName);
        setLastName(lastName);
        setAge(age);
        setSpecialNeeds(specialNeeds);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getAge() {
        return age;
    }

    public ArrayList<String> getSpecialNeeds() {
        return specialNeeds;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setSpecialNeeds(ArrayList<String> specialNeeds) {
        this.specialNeeds = specialNeeds;
    }
}
