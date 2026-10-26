package model;

/**
 * The different statuses for a background check.
 * @author Team Tech-vengers
 */
public enum BackgroundCheckStatus {
    /**
     * Background check has not been verified.
     */
    PENDING,
    /**
     * Background check has been approved.
     */
    VERIFIED,
    /**
     * Background check was rejected.
     */
    REJECTED
}
