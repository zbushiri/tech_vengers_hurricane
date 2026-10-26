package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Builds the right kind of account for a role. Volunteers get a
 * VolunteerProfile, and every other role gets a plain User.
 * 
 * Keys read from details: username, password, firstName, lastName,
 * phoneNumber, email, address. Volunteers may also give skills and
 * equipment (comma separated text or a List) and transportationAccess
 * ("true" or "false").
 * @author Team Tech-vengers
 */
public class AccountFactory {
    private AccountFactory() {
    }

    /**
     * Create account.
     * @return the new account, or null if the role, username or password is missing
     * @param role role for this operation
     * @param details details for this operation
     */
    public static User createAccount(AccountRole role, Map<String, ?> details) {
        if (role == null || details == null) {
            return null;
        }
        String username = text(details, "username");
        String password = text(details, "password");
        if (username.isEmpty() || password.isEmpty()) {
            return null;
        }

        User user = new User(username, password);
        user.setFirstName(text(details, "firstName"));
        user.setLastName(text(details, "lastName"));
        user.setPhoneNumber(text(details, "phoneNumber"));
        user.setEmail(text(details, "email"));
        user.setAddress(text(details, "address"));
        user.getRoles().add(role);

        if (role != AccountRole.VOLUNTEER) {
            return user;
        }
        // A new volunteer must pass a background check before claiming requests.
        return new VolunteerProfile(user,
                list(details.get("skills")),
                list(details.get("equipment")),
                BackgroundCheckStatus.PENDING,
                Boolean.parseBoolean(text(details, "transportationAccess")),
                AvailabilityStatus.AVAILABLE);
    }

    private static String text(Map<String, ?> details, String key) {
        Object value = details.get(key);
        return value == null ? "" : String.valueOf(value).trim();
    }

    private static List<String> list(Object value) {
        List<String> items = new ArrayList<>();
        JsonNode node = new ObjectMapper().valueToTree(value);
        if (node != null && node.isArray()) {
            for (JsonNode item : node) {
                String text = item.asText().trim();
                if (!text.isEmpty()) items.add(text);
            }
        } else if (value != null) {
            for (String item : String.valueOf(value).split(",")) {
                if (!item.trim().isEmpty()) {
                    items.add(item.trim());
                }
            }
        }
        return items;
    }
}
