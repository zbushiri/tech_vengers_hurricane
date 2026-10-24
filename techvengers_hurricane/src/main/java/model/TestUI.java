package model;

import java.util.ArrayList;


class TestUI {
	private  HurricaneReliefApp app = new HurricaneReliefApp();
    private UserList userListInstance = UserList.getInstance();
    private String createdUserToString;
    private String loginUserToString;

    // Load the saved data first, so saveUsers() doesn't overwrite users.json with only the test user
    TestUI() {
        app.initialize();
    }
	


	public void testCreateValidAccount() {
		app.createAccount("Edward", "Marks", "emarks@gmail.com",
        "1234567890", new location.Address("300 Lincoln Street", "Columbia", "SC", "29208"),
        "emarks", "emarks127", new ArrayList<AccountRole>() {{add(AccountRole.RESIDENT);}}, 
        new location.MapLocation(new location.Address("300 Lincoln Street", "Columbia", "SC", "29208"), new location.Coordinates(34.0007, -81.0348)));
		app.login("emarks", "emarks127");
		User myUser = app.getCurrentUser();
        this.createdUserToString = myUser.toString();
        System.out.println(createdUserToString);
        DataWriter.saveUsers();
        app.logout();
	}

    public void testLogin() {
        userListInstance.getUsers();
        app.login("emarks", "emarks127");
        User myUser = app.getCurrentUser();
        System.out.println(myUser.getUsername().equals("emarks") + ": " + myUser.getUsername() + " is logged in.");
        this.loginUserToString = myUser.toString();
        System.out.println(this.loginUserToString);
        DataWriter.saveUsers();
        app.logout();
    }

    public boolean checkToString(String createdUser, String loginUser) {
        
        return createdUser.equals(loginUser);
    }

    public static void main(String[] args) {
        TestUI testUI = new TestUI();
        testUI.testCreateValidAccount();
        testUI.testLogin();
        System.out.println("Check toString: " + testUI.checkToString(testUI.createdUserToString, testUI.loginUserToString));

    }
}
