package model;

import java.util.ArrayList;


class TestUI {
	private  HurricaneReliefApp app = new HurricaneReliefApp();
	

	public void testCreateValidAccount() {
		app.createAccount("Edward", "Marks", "emarks@gmail.com", "300 Lincoln Street", 
        "emarks", "emarks127", new ArrayList<AccountRole>() {{add(AccountRole.RESIDENT);}}, 
        "300 Lincoln Street");
		app.login("emarks", "emarks127");
		User myUser = app.getCurrentUser();
        System.out.println(myUser.toString());
	}

    public static void main(String[] args) {
        TestUI testUI = new TestUI();
        testUI.testCreateValidAccount();
    }
}