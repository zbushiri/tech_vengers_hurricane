package model;

import java.util.ArrayList;

public class HurricaneReliefApp {
    private UserList userList;
   // private ShelterList shelterList;
    private ReliefRequestCatalog reliefRequestCatalog;
    private User currentUser;

    public HurricaneReliefApp() {
        userList = UserList.getInstance();
        //shelterList = ShelterList.getInstance();
        reliefRequestCatalog = ReliefRequestCatalog.getInstance();
    }

    public boolean login(String username, String password) {
        currentUser = userList.authenticate(username, password);
        return currentUser != null;
    }

    public void createAccount(String firstName, String lastName, String email, String address,
        String username, String password, ArrayList<AccountRole> roles,String lastKnownLocation) {
        userList.addUser(firstName, lastName, email, address, username, password, roles, lastKnownLocation);
    }

    public boolean submitReliefRequest(ReliefRequest request) {
        if (currentUser == null) {
            return false;
        }
        reliefRequestCatalog.submitRequest(request);
        return true;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}