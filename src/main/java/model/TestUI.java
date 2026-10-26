package model;

import java.util.ArrayList;


/**
 * Manual account-creation and login demonstration that prints results and saves users.
 * Running it writes to the configured users JSON file; it is not a JUnit test class.
 *
 * @author Team Tech-vengers
 */
class TestUI {
	private  HurricaneReliefApp app = new HurricaneReliefApp();
    private UserList userListInstance = UserList.getInstance();
    private String createdUserToString;
    private String loginUserToString;

    // Load the saved data first, so saveUsers() doesn't overwrite users.json with only the test user
    /** Loads the saved users before demonstrating account creation and login. */
    TestUI() {
        app.initialize();
    }
	


	/**
	 * Creates the sample Edward Marks account, logs in and saves the users file.
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
     * Logs in as the sample account and prints its profile for comparison.
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
     * Compares the profile text captured after creation and after login.
     * @param createdUser profile text captured immediately after account creation
     * @param loginUser profile text captured after logging in again
     * @return true if the two profile strings are equal
     */
    public boolean checkToString(String createdUser, String loginUser) {
        
        return createdUser.equals(loginUser);
    }

    /**
     * Runs the manual account-creation and login demonstration.
     * @param args command-line arguments; unused by this entry point
     */
    public static void main(String[] args) {
        TestUI testUI = new TestUI();
        testUI.testCreateValidAccount();
        testUI.testLogin();
        System.out.println("Check toString: " + testUI.checkToString(testUI.createdUserToString, testUI.loginUserToString));

    }
}
