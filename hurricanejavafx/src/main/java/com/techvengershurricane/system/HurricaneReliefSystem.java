package com.techvengershurricane.system;

import com.techvengershurricane.data.JsonDataAccess;
import com.techvengershurricane.model.HurricaneEvent;
import com.techvengershurricane.model.ReliefRequest;
import com.techvengershurricane.model.Shelter;
import com.techvengershurricane.model.User;
import com.techvengershurricane.model.Volunteer;

import java.nio.file.Path;
import java.util.List;

/* FACADE: controllers call this class instead of opening JSON files. */
public class HurricaneReliefSystem {
    private final JsonDataAccess<User> users;
    private final JsonDataAccess<Volunteer> volunteers;
    private final JsonDataAccess<Shelter> shelters;
    private final JsonDataAccess<ReliefRequest> requests;
    private final JsonDataAccess<HurricaneEvent> hurricanes;

    public HurricaneReliefSystem(Path jsonFolder) {
        /* SETUP: connects each model to its original JSON file. */
        users = new JsonDataAccess<>(jsonFolder.resolve("users.json"), User[].class,
                user -> String.valueOf(user.getUserId()));
        volunteers = new JsonDataAccess<>(jsonFolder.resolve("volunteers.json"), Volunteer[].class,
                volunteer -> String.valueOf(volunteer.getUserId()));
        shelters = new JsonDataAccess<>(jsonFolder.resolve("shelters.json"), Shelter[].class,
                shelter -> String.valueOf(shelter.getShelterId()));
        requests = new JsonDataAccess<>(jsonFolder.resolve("requests.json"), ReliefRequest[].class,
                ReliefRequest::getRequestId);
        hurricanes = new JsonDataAccess<>(jsonFolder.resolve("hurricanes.json"), HurricaneEvent[].class,
                HurricaneEvent::getName);
    }

    /* GET: screens use these methods to display saved data. */
    public List<User> getUsers() { return users.getAll(); }
    public List<Volunteer> getVolunteers() { return volunteers.getAll(); }
    public List<Shelter> getShelters() { return shelters.getAll(); }
    public List<ReliefRequest> getRequests() { return requests.getAll(); }
    public List<HurricaneEvent> getHurricanes() { return hurricanes.getAll(); }

    /* USER: put user rules before these JSON calls. */
    public void addUser(User item) { users.add(item); }
    public boolean editUser(User item) { return users.edit(item); }
    public boolean deleteUser(String id) { return users.delete(id); }

    /* VOLUNTEER: put volunteer rules here. */
    public void addVolunteer(Volunteer item) { volunteers.add(item); }
    public boolean editVolunteer(Volunteer item) { return volunteers.edit(item); }
    public boolean deleteVolunteer(String id) { return volunteers.delete(id); }

    /* SHELTER: put capacity and status rules here. */
    public void addShelter(Shelter item) { shelters.add(item); }
    public boolean editShelter(Shelter item) { return shelters.edit(item); }
    public boolean deleteShelter(String id) { return shelters.delete(id); }

    /* REQUEST: resident request workflow starts here. */
    public void addRequest(ReliefRequest item) { requests.add(item); }
    public boolean editRequest(ReliefRequest item) { return requests.edit(item); }
    public boolean deleteRequest(String id) { return requests.delete(id); }

    /* HURRICANE: coordinator event workflow starts here. */
    public void addHurricane(HurricaneEvent item) { hurricanes.add(item); }
    public boolean editHurricane(HurricaneEvent item) { return hurricanes.edit(item); }
    public boolean deleteHurricane(String id) { return hurricanes.delete(id); }

    public void submitRequest(ReliefRequest request) {
        /* TODO: validate, check duplicates, and calculate priority here. */
        addRequest(request);
    }

    public boolean claimRequest(String requestId, int volunteerId) {
        /* TODO: check volunteer eligibility before assigning the request. */
        for (ReliefRequest request : getRequests()) {
            if (request.getRequestId().equals(requestId)) {
                request.setAssignedVolunteerId(volunteerId);
                request.setStatus("IN_PROGRESS");
                return editRequest(request);
            }
        }
        return false;
    }
}
