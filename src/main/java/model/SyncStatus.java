package model;

/**
 * Whether a message has reached the server.
 * @author Team Tech-vengers
 */
public enum SyncStatus {
    /**
     * Waiting to be sent.
     */
    PENDING,
    /**
     * Sent and saved.
     */
    SYNCED
}
