package model;

/**
 * request status in the relief application.
 * @author Team Tech-vengers
 */
public enum RequestStatus {
    /**
     * submitted.
     */
    SUBMITTED,
    /**
     * in progress.
     */
    IN_PROGRESS,
    /**
     * fulfilled.
     */
    FULFILLED,
    /**
     * cancelled.
     */
    CANCELLED, 
    /**
     * assigned.
     */
    ASSIGNED,
    /**
     * Legacy saved value; normalized to FULFILLED when loaded.
     */
    @Deprecated
    COMPLETE
}
