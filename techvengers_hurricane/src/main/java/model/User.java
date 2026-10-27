package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/** Stores a user's profile and account information. */
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
     * @param passwordHash the hashed password for the new account
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
     * @param passwordHash the hashed password for the new account
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
     * Loading user account from persistent storage.
     * @param userId the unique identifier for the user
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param email the user's email address
     * @param phoneNumber the user's phone number
     * @param address the user's address
     * @param username the username for the account
     * @param passwordHash the hashed password for the account
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
     * Logs in the user by verifying the provided username and password hash.
     *
     * @param username the username to log in with
     * @param passwordHash the hashed password to verify
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
     * Verifies the user's identity using the specified method.
     *
     * @param method the verification method to use
     * @return true if the verification is successful, false otherwise
     */
    public boolean verifyIdentity(VerificationMethod method) {
        if (method == null) return false;
        verificationStatus = VerificationStatus.VERIFIED;
        return true;
    }


    public void markSafe() {
        safetyStatus = SafetyStatusType.SAFE;
        safetyStatusUpdatedAt = LocalDateTime.now();
    }

    public void logout() { System.out.println("Logged out: " + username); }
    public void updateLocation(String location) { lastKnownLocation = location; }
    public static void getLatestInfo(HurricaneEvent hurricane) { System.out.println(hurricane); }
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

    /** Ordinary accounts have no volunteer profile; volunteers override this. */
    public VolunteerProfile getVolunteerProfile() { return null; }

    public boolean hasRole(AccountRole role) { return roles.contains(role); }
    public UUID getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public ArrayList<AccountRole> getRoles() { return roles; }
    public ArrayList<Person> getAssociatedPersons() { return associatedPersons; }
    public VerificationStatus getVerificationStatus() { return verificationStatus; }
    public String getLastKnownLocation() { return lastKnownLocation; }
    public SafetyStatusType getSafetyStatus() { return safetyStatus; }
    public LocalDateTime getSafetyStatusUpdatedAt() { return safetyStatusUpdatedAt; }
    public void setUserId(UUID value) { userId = value; }
    public void setFirstName(String value) { firstName = value; }
    public void setLastName(String value) { lastName = value; }
    public void setPhoneNumber(String value) { phoneNumber = value; }
    public void setEmail(String value) { email = value; }
    public void setAddress(String value) { address = value; }
    public void setUsername(String value) { username = value; }
    public void setPasswordHash(String value) { passwordHash = value; }
    public void setRoles(ArrayList<AccountRole> value) { roles = value; }
    public void setAssociatedPersons(ArrayList<Person> value) { associatedPersons = value; }
    public void setVerificationStatus(VerificationStatus value) { verificationStatus = value; }
    public void setLastKnownLocation(String value) { lastKnownLocation = value; }
    public void setSafetyStatus(SafetyStatusType value) { safetyStatus = value; }
    public void setSafetyStatusUpdatedAt(LocalDateTime value) { safetyStatusUpdatedAt = value; }

    @Override
    public String toString() { return firstName + " " + lastName + " (" + username + ")"; }
}
