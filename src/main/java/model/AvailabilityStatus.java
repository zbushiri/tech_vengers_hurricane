package model;

/**
 * The different availability statuses for a volunteer.
 * @author Team Tech-vengers
 */
public enum AvailabilityStatus {
    /**
     * Available to claim a new request.
     */
    AVAILABLE,
    /**
     * Not currently available for assignments.
     */
    UNAVAILABLE,
    /**
     * Currently handling an assigned request.
     */
    ON_ASSIGNMENT
}
