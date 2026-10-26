package model;
import java.util.ArrayList;

/**
 * A family member linked to a user account, such as a child.
 * @author Team Tech-vengers
 */
public class Person {
    private String firstName;
    private String lastName;
    private int age;
    private ArrayList<String> specialNeeds;

    /**
     * Creates a family member.
     * 
     * @param firstName their first name
     * @param lastName their last name
     * @param age their age in years
     * @param specialNeeds needs such as medication, can be empty
     */
    public Person(String firstName, String lastName, int age, ArrayList<String> specialNeeds) {
        setFirstName(firstName);
        setLastName(lastName);
        setAge(age);
        setSpecialNeeds(specialNeeds);
    }

    /**
     * Returns the first name.
     * @return their first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Returns the last name.
     * @return their last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Returns the age.
     * @return their age in years
     */
    public int getAge() {
        return age;
    }

    /**
     * Returns the special needs.
     * @return their special needs
     */
    public ArrayList<String> getSpecialNeeds() {
        return specialNeeds;
    }

    /**
     * Sets the first name.
     * @param firstName their first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Sets the last name.
     * @param lastName their last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Sets the age.
     * @param age their age in years
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Sets the special needs.
     * @param specialNeeds their special needs
     */
    public void setSpecialNeeds(ArrayList<String> specialNeeds) {
        this.specialNeeds = specialNeeds;
    }
}
