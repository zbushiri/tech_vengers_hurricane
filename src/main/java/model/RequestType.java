package model;

/**
 * The kind of help a relief request asks for.
 * @author Team Tech-vengers
 */
public enum RequestType {
    /**
     * Food or drinking water.
     */
    FOOD,
    /**
     * Medical help.
     */
    MEDICAL,
    /**
     * A place to stay.
     */
    SHELTER,
    /**
     * A ride, for example to a shelter.
     */
    TRANSPORTATION,
    /**
     * Supplies such as tarps or generators.
     */
    SUPPLIES,
    /**
     * A danger such as a downed power line.
     */
    HAZARD,
    /**
     * Anything else.
     */
    OTHER
}
