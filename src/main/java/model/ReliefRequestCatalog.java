package model;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Shared collection of relief requests, with duplicate detection, priority ordering and assignment lookup.
 * @author Team Tech-vengers
 */
public class ReliefRequestCatalog {
    private static ReliefRequestCatalog reliefRequestCatalog;
    private ArrayList<ReliefRequest> reliefRequests;

    private ReliefRequestCatalog() {}

    /**
     * Returns the shared request catalog.
     * @return the shared instance, created on first access
     */
    public static ReliefRequestCatalog getInstance() {
        if (reliefRequestCatalog == null) {
            reliefRequestCatalog = new ReliefRequestCatalog();
            reliefRequestCatalog.reliefRequests = new ArrayList<>();
        }
        return reliefRequestCatalog;
    }

    /**
     * Returns the requests.
     * @return the requests
     */
    public ArrayList<ReliefRequest> getRequests() {
        return reliefRequests;
    }

    /**
     * Looks up a request by its unique identifier.
     * @param requestId unique identifier of the relief request
     * @return matching request, or null when the identifier is not present
     */
    public ReliefRequest getRequest(UUID requestId) {
        for (ReliefRequest request : reliefRequests) {
            if (request.getRequestId().equals(requestId)) {
                return request;
            }
        }
        return null;
    }

    /**
     * Adds a request to the catalog. If an open request of the same type
     * already exists at the same location, the new one is marked as its
     * duplicate. The priority is then calculated.
     * @param request relief request to store or update
     */
    public void submitRequest(ReliefRequest request) {
        if (request == null || getRequest(request.getRequestId()) != null) {
            return;
        }
        for (ReliefRequest existing : findDuplicates(request.getLocation())) {
            if (existing.getType() == request.getType()) {
                request.setIsDuplicateOf(existing);
                break;
            }
        }
        request.setPriority(request.calculatePriority());
        reliefRequests.add(request);
    }

    /**
     * Adds a request read from the JSON file exactly as it was saved.
     * @param request relief request to store or update
     */
    public void addLoadedRequest(ReliefRequest request) {
        if (request != null && getRequest(request.getRequestId()) == null) {
            reliefRequests.add(request);
        }
    }

    /**
     * Open requests, most urgent first. Requests with the same priority are
     * listed oldest first.
     * @return the open requests by urgency
     */
    public List<ReliefRequest> getOpenRequestsByUrgency() {
        List<ReliefRequest> result = new ArrayList<>();
        for (ReliefRequest request : reliefRequests) {
            if (request.isOpen()) {
                result.add(request);
            }
        }
        result.sort(Comparator
                .comparing((ReliefRequest r) -> r.getPriority() == null ? 0 : r.getPriority().ordinal())
                .reversed()
                .thenComparing(ReliefRequest::getDateSubmitted, Comparator.nullsLast(Comparator.naturalOrder())));
        return result;
    }

    /**
     * Finds all stored requests with the specified assistance type.
     * @param type kind of relief assistance requested
     * @return matching requests, including closed requests
     */
    public List<ReliefRequest> searchByType(RequestType type) {
        List<ReliefRequest> result = new ArrayList<>();
        for (ReliefRequest request : reliefRequests) {
            if (request.getType() == type) {
                result.add(request);
            }
        }
        return result;
    }

    /**
     * Open requests at the same location, ignoring case and extra spaces.
     * @param location full street address where help is needed
     * @return open requests with the same location text, ignoring case and surrounding whitespace
     */
    public List<ReliefRequest> findDuplicates(String location) {
        List<ReliefRequest> result = new ArrayList<>();
        if (location == null) {
            return result;
        }
        String wanted = location.trim();
        for (ReliefRequest request : reliefRequests) {
            if (request.isOpen() && request.getLocation() != null
                    && request.getLocation().trim().equalsIgnoreCase(wanted)) {
                result.add(request);
            }
        }
        return result;
    }

    /**
     * Open requests that volunteers can help with and nobody has claimed yet,
     * most urgent first.
     * @return the open requests for volunteers
     */
    public List<ReliefRequest> getOpenRequestsForVolunteers() {
        List<ReliefRequest> result = new ArrayList<>();
        for (ReliefRequest request : getOpenRequestsByUrgency()) {
            if (request.getStatus() == RequestStatus.SUBMITTED && request.isForVolunteers()
                    && !request.hasOpenOriginal()) {
                result.add(request);
            }
        }
        return result;
    }

    /**
     * Lets a volunteer claim a request by its id.
     * 
     * @return true if the volunteer now has the request
     * @param requestId unique identifier of the relief request
     * @param volunteer volunteer attempting to claim or start the assignment
     */
    public boolean claimRequest(UUID requestId, VolunteerProfile volunteer) {
        ReliefRequest request = getRequest(requestId);
        if (request == null || request.getStatus() != RequestStatus.SUBMITTED) {
            return false;
        }
        request.claimRequest(volunteer);
        return request.getStatus() == RequestStatus.ASSIGNED;
    }

    /**
     * Counts the volunteers who are free to take a request right now.
     * @return the available volunteer count
     */
    public int getAvailableVolunteerCount() {
        int count = 0;
        for (User user : UserList.getInstance().getUsers()) {
            VolunteerProfile volunteer = user.getVolunteerProfile();
            if (volunteer != null && volunteer.canClaimRequests()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Saves the shared request catalog to JSON.
     * @return true if the requests JSON file was written successfully
     */
    public boolean save() {
        return DataWriter.saveRequests();
    }
}
