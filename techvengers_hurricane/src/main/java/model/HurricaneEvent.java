package model;
import java.util.Date;
import java.util.List;

public class HurricaneEvent {
    private String name;
    private Date startDate;
    private Date endDate;
    private List<String> affectedAreas;
    private EventStatus status;

    public HurricaneEvent(String name, Date startDate, Date endDate, List<String> affectedAreas, EventStatus status) {
        setName(name);
        setStartDate(startDate);
        setEndDate(endDate);
        setAffectedAreas(affectedAreas);
        setStatus(status);
    }

    public static void issueAdvisory(String message) {
        // TODO: Implementation for issuing an advisory
        System.out.println("Advisory issued: " + message);
    }

    public static void refreshStormData() {
    // TODO: Implementation for refreshing storm data
        System.out.println("Storm data refreshed.");
    }


    public String getName() {
        return name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public List<String> getAffectedAreas() {
        return affectedAreas;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public void setAffectedAreas(List<String> affectedAreas) {
        this.affectedAreas = affectedAreas;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

     @Override
    public String toString() {
        return "HurricaneEvent{" + "\n" +
                "name='" + name + "\n" +
                "startDate=" + startDate + "\n" +
                "endDate=" + endDate + "\n" +
                "affectedAreas=" + affectedAreas + "\n" +
                "status=" + status + "\n" +
                '}';
    }
}
