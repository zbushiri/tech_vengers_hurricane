package controllers;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;
import location.Address;
import location.MapTools;
import model.HurricaneReliefApp;
import model.ReliefRequest;
import model.RequestStatus;
import model.RequestType;
import model.Shelter;
import model.ShelterList;
import model.ShelterStatus;
import model.User;
import model.VolunteerProfile;
import techvengers_hurricane.App;

/**
 * Shared navigation state and small UI helpers used by every page after login.
 * @author nate sheffield
 */
public final class Pages {
    /** FXML name of the page that is showing, used to highlight the sidebar. */
    static String current = "rolehome";
    /** Request picked on the previous page (details, edit, confirmation). */
    static ReliefRequest selectedRequest;
    /** Shelter picked on the Find shelters page. */
    static Shelter selectedShelter;
    /** True when the request form should edit {@link #selectedRequest} instead of creating one. */
    static boolean editing;

    private static final Map<UUID, Double> MILES = new ConcurrentHashMap<>();
    private static volatile boolean loadingMiles;

    private Pages() { }

    /**
     * Switches the window to another page.
     * @param page FXML name without the .fxml extension
     */
    static void go(String page) {
        current = page;
        try {
            App.setRoot(page);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Returns the shared application facade.
     * @return the facade
     */
    static HurricaneReliefApp app() { return App.getReliefApp(); }

    /**
     * Returns the logged-in user.
     * @return the user, or null
     */
    static User user() { return app().getCurrentUser(); }

    /**
     * Checks whether the logged-in user is a volunteer.
     * @return true for volunteers
     */
    static boolean isVolunteer() { return user() != null && user().getVolunteerProfile() != null; }

    /**
     * Saves all data, ignoring the result so a failed save does not stop the screen.
     */
    static void save() { app().saveData(); }

    /**
     * Makes a label with style classes.
     * @param text label text
     * @param styles CSS classes
     * @return the label
     */
    static Label label(String text, String... styles) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.getStyleClass().addAll(styles);
        return label;
    }

    /**
     * Makes a status pill.
     * @param text pill text
     * @param tone one of red, gold, green, alert, blue, grey
     * @return the pill
     */
    static Label pill(String text, String tone) {
        Label pill = new Label(text);
        pill.getStyleClass().addAll("rh-pill", "pg-pill-" + tone);
        pill.setMinWidth(Label.USE_PREF_SIZE);
        return pill;
    }

    /**
     * Stops buttons and pills from shrinking and cutting off their text.
     * @param root the part of the page to fix
     */
    static void fit(Node root) {
        for (Node node : root.lookupAll(".button")) {
            ((javafx.scene.control.Control) node).setMinWidth(javafx.scene.control.Control.USE_PREF_SIZE);
        }
        for (Node node : root.lookupAll(".toggle-button")) {
            ((javafx.scene.control.Control) node).setMinWidth(javafx.scene.control.Control.USE_PREF_SIZE);
        }
    }

    /**
     * Makes a white card.
     * @param children card contents
     * @return the card
     */
    static VBox card(Node... children) {
        VBox card = new VBox(8, children);
        card.getStyleClass().add("rh-card");
        return card;
    }

    /**
     * Short request code like GR-1A2B3C.
     * @param request the request
     * @return the code
     */
    static String code(ReliefRequest request) {
        return "GR-" + request.getRequestId().toString().substring(0, 6).toUpperCase();
    }

    /**
     * Friendly request type name used by the Figma.
     * @param type request type
     * @return display name
     */
    static String typeName(RequestType type) {
        if (type == null) return "Request";
        return switch (type) {
            case TRANSPORTATION -> "Transportation";
            case FOOD -> "Food and water";
            case SUPPLIES -> "Emergency supplies";
            case MEDICAL -> "Medical assistance";
            case SHELTER -> "Shelter";
            case HAZARD -> "Hazard";
            default -> "Other";
        };
    }

    /**
     * Request types offered on the request form, in Figma order.
     * @return the types
     */
    static List<RequestType> formTypes() {
        return List.of(RequestType.TRANSPORTATION, RequestType.FOOD, RequestType.SUPPLIES, RequestType.MEDICAL);
    }

    /**
     * Friendly status name.
     * @param status request status
     * @return display name
     */
    static String statusName(RequestStatus status) {
        if (status == null) return "Submitted";
        return switch (status) {
            case ASSIGNED -> "Assigned";
            case IN_PROGRESS -> "In progress";
            case FULFILLED, COMPLETE -> "Complete";
            case CANCELLED -> "Cancelled";
            default -> "Submitted";
        };
    }

    /**
     * Pill tone for a status.
     * @param status request status
     * @return tone name
     */
    static String statusTone(RequestStatus status) {
        if (status == null) return "gold";
        return switch (status) {
            case ASSIGNED, IN_PROGRESS -> "blue";
            case FULFILLED, COMPLETE -> "green";
            case CANCELLED -> "grey";
            default -> "gold";
        };
    }

    /**
     * First name of the assigned volunteer.
     * @param request the request
     * @return the name, or "Pending"
     */
    static String helper(ReliefRequest request) {
        VolunteerProfile volunteer = request.getAssignedVolunteer();
        return volunteer == null ? "Pending" : volunteer.getFirstName();
    }

    /**
     * Full name of the person who submitted a request.
     * @param request the request
     * @return the name, or "Resident"
     */
    static String requester(ReliefRequest request) {
        User owner = request.getSubmittedBy();
        return owner == null ? "Resident" : owner.getFirstName() + " " + owner.getLastName();
    }

    /**
     * Formats a date like "Nov 11, 3:42 PM".
     * @param date the date
     * @return the text, or "Recently"
     */
    static String when(Date date) {
        return date == null ? "Recently" : new SimpleDateFormat("MMM d, h:mm a").format(date);
    }

    /**
     * Household text like "4 people · 1 dog".
     * @param request the request
     * @return the text
     */
    static String household(ReliefRequest request) {
        int people = request.getNumberOfPeopleNeeded();
        String text = people + (people == 1 ? " person" : " people");
        String notes = request.getAnimalNotes();
        if (notes != null && !notes.isBlank()) return text + " · " + notes;
        int pets = request.getNumberOfAnimals();
        return pets == 0 ? text + " · No pets" : text + " · " + pets + (pets == 1 ? " pet" : " pets");
    }

    /**
     * Medical needs or "None reported".
     * @param request the request
     * @return the text
     */
    static String medical(ReliefRequest request) {
        String needs = request.getMedicalNeeds();
        return needs == null || needs.isBlank() ? "None reported" : needs;
    }

    /**
     * Phone number saved on the request as a "Phone:" detail line.
     * @param request the request
     * @return the phone, or "Not provided"
     */
    static String phone(ReliefRequest request) {
        for (String line : lines(request)) {
            if (line.startsWith("Phone: ")) return line.substring(7);
        }
        return "Not provided";
    }

    /**
     * The "What happened?" text without the saved phone line.
     * @param request the request
     * @return the text
     */
    static String story(ReliefRequest request) {
        List<String> kept = new ArrayList<>();
        for (String line : lines(request)) {
            if (!line.startsWith("Phone: ")) kept.add(line);
        }
        return String.join("\n", kept);
    }
    /**
     * Splits the description into lines.
     * @param request the request
     * @return the lines
     */
    private static List<String> lines(ReliefRequest request) {
        String text = request.getDescription();
        return text == null ? List.of() : List.of(text.split("\n"));
    }

    /**
     * All shelters loaded from the JSON data.
     * @return the shelters
     */
    static List<Shelter> shelters() { return ShelterList.getInstance().getShelters(); }

    /**
     * Finds a shelter by id.
     * @param id shelter id
     * @return the shelter, or null
     */
    static Shelter shelter(UUID id) {
        if (id == null) return null;
        for (Shelter shelter : shelters()) {
            if (id.equals(shelter.getShelterId())) return shelter;
        }
        return null;
    }

    /**
     * Checks whether a shelter is open.
     * @param shelter the shelter
     * @return true when open
     */
    static boolean isOpen(Shelter shelter) { return shelter.getStatus() == ShelterStatus.OPEN; }

    /**
     * Driving miles from the user's address, once loaded.
     * @param shelter the shelter
     * @return miles, or null when not known yet
     */
    static Double miles(Shelter shelter) { return MILES.get(shelter.getShelterId()); }

    /**
     * Distance text like "2.4 mi".
     * @param shelter the shelter
     * @return the text
     */
    static String distance(Shelter shelter) {
        Double miles = miles(shelter);
        return miles == null ? "Distance unavailable" : String.format("%.1f mi", miles);
    }

    /**
     * Looks up driving distances to every shelter in the background, using the map API.
     * @param done runs on the UI thread when finished
     */
    static void loadDistances(Runnable done) {
        if (!MILES.isEmpty() || loadingMiles || user() == null) return;
        loadingMiles = true;
        String from = user().getDepartureAddress();
        Thread worker = new Thread(() -> {
            Address start = Address.fromString(from);
            for (Shelter shelter : new ArrayList<>(shelters())) {
                try {
                    Address end = Address.fromString(shelter.getAddress());
                    if (start != null && end != null) {
                        double meters = MapTools.getInstance().route(start, end).distanceMeters();
                        MILES.put(shelter.getShelterId(), meters / ShelterList.METERS_PER_MILE);
                    }
                } catch (IOException | RuntimeException e) {
                    // Leave this shelter without a distance.
                }
            }
            loadingMiles = false;
            Platform.runLater(done);
        });
        worker.setDaemon(true);
        worker.start();
    }

    /**
     * Copies a Google Maps directions link for the shelter to the clipboard.
     * @param shelter the destination
     */
    static void copyDirections(Shelter shelter) {
        ClipboardContent content = new ClipboardContent();
        content.putString("https://www.google.com/maps/dir/?api=1&destination="
                + URLEncoder.encode(shelter.getAddress(), StandardCharsets.UTF_8));
        Clipboard.getSystemClipboard().setContent(content);
    }
}
