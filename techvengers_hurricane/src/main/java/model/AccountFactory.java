package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Builds the right kind of account for a role. Volunteers get a
 * VolunteerProfile, and every other role gets a plain User.
 *
 * Keys read from details: username, password, firstName, lastName,
 * phoneNumber, email, address. Volunteers may also give skills and
 * equipment (comma separated text or a List) and transportationAccess
 * ("true" or "false").
 */
public class AccountFactory {
    private AccountFactory() {
    }

    /**
     * @return the new account, or null if the role, username or password is missing
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
        user.setAddress(new location.Address(text(details, "address"),
                text(details, "city"), text(details, "state"), text(details, "zip")));
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
        if (value instanceof List) {
            for (Object item : (List<?>) value) {
                items.add(String.valueOf(item).trim());
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
