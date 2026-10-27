package model;

/**
 *  An emergency contact for a person.
 */

public class EmergencyContact {
    private String name;
    private String phoneNumber;
    private String relationship;

    /**
     * The person's emergency contact information.
     * @param name The name of the emergency contact.
     * @param phoneNumber The phone number of the emergency contact.
     * @param relationship The relationship of the emergency contact to the person.
     */
    public EmergencyContact(String name, String phoneNumber, String relationship) {
        setName(name);
        setPhoneNumber(phoneNumber);
        setRelationship(relationship);
    }

    /**
     * Gets the name of the emergency contact.
     * @return The name of the emergency contact.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the emergency contact.
     * @param name The name of the emergency contact.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the phone number of the emergency contact.
     * @return The phone number of the emergency contact.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number of the emergency contact.
     * @param phoneNumber The phone number of the emergency contact.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the relationship of the emergency contact to the person.
     * @return The relationship of the emergency contact to the person.
     */
    public String getRelationship() {
        return relationship;
    }

    /**
     * Sets the relationship of the emergency contact to the person.
     * @param relationship The relationship of the emergency contact to the person.
     */
    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }
}
