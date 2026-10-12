package model;
import java.util.Scanner;
import java.util.ArrayList;

public class UI {
    private String[] mainMenuOptions = {"Create Account", "Login", "Logout"};
    private Scanner scanner;
    private HurricaneReliefApp app;
    
    public UI() {
        scanner = new Scanner(System.in);
        app = new HurricaneReliefApp();
    }

    public void run() {
        System.out.println("Main Menu:");
        while (true) {
            displayMainMenu();

            int userCommand = getUserCommand(mainMenuOptions.length);

        if(userCommand == -1) {
				System.out.println("Not a valid command");
				continue;
			}
			
			//if they picked the last option then log them out
			if(userCommand == mainMenuOptions.length -1) {
				app.logout();
				break;
			}
		
			switch(userCommand) {
				case(0):
					createAccount();
					break;
				case(1):
					login();
					break;
			}
		}
	}


    private void displayMainMenu() {
	    System.out.println("\n************ Main Menu *************");
	    for(int i=0; i< mainMenuOptions.length; i++) {
	        System.out.println((i+1) + ". " + mainMenuOptions[i]);
	    }
	    System.out.println("\n");
    }
    private int getUserCommand(int numCommands) {
	    System.out.print("What would you like to do?: ");
		
	    String input = scanner.nextLine();
	    int command = Integer.parseInt(input) - 1;
		
	    if(command >= 0 && command <= numCommands -1) return command;
		
	    return -1;
    }

    private void createAccount() {
        String firstName = getField("First Name");
        String lastName = getField("Last Name");
        String email = getField("Email");
        String address = getField("Address");
        String username = getField("Username");
        String password = getField("Password");
        String lastKnownLocation = getField("Last Known Location");
        // For simplicity, we'll assume the user has a single role
        AccountRole role = AccountRole.valueOf(getField("Role (e.g., DONOR, VOLUNTEER, SHELTER_MANAGER)").toUpperCase());
        ArrayList<AccountRole> roles = new ArrayList<>();
        roles.add(role);

        app.createAccount(firstName, lastName, email, address, username, password, roles, lastKnownLocation);
    }

    private void login() {
        String username = getField("Username");
        String password = getField("Password");
        app.login(username, password);
        if(app.getCurrentUser() != null) {
            System.out.println("Login successful");
        } else {
            System.out.println("Login failed");
        }
    }

    private String getField(String prompt) {
		System.out.print(prompt + ": ");
		return scanner.nextLine();
	}

    public static void main(String[] args) {
		UI ui = new UI();
		ui.run();

	}
}
