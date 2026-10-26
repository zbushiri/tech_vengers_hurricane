package model;

import java.util.ArrayList;
import java.util.List;

import location.Address;
import location.MapTools;

/**
 * Runs the Hurricane Scarlett scenario as a hard-coded walkthrough
 * the same way the teacher's LibraryUI runs scenario1().
 * 
 * @author Tavien Smith
 */

public class ScenarioUI {
    private HurricaneReliefApp app;

    /**
     * Creats the scenario and loads the saved data. 
     */
    public ScenarioUI() {
        app = new HurricaneReliefApp();
        app.initialize();
    }

    /**
     * Runs every step of the scenario in order.
     */
    public void run() {
        ReliefRequest request = carterAsksForHelp();
        Shelter shelter = rosaPicksUpTheRequest(request);
        rosaPrintsTheTripSheet(request, shelter);
        rosaFinishesTheRequest(request);
        felixChecksTheFamilyIn(shelter);
    }

    /** Step 1: the Carter family makes an account and asks for a shelter. */
    private ReliefRequest carterAsksForHelp() {
        System.out.println("\n--- Step 1: David Carter asks for help ---");
        ArrayList<AccountRole> roles = new ArrayList<>();
        roles.add(AccountRole.RESIDENT);
        app.createAccount ("David", "Carter", "dcarter@email.com", "803-555-0142",
            "25 Calhoun Street, Charleston, SC 29401", "dcarter", "carter2026",
            roles, "25 Calhoun Street, Charleston, SC 29401");
        app.login("dcarter", "carter2026");
        app.submitReliefRequest(RequestType.TRANSPORTATION, 
                "Family of 3 and our dog Buddy need a ride to a shelter. Our daughter needs medical care.",
                "25 Calhoun Street, Charleston, SC 29401", 3);
        ReliefRequest request = app.getMyRequests().get(0);
        app.logout();
        return request;
    }

    /** Steps 2 to 5: Rosa sees the open requests, finds a shelter and claims Carter's. */
    private Shelter rosaPicksUpTheRequest(ReliefRequest request) {
       System.out.println("\n--- Step 2: volunteer Rosa logs in ---");
       app.login("rosadelgado", "rosa2026");

       System.out.println("\nOpen requests, most urgent first:");
       List<ReliefRequest> open = app.getOpenRequestsByUrgency();
       for (ReliefRequest openRequest : open) {
            System.out.println(" " + openRequest.getPriority() + " " + openRequest.getType()
                    + " " + openRequest.getLocation());
       }

       System.out.println("\n--- Step 3: Rosa opens the Carter request ---");
       System.out.println(" " + request.getDescription());
       System.out.println(" People: " + request.getNumberOfPeopleNeeded()
               + ", submitted by " + request.getSubmittedBy().getUsername());
       
       System.out.println("\n--- Step 4: closest shelter that takes dogs and has medical staff ---");
       ArrayList<Accommodation> needs = new ArrayList<>();
       needs.add(Accommodation.PET_FRIENDLY);
       needs.add(Accommodation.MEDICAL_SUPPORT);
       Shelter shelter = ShelterList.getInstance().findClosestShelter(
               Address.fromString(request.getLocation()), needs, 3);
       System.out.println("  " + shelter.getName() + ", " + shelter.getAddress());

       System.out.println("\n--- Step 5: Rosa claims the request ---");
       app.claimRequest(request.getRequestId());
       System.out.println(" Status is now " + request.getStatus()
               + ", assigned to " + request.getAssignedVolunteer().getUsername());
       return shelter; 
    }

    /** Step 6: Rosa saves a trip sheet with the family, the shelter and directions. */
    private void rosaPrintsTheTripSheet(ReliefRequest request, Shelter shelter) {
        System.out.println("\n--- Step 6: Rosa saves a trip sheet ---");
        String[] directions;
        try {
            directions = MapTools.getInstance().getDrivingDirections(
                    Address.fromString(request.getLocation()), Address.fromString(shelter.getAddress()));
        } catch (Exception e) {
            directions = new String[] {"Directions unavailable, use the shelter address."};
        }
        if (DataWriter.saveTripSheet(request, shelter, directions)) {
            System.out.println(" Saved trip-sheet.txt");
        }
    }

    /** Steps 7 and 8: Rosa starts the trip and marks it complete. */
    private void rosaFinishesTheRequest(ReliefRequest request) {
        System.out.println("\n--- Step 7: Rosa drives the family to the shelter ---");
        app.startRequest(request.getRequestId());

        System.out.println("\n--- Step 8: Rosa marks the request complete ---");
        app.completeRequest(request.getRequestId());
        System.out.println(" Status is now " + request.getStatus());
        app.logout();
    }

    /** Step 9: shelter manager Felix checks the family in. */
    private void felixChecksTheFamilyIn(Shelter shelter) {
        System.out.println("\n--- Step 9: shelter manager Felix checks the family in ---");
        app.login("felixgrant", "felix2026");
        System.out.println("  " + shelter.getName() + ": " + shelter.getOccupancy() + " of "
                + shelter.getCapacity() + " beds used");
        shelter.setOccupancy(shelter.getOccupancy() + 3);
        System.out.println("  Checked in the Carter family and Buddy");
        System.out.println("  " + shelter.getName() + ": " + shelter.getOccupancy() + " of "
                + shelter.getCapacity() + " beds used");
        app.logout();
    }

    /**
     * Starts the scenario.
     * 
     * @param args not used
     */
    public static void main(String[] args) {
        new ScenarioUI().run();
    }
}