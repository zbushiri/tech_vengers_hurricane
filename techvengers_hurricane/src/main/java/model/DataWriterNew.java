package model;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DataWriterNew extends DataConstants {

	
	public static void saveUsers() {
		UserList userListInstance = UserList.getInstance();
		ArrayList<User> userList = userListInstance.getUsers();
		JSONArray jsonUsers = new JSONArray();

		//creating all the json objects
		for(int i=0; i< userList.size(); i++) {
			jsonUsers.add(getUserJSON(userList.get(i)));
		}
		
		//Write JSON file
        try (FileWriter file = new FileWriter(USER_FILE)) {
 
            file.write(jsonUsers.toJSONString());
            file.flush();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public static JSONObject getUserJSON(User user) {
		JSONObject userDetails = new JSONObject();
		userDetails.put(USER_ID, user.getUserId().toString());
		userDetails.put(USER_FIRST_NAME, user.getFirstName());
		userDetails.put(USER_LAST_NAME, user.getLastName());
		userDetails.put(USER_PHONE_NUMBER, user.getPhoneNumber());
		userDetails.put(USER_EMAIL, user.getEmail());
		userDetails.put(USER_ADDRESS, user.getAddress());
		userDetails.put(USER_USERNAME, user.getUsername());
		userDetails.put(USER_PASSWORD, user.getPasswordHash());
		userDetails.put(USER_ROLES, user.getRoles().toString());
		userDetails.put(ASSOCIATED_PERSONS, user.getAssociatedPersons().toString());
		userDetails.put(VERIFICATION_STATUS, user.getVerificationStatus().toString());
		userDetails.put(USER_LAST_KNOWN_LOCATION, user.getLastKnownLocation());
		userDetails.put(SAFETY_STATUS, user.getSafetyStatus().toString());
		userDetails.put(SAFETY_STATUS_UPDATED_AT, user.getSafetyStatusUpdatedAt().toString());

        
        return userDetails;
	}
}