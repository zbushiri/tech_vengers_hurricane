package model;

/**
 * Workflow states for submitted, assigned, active, completed and cancelled relief requests.
 * @author Team Tech-vengers
 */
public enum RequestStatus {
    /**
     * Submitted and not yet assigned to a volunteer.
     */
    SUBMITTED,
    /**
     * Assigned volunteer has started work.
     */
    IN_PROGRESS,
    /**
     * Completed by an authorized manager or coordinator.
     */
    FULFILLED,
    /**
     * Cancelled and no longer available for assignment.
     */
    CANCELLED, 
    /**
     * Claimed by a volunteer who is responsible for the request.
     */
    ASSIGNED,
    /**
     * Legacy saved value; normalized to FULFILLED when loaded.
     */
    @Deprecated
    COMPLETE
}
