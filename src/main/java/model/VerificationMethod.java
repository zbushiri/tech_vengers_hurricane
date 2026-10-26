package model;

/**
 * Ways a user's identity can be verified.
 * @author Team Tech-vengers
 */
public enum VerificationMethod {
    /**
     * A background check.
     */
    BACKGROUND_CHECK,
    /**
     * A one time code sent to the user.
     */
    ONE_TIME_PASSWORD,
    /**
     * An outside verification service.
     */
    THIRD_PARTY_VERIFICATION
}
