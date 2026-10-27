package model;

public enum RequestStatus {
    SUBMITTED,
    IN_PROGRESS,
    FULFILLED,
    CANCELLED, 
    ASSIGNED,
    /** Legacy saved value; normalized to FULFILLED when loaded. */
    @Deprecated
    COMPLETE
}
