package model;
import java.util.UUID;
import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

/**
 * User's profile and account information.
 * @author CharlesNewhouse
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
    private List <AccountRole> roles;
    private List <Person> associatedPersons;
    private VerificationStatus verificationStatus;
    private String lastKnownLocation;
    private SafetyStatusType safetyStatus;
    private LocalDateTime safetyStatusUpdatedAt;

    public User(String username, String passwordHash) {
        setUserId(UUID.randomUUID());
        setFirstName("");
        setLastName("");
        setPhoneNumber("");
        setEmail("");
        setAddress("");
        setUsername(username);
        setPasswordHash(passwordHash);
        setRoles(new ArrayList<>());
        setAssociatedPersons(new ArrayList<>());
        setVerificationStatus(VerificationStatus.UNVERIFIED);
        setLastKnownLocation("");
        setSafetyStatus(SafetyStatusType.UNKNOWN);
        setSafetyStatusUpdatedAt(LocalDateTime.now());
    }

    public User(UUID userId, String firstName, String lastName, String phoneNumber, String email, 
        String address, String username, String passwordHash, List<AccountRole> roles, 
        List<Person> associatedPersons, VerificationStatus verificationStatus, String lastKnownLocation, 
        SafetyStatusType safetyStatus, LocalDateTime safetyStatusUpdatedAt) {
        setUserId(userId);
        setFirstName(firstName);
        setLastName(lastName);
        setPhoneNumber(phoneNumber);
        setEmail(email);
        setAddress(address);
        setUsername(username);
        setPasswordHash(passwordHash);
        setRoles(roles);
        setAssociatedPersons(associatedPersons);
        setVerificationStatus(verificationStatus);
        setLastKnownLocation(lastKnownLocation);
        setSafetyStatus(safetyStatus);
        setSafetyStatusUpdatedAt(safetyStatusUpdatedAt);
    }

    public boolean login(String username, String passwordHash) {
        return this.username != null && this.username.equalsIgnoreCase(username)
                && Objects.equals(this.passwordHash, passwordHash);
    }

    /**
     * Ends this user's session. The facade tracks who is logged in, so the
     * account's username and password must stay as they are.
     */
    public void logout() {
        System.out.println("Logged out: " + this.username);
    }


    // TODO: Clarify the purpose of this method and implement it accordingly
    public void updateProfile() {
        System.out.println("Updating profile for user: " + this.username);
    }

    public void submitReliefRequest(ReliefRequest request) {
        ReliefRequestCatalog.getInstance().submitRequest(request);
    }

    public boolean verifyIdentity(VerificationMethod verificationMethod) {
        setVerificationStatus(VerificationStatus.VERIFIED);
        return true;
    }

    public void markSafe() {
        this.safetyStatus = SafetyStatusType.SAFE;
        this.safetyStatusUpdatedAt = LocalDateTime.now();
    }

    public void updateLocation(String location) {
        this.lastKnownLocation = location;
    }

    
    public static void getLatestInfo(HurricaneEvent hurricane) {
        System.out.println(hurricane.toString());
    }

   public static void searchGuidanceContent() {
        // TODO: Implementation for searching guidance content goes here
        System.out.println("Searching guidance content...");

   }

   public ArrayList<Shelter> findNearestShelters() {
       // A location saved as coordinates has no town to match, so use the address.
       boolean hasTown = lastKnownLocation != null && lastKnownLocation.matches(".*[A-Za-z].*");
       String location = hasTown ? lastKnownLocation : address;
       return ShelterList.getInstance().searchByProximity(location, ShelterList.DEFAULT_RADIUS_MILES);
   }

   public boolean hasRole(AccountRole role) {
       return roles != null && roles.contains(role);
   }

    public UUID getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public List<AccountRole> getRoles() {
        return roles;
    }

    public List<Person> getAssociatedPersons() {
        return associatedPersons;
    }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public String getLastKnownLocation() {
        return lastKnownLocation;
    }

    public SafetyStatusType getSafetyStatus() {
        return safetyStatus;
    }

    public LocalDateTime getSafetyStatusUpdatedAt() {
        return safetyStatusUpdatedAt;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setRoles(List<AccountRole> roles) {
        this.roles = roles;
    }

    public void setAssociatedPersons(List<Person> associatedPersons) {
        this.associatedPersons = associatedPersons;
    }

    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public void setLastKnownLocation(String lastKnownLocation) {
        this.lastKnownLocation = lastKnownLocation;
    }

    public void setSafetyStatus(SafetyStatusType safetyStatus) {
        this.safetyStatus = safetyStatus;
    }

    public void setSafetyStatusUpdatedAt(LocalDateTime safetyStatusUpdatedAt) {
        this.safetyStatusUpdatedAt = safetyStatusUpdatedAt;
    }

    @Override 
    public String toString() {
        return "User{" + "\n" +
                "userId=" + userId + "\n" +
                "firstName='" + firstName + "\n" +
                "lastName='" + lastName + "\n" +
                "phoneNumber='" + phoneNumber + "\n" +
                "email='" + email + "\n" +
                "address='" + address + "\n" +
                "username='" + username + "\n" +
                "roles=" + roles + "\n" +
                "associatedPersons=" + associatedPersons + "\n" +
                "verificationStatus=" + verificationStatus + "\n" +
                "lastKnownLocation='" + lastKnownLocation + "\n" +
                "safetyStatus=" + safetyStatus + "\n" +
                "safetyStatusUpdatedAt=" + safetyStatusUpdatedAt + "\n" +
                '}';
    }
}
