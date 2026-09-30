package com.techvengershurricane.system;

import com.techvengershurricane.data.HurricaneRepository;
import com.techvengershurricane.data.ReliefRequestRepository;
import com.techvengershurricane.data.ShelterRepository;
import com.techvengershurricane.data.UserRepository;
import com.techvengershurricane.data.VolunteerRepository;
import com.techvengershurricane.model.HurricaneEvent;
import com.techvengershurricane.model.ReliefRequest;
import com.techvengershurricane.model.Shelter;
import com.techvengershurricane.model.User;
import com.techvengershurricane.model.Volunteer;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class HurricaneReliefSystem {
    private final UserRepository users;
    private final VolunteerRepository volunteers;
    private final ShelterRepository shelters;
    private final ReliefRequestRepository requests;
    private final HurricaneRepository hurricanes;

    public HurricaneReliefSystem(Path jsonDirectory) {
        /* Facade: connects all JSON repositories in one place. */
        users = new UserRepository(jsonDirectory);
        volunteers = new VolunteerRepository(jsonDirectory);
        shelters = new ShelterRepository(jsonDirectory);
        requests = new ReliefRequestRepository(jsonDirectory);
        hurricanes = new HurricaneRepository(jsonDirectory);
    }

    public List<User> getUsers() { return users.getAll(); }
    public List<Volunteer> getVolunteers() { return volunteers.getAll(); }
    public List<Shelter> getShelters() { return shelters.getAll(); }
    public List<ReliefRequest> getRequests() { return requests.getAll(); }
    public List<HurricaneEvent> getHurricanes() { return hurricanes.getAll(); }

    public Optional<User> findUser(String id) { return users.findById(id); }
    public Optional<Shelter> findShelter(String id) { return shelters.findById(id); }
    public Optional<ReliefRequest> findRequest(String id) { return requests.findById(id); }

    public void addUser(User user) { users.add(user); }
    public boolean editUser(User user) { return users.edit(user); }
    public boolean deleteUser(String id) { return users.delete(id); }

    public void addVolunteer(Volunteer volunteer) { volunteers.add(volunteer); }
    public boolean editVolunteer(Volunteer volunteer) { return volunteers.edit(volunteer); }
    public boolean deleteVolunteer(String id) { return volunteers.delete(id); }

    public void addShelter(Shelter shelter) { shelters.add(shelter); }
    public boolean editShelter(Shelter shelter) { return shelters.edit(shelter); }
    public boolean deleteShelter(String id) { return shelters.delete(id); }

    public void addHurricane(HurricaneEvent event) { hurricanes.add(event); }
    public boolean editHurricane(HurricaneEvent event) { return hurricanes.edit(event); }
    public boolean deleteHurricane(String id) { return hurricanes.delete(id); }

    public void submitRequest(ReliefRequest request) {
        /* Facade: the resident screen calls this method. */
        /* TODO: validate, calculate priority, and check duplicates. */
        requests.add(request);
    }

    public boolean editRequest(ReliefRequest request) { return requests.edit(request); }
    public boolean deleteRequest(String id) { return requests.delete(id); }

    public boolean claimRequest(String requestId, int volunteerId) {
        /* Facade: the volunteer screen calls this method. */
        if (volunteers.findById(String.valueOf(volunteerId)).isEmpty()) {
            return false;
        }
        Optional<ReliefRequest> match = requests.findById(requestId);
        if (match.isEmpty()) {
            return false;
        }
        ReliefRequest request = match.get();
        request.setAssignedVolunteerId(volunteerId);
        request.setStatus("IN_PROGRESS");
        return requests.edit(request);
    }

    public boolean updateSafetyStatus(int userId, String newStatus) {
        /* Facade: keeps UI code away from JSON details. */
        Optional<User> match = users.findById(String.valueOf(userId));
        if (match.isEmpty()) {
            return false;
        }
        User user = match.get();
        user.setSafetyStatus(newStatus);
        return users.edit(user);
    }
}
