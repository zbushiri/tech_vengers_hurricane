package model;

import java.util.ArrayList;
import model.ShelterList.Location;

public class HurricaneReliefApp {
    private UserList userList;
    private ShelterList shelterList;
    private ReliefRequestCatalog reliefRequestCatalog;
    private User currentUser;

    public HurricaneReliefApp() {
        userList = UserList.getInstance();
        shelterList = ShelterList.getInstance();
        reliefRequestCatalog = ReliefRequestCatalog.getInstance();
        loadData();
    }

    private void loadData() {
        DataLoader.getUsers();
        DataLoader.getShelters();
    }

    public boolean login(String username, String password) {
        currentUser = userList.authenticate(username, password);
        return currentUser != null;
    }

    public void createAccount(String firstName, String lastName, String email, String phoneNumber, String address,
        String username, String password, ArrayList<AccountRole> roles,String lastKnownLocation) {
        userList.addUser(firstName, lastName, email, phoneNumber, address, username, password, roles, lastKnownLocation);
    }

    public boolean submitReliefRequest(ReliefRequest request) {
        if (currentUser == null) {
            return false;
        }
        reliefRequestCatalog.submitRequest(request);
        return true;
    }

    public ArrayList<Shelter> searchByProximity(Location location, double maxDistance) {
        return shelterList.searchByProximity(location, maxDistance);
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}