package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Stores a user's profile and account information.
 * @author Team Tech-vengers
 */
public class User {
    private UUID userId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String address;
    private String username;
    private String passwordHash;
    private ArrayList<AccountRole> roles;
    private ArrayList<Person> associatedPersons;
    private VerificationStatus verificationStatus;
    private String lastKnownLocation;
    private SafetyStatusType safetyStatus;
    private LocalDateTime safetyStatusUpdatedAt;

    /**
     * A new user account with just a username and password.
     * @param username the username for the new account
     * @param passwordHash password text stored directly; this class does not hash it
     */
    public User(String username, String passwordHash) {
        this("", "", "", "", "", username, passwordHash, new ArrayList<>(), "");
    }

    /**
     * A new user account with full profile details.
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param email the user's email address
     * @param phoneNumber the user's phone number
     * @param address the user's address
     * @param username the username for the new account
     * @param passwordHash password text stored directly; this class does not hash it
     * @param roles the roles assigned to the user
     * @param lastKnownLocation the user's last known location
     */
    public User(String firstName, String lastName, String email, String phoneNumber,
            String address, String username, String passwordHash,
            ArrayList<AccountRole> roles, String lastKnownLocation) {
        this(UUID.randomUUID(), firstName, lastName, email, phoneNumber, address,
                username, passwordHash, roles, new ArrayList<>(),
                VerificationStatus.UNVERIFIED, lastKnownLocation,
                SafetyStatusType.UNKNOWN, LocalDateTime.now());
    }

    /**
     * Restores an account from saved profile data.
     * @param userId the unique identifier for the user
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param email the user's email address
     * @param phoneNumber the user's phone number
     * @param address the user's address
     * @param username the username for the account
     * @param passwordHash stored password text used for direct comparison
     * @param roles the roles assigned to the user
     * @param associatedPersons the persons associated with the user
     * @param verificationStatus the verification status of the user
     * @param lastKnownLocation the user's last known location
     * @param safetyStatus the user's safety status
     * @param safetyStatusUpdatedAt the timestamp when the safety status was last updated
     */
    public User(UUID userId, String firstName, String lastName, String email,
            String phoneNumber, String address, String username, String passwordHash,
            List<AccountRole> roles, List<Person> associatedPersons,
            VerificationStatus verificationStatus, String lastKnownLocation,
            SafetyStatusType safetyStatus, LocalDateTime safetyStatusUpdatedAt) {
        this.userId = userId == null ? UUID.randomUUID() : userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.username = username;
        this.passwordHash = passwordHash;
        this.roles = roles == null ? new ArrayList<>() : new ArrayList<>(roles);
        this.associatedPersons = associatedPersons == null ? new ArrayList<>() : new ArrayList<>(associatedPersons);
        this.verificationStatus = verificationStatus == null ? VerificationStatus.UNVERIFIED : verificationStatus;
        this.lastKnownLocation = lastKnownLocation;
        this.safetyStatus = safetyStatus == null ? SafetyStatusType.UNKNOWN : safetyStatus;
        this.safetyStatusUpdatedAt = safetyStatusUpdatedAt == null ? LocalDateTime.now() : safetyStatusUpdatedAt;
    }

    /**
     * Logs in the user by comparing the provided username and stored password text.
     * 
     * @param username the username to log in with
     * @param passwordHash password text to compare directly with the stored value
     * @return true if the login is successful, false otherwise
     */
    public boolean login(String username, String passwordHash) {
        return this.username != null && this.username.equalsIgnoreCase(username)
                && Objects.equals(this.passwordHash, passwordHash);
    }


    /**
     * Submits a relief request
     * @param request the relief request to submit
     */
    public void submitReliefRequest(ReliefRequest request) {

        if (request == null) return;
        request.setSubmittedBy(this);
        ReliefRequestCatalog.getInstance().submitRequest(request);
    }

    /**
     * Marks the profile verified when a method is provided; no external verification is performed.
     * 
     * @param method the verification method to use
     * @return true if the verification is successful, false otherwise
     */
    public boolean verifyIdentity(VerificationMethod method) {
        if (method == null) return false;
        verificationStatus = VerificationStatus.VERIFIED;
        return true;
    }


    /**
     * Marks this user safe and records the current update time.
     */
    public void markSafe() {
        safetyStatus = SafetyStatusType.SAFE;
        safetyStatusUpdatedAt = LocalDateTime.now();
    }

    /**
     * Prints the logout message; the application facade clears the current session.
     */
    public void logout() { System.out.println("Logged out: " + username); }
    /**
     * Stores the user's latest reported location.
     * @param location user's latest reported location or address
     */
    public void updateLocation(String location) { lastKnownLocation = location; }
    /**
     * Prints the supplied hurricane event and its current information.
     * @param hurricane hurricane event to display
     */
    public static void getLatestInfo(HurricaneEvent hurricane) { System.out.println(hurricane); }
    /**
     * Prints the guidance-search placeholder message; no guidance search is performed.
     */
    public static void searchGuidanceContent() { System.out.println("Searching guidance content..."); }

