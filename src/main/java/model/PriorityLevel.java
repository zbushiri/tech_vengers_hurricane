package model;

/**
 * How urgent a relief request is, from least to most urgent.
 * @author Team Tech-vengers
 */
public enum PriorityLevel {
    /**
     * Can wait.
     */
    LOW,
    /**
     * Should be handled soon.
     */
    MEDIUM,
    /**
     * Needs help quickly.
     */
    HIGH,
    /**
     * Life threatening, handle first.
     */
    CRITICAL
}
