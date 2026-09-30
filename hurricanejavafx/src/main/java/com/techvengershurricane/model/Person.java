package com.techvengershurricane.model;

public class Person {
    private String firstName;
    private String lastName;
    private int age;
    private String specialNeeds;

    public Person() {
        /* JSON: required by Gson. */
    }

    public Person(String firstName, String lastName, int age, String specialNeeds) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.specialNeeds = specialNeeds;
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getSpecialNeeds() { return specialNeeds; }
    public void setSpecialNeeds(String specialNeeds) { this.specialNeeds = specialNeeds; }
}
