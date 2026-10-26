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
	


	/**
	 * Test create valid account.
	 */
	public void testCreateValidAccount() {
		app.createAccount("Edward", "Marks", "emarks@gmail.com",
        "1234567890", "300 Lincoln Street", 
        "emarks", "emarks127", new ArrayList<AccountRole>() {{add(AccountRole.RESIDENT);}}, 
        "300 Lincoln Street");
		app.login("emarks", "emarks127");
		User myUser = app.getCurrentUser();
        this.createdUserToString = myUser.toString();
        System.out.println(createdUserToString);
        DataWriter.saveUsers();
        app.logout();
	}

    /**
     * Test login.
     */
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

    /**
     * Check to string.
     * @param createdUser created user for this operation
     * @param loginUser login user for this operation
     * @return true when the operation succeeds
     */
    public boolean checkToString(String createdUser, String loginUser) {
        
        return createdUser.equals(loginUser);
    }

    /**
     * Test scenario1.
     */
    public void testScenario1() {
        
    }

    /**
     * Starts this application entry point.
     * @param args args for this operation
     */
    public static void main(String[] args) {
        TestUI testUI = new TestUI();
        testUI.testCreateValidAccount();
        testUI.testLogin();
        System.out.println("Check toString: " + testUI.checkToString(testUI.createdUserToString, testUI.loginUserToString));

    }
}
