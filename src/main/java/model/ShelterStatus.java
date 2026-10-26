package model;

/**
 * Whether a shelter can take people right now.
 * @author Team Tech-vengers
 */
public enum ShelterStatus {
    /**
     * Taking people.
     */
    OPEN,
    /**
     * Open but out of beds.
     */
    FULL,
    /**
     * Not operating.
     */
    CLOSED
}