    /**
     * Finds the nearest shelters based on the user's last known location.
     * 
     * @return a list of nearby shelters
     */
    public ArrayList<Shelter> findNearestShelters() {
        boolean hasTown = lastKnownLocation != null && lastKnownLocation.matches(".*[A-Za-z].*");
        return ShelterList.getInstance().searchByProximity(hasTown ? lastKnownLocation : address,
                ShelterList.DEFAULT_RADIUS_MILES);
    }

    /**
     * Ordinary accounts have no volunteer profile; volunteers override this.
     * @return null for an ordinary account; volunteers override this to return their profile
     */
    public VolunteerProfile getVolunteerProfile() { return null; }

    /**
     * Checks whether this account has the specified role.
     * @param role account role to create or check
     * @return true if the account's roles contain the requested role
     */
    public boolean hasRole(AccountRole role) { return roles.contains(role); }
    /**
     * Returns the user id.
     * @return the user id
     */
    public UUID getUserId() { return userId; }
    /**
     * Returns the first name.
     * @return the first name
     */
    public String getFirstName() { return firstName; }
    /**
     * Returns the last name.
     * @return the last name
     */
    public String getLastName() { return lastName; }
    /**
     * Returns the phone number.
     * @return the phone number
     */
    public String getPhoneNumber() { return phoneNumber; }
    /**
     * Returns the email.
     * @return the email
     */
    public String getEmail() { return email; }
    /**
     * Returns the address.
     * @return the address
     */
    public String getAddress() { return address; }
    /**
     * Returns the username.
     * @return the username
     */
    public String getUsername() { return username; }
    /**
     * Returns the stored password value; the legacy field name does not imply that hashing is performed.
     * @return stored password text used for direct credential comparison
     */
    public String getPasswordHash() { return passwordHash; }
    /**
     * Returns the roles.
     * @return the roles
     */
    public ArrayList<AccountRole> getRoles() { return roles; }
    /**
     * Returns the household members linked to this account.
     * @return the account's mutable list of associated people
     */
    public ArrayList<Person> getAssociatedPersons() { return associatedPersons; }
    /**
     * Returns the verification status.
     * @return the verification status
     */
    public VerificationStatus getVerificationStatus() { return verificationStatus; }
    /**
     * Returns the last known location.
     * @return the last known location
     */
    public String getLastKnownLocation() { return lastKnownLocation; }
    /**
     * Returns the safety status.
     * @return the safety status
     */
    public SafetyStatusType getSafetyStatus() { return safetyStatus; }
    /**
     * Returns the safety status updated at.
     * @return the safety status updated at
     */
    public LocalDateTime getSafetyStatusUpdatedAt() { return safetyStatusUpdatedAt; }
    /**
     * Sets the user id.
     * @param value replacement unique account identifier
     */
    public void setUserId(UUID value) { userId = value; }
    /**
     * Sets the first name.
     * @param value replacement account holder's first name
     */
    public void setFirstName(String value) { firstName = value; }
    /**
     * Sets the last name.
     * @param value replacement account holder's last name
     */
    public void setLastName(String value) { lastName = value; }
    /**
     * Sets the phone number.
     * @param value replacement contact telephone number
     */
    public void setPhoneNumber(String value) { phoneNumber = value; }
    /**
     * Sets the email.
     * @param value replacement contact email address
     */
    public void setEmail(String value) { email = value; }
    /**
     * Sets the address.
     * @param value replacement street address
     */
    public void setAddress(String value) { address = value; }
    /**
     * Sets the username.
     * @param value replacement account username
     */
    public void setUsername(String value) { username = value; }
    /**
     * Stores the password value without hashing it.
     * @param value replacement password text stored without hashing
     */
    public void setPasswordHash(String value) { passwordHash = value; }
    /**
     * Sets the roles.
     * @param value replacement roles assigned to the account
     */
    public void setRoles(ArrayList<AccountRole> value) { roles = value; }
    /**
     * Sets the associated persons.
     * @param value replacement household members linked to the account
     */
    public void setAssociatedPersons(ArrayList<Person> value) { associatedPersons = value; }
    /**
     * Sets the verification status.
     * @param value replacement identity-verification status
     */
    public void setVerificationStatus(VerificationStatus value) { verificationStatus = value; }
    /**
     * Sets the last known location.
     * @param value replacement user's latest reported location
     */
    public void setLastKnownLocation(String value) { lastKnownLocation = value; }
    /**
     * Sets the safety status.
     * @param value replacement user's reported safety status
     */
    public void setSafetyStatus(SafetyStatusType value) { safetyStatus = value; }
    /**
     * Sets the safety status updated at.
     * @param value replacement time of the most recent safety-status update
     */
    public void setSafetyStatusUpdatedAt(LocalDateTime value) { safetyStatusUpdatedAt = value; }

    /**
     * Formats this object for display.
     * @return user's full name followed by the username
     */
    @Override
    public String toString() { return firstName + " " + lastName + " (" + username + ")"; }
}
