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

    public User(String username, String passwordHash) {
        this("", "", "", "", "", username, passwordHash, new ArrayList<>(), "");
    }

    public User(String firstName, String lastName, String email, String phoneNumber,
            String address, String username, String passwordHash,
            ArrayList<AccountRole> roles, String lastKnownLocation) {
        this(UUID.randomUUID(), firstName, lastName, email, phoneNumber, address,
                username, passwordHash, roles, new ArrayList<>(),
                VerificationStatus.UNVERIFIED, lastKnownLocation,
                SafetyStatusType.UNKNOWN, LocalDateTime.now());
    }

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

    public boolean login(String username, String passwordHash) {
        return this.username != null && this.username.equalsIgnoreCase(username)
                && Objects.equals(this.passwordHash, passwordHash);
    }

    public void logout() { System.out.println("Logged out: " + username); }
    public void updateProfile() { System.out.println("Updating profile for user: " + username); }
    public void submitReliefRequest(ReliefRequest request) { ReliefRequestCatalog.getInstance().submitRequest(request); }

    public boolean verifyIdentity(VerificationMethod method) {
        if (method == null) return false;
        verificationStatus = VerificationStatus.VERIFIED;
        return true;
    }

    public void markSafe() {
        safetyStatus = SafetyStatusType.SAFE;
        safetyStatusUpdatedAt = LocalDateTime.now();
    }

    public void updateLocation(String location) { lastKnownLocation = location; }
    public static void getLatestInfo(HurricaneEvent hurricane) { System.out.println(hurricane); }
    public static void searchGuidanceContent() { System.out.println("Searching guidance content..."); }

    public ArrayList<Shelter> findNearestShelters() {
        boolean hasTown = lastKnownLocation != null && lastKnownLocation.matches(".*[A-Za-z].*");
        return ShelterList.getInstance().searchByProximity(hasTown ? lastKnownLocation : address,
                ShelterList.DEFAULT_RADIUS_MILES);
    }

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
