package model;

import java.util.ArrayList;


class TestUI {
	private  HurricaneReliefApp app = new HurricaneReliefApp();
    private UserList userListInstance = UserList.getInstance();
	


	public void testCreateValidAccount() {
		app.createAccount("Edward", "Marks", "emarks@gmail.com",
        "1234567890", "300 Lincoln Street", 
        "emarks", "emarks127", new ArrayList<AccountRole>() {{add(AccountRole.RESIDENT);}}, 
        "300 Lincoln Street");
		app.login("emarks", "emarks127");
		User myUser = app.getCurrentUser();
        System.out.println(myUser.toString());
        DataWriterNew.saveUsers();
        app.logout();
	}

    public void testLogin() {
        userListInstance.getUsers();
        app.login("emarks", "emarks127");
        User myUser = app.getCurrentUser();
        System.out.println(myUser.getUsername().equals("emarks") + ": " + myUser.getUsername() + " is logged in.");
        DataWriterNew.saveUsers();
        app.logout();
    }


    public static void main(String[] args) {
        TestUI testUI = new TestUI();
        testUI.testCreateValidAccount();
        testUI.testLogin();

    }
}