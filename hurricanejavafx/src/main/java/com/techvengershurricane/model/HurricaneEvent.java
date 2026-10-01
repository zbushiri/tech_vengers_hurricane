package com.techvengershurricane.model;

import java.util.ArrayList;
import java.util.List;

/* MODEL: add hurricane dates, areas, and status behavior here. */
public class HurricaneEvent {
    private String name;
    private String startDate;
    private String endDate;
    private String status;
    private List<String> affectedAreas = new ArrayList<>();

    public HurricaneEvent() {
        /* JSON: required by Gson. */
    }

    public HurricaneEvent(String name, String startDate, String status) {
        this.name = name;
        this.startDate = startDate;
        this.status = status;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<String> getAffectedAreas() { return affectedAreas; }
    public void setAffectedAreas(List<String> affectedAreas) { this.affectedAreas = affectedAreas; }
}
