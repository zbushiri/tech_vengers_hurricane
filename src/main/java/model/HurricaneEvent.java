package model;
import java.util.Date;
import java.util.List;

/**
 * A hurricane event.
 * @author Team Tech-vengers
 */
public class HurricaneEvent {
    private String name;
    private Date startDate;
    private Date endDate;
    private List<String> affectedAreas;
    private EventStatus status;

    /**
     * Creates a new HurricaneEvent.
     * @param name The name of the hurricane event.
     * @param startDate The start date of the hurricane event.
     * @param endDate The end date of the hurricane event.
     * @param affectedAreas The areas affected by the hurricane event.
     * @param status The status of the hurricane event.
     */
    public HurricaneEvent(String name, Date startDate, Date endDate, List<String> affectedAreas, EventStatus status) {
        setName(name);
        setStartDate(startDate);
        setEndDate(endDate);
        setAffectedAreas(affectedAreas);
        setStatus(status);
    }

    /**
     * Issues an advisory for the hurricane event.
     * @param message The advisory message.
     */
    public static void issueAdvisory(String message) {
        System.out.println("Advisory issued: " + message);
    }

    /**
     * Refreshes the storm data for the hurricane event.
     */
    public static void refreshStormData() {
        System.out.println("Storm data refreshed.");
    }


    /**
     * Gets the name of the hurricane event.
     * @return The name of the hurricane event.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the start date of the hurricane event.
     * @return The start date of the hurricane event.
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Gets the end date of the hurricane event.
     * @return The end date of the hurricane event.
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Gets the areas affected by the hurricane event.
     * @return The areas affected by the hurricane event.
     */
    public List<String> getAffectedAreas() {
        return affectedAreas;
    }

    /**
     * Gets the status of the hurricane event.
     * @return The status of the hurricane event.
     */
    public EventStatus getStatus() {
        return status;
    }

    /**
     * Sets the status of the hurricane event.
     * @param status The status of the hurricane event.
     */
    public void setStatus(EventStatus status) {
        this.status = status;
    }

    /**
     * Sets the areas affected by the hurricane event.
     * @param affectedAreas The areas affected by the hurricane event.
     */
    public void setAffectedAreas(List<String> affectedAreas) {
        this.affectedAreas = affectedAreas;
    }

    /**
     * Sets the name of the hurricane event.
     * @param name The name of the hurricane event.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the start date of the hurricane event.
     * @param startDate The start date of the hurricane event.
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Sets the end date of the hurricane event.
     * @param endDate The end date of the hurricane event.
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /** Formats persisted UTC calendar dates without shifting to the previous local day. */
    private static String calendarDate(Date date) {
        return date == null ? "unknown" : date.toInstant().atZone(java.time.ZoneOffset.UTC).toLocalDate().toString();
    }

     /**
      * Formats this object for display.
      * @return hurricane details with dates formatted as UTC calendar dates
      */
     @Override
    public String toString() {
        return "HurricaneEvent{" + "\n" +
                "name='" + name + "\n" +
                "startDate=" + calendarDate(startDate) + "\n" +
                "endDate=" + calendarDate(endDate) + "\n" +
                "affectedAreas=" + affectedAreas + "\n" +
                "status=" + status + "\n" +
                '}';
    }
}
