package model;
import java.util.ArrayList;
import java.util.List;

public class ReliefRequestCatalog {
    private static ReliefRequestCatalog reliefRequestCatalog;
    private ArrayList<ReliefRequest> reliefRequests;
    
    private ReliefRequestCatalog() {}

    public static ReliefRequestCatalog getInstance() {
        if (reliefRequestCatalog == null) {
            reliefRequestCatalog = new ReliefRequestCatalog();
            reliefRequestCatalog.reliefRequests = new ArrayList<>();
        }
        return reliefRequestCatalog;
    }

    public void submitRequest(ReliefRequest request) {
        reliefRequests.add(request);
    }

    public List<ReliefRequest> getOpenRequestsByUrgency() {
        return reliefRequests;
    }

    public List<ReliefRequest> searchByType(RequestType type) {
        List<ReliefRequest> result = new ArrayList<>();
        for (ReliefRequest request : reliefRequests) {
            if (request.getType() == type) {
                result.add(request);
            }
        }
        return result;
    }

    public List<ReliefRequest> findDuplicates(String Location) {
        List<ReliefRequest> result = new ArrayList<>();
        for (ReliefRequest request : reliefRequests) {
            if (request.getLocation().equals(Location)) {
                result.add(request);
            }
        }
        return result;
    }

    public List<ReliefRequest> getOpenRequestsForVolunteers() {
        List<ReliefRequest> result = new ArrayList<>();
        for (ReliefRequest request : reliefRequests) {
            if (request.isOpen() && request.isForVolunteers()) {
                result.add(request);
            }
        }
        return result;
    }

    public int getAvailableVolunteerCount() {
        int count = 0;
        for (ReliefRequest request : reliefRequests) {
            if (request.isOpen() && request.isForVolunteers()) {
                count++;
            }
        }
        return count;
    }



    public boolean save() {
        // TODO: Implement the save logic here
        return true;
    }


}
