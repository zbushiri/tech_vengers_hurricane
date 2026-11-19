package controllers;

import java.util.Comparator;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import model.ReliefRequest;
import model.RequestType;
import model.User;

/**
 * Resident form for a new relief request, also used to edit an open request.
 * @author nate sheffield
 */
public class RequestFormController extends PageController {
    private ComboBox<RequestType> typeBox;
    private TextField pickupField, peopleField, petsField, medicalField, phoneField;
    private TextArea happenedArea;
    private VBox errorBanner;
    private Label errorBody, pickupError;
    private ReliefRequest editing;

    /**
     * Creates the controller; JavaFX calls this when loading the FXML.
     */
    public RequestFormController() { }

    /**
     * Builds the request form, filled in when editing.
     */
    @Override
    protected void build() {
        editing = Pages.editing ? Pages.selectedRequest : null;
        titleLabel.setText(editing == null ? "New request" : "Edit request");
        User user = Pages.user();

        typeBox = new ComboBox<>(FXCollections.observableArrayList(Pages.formTypes()));
        typeBox.setConverter(new StringConverter<>() {
            @Override public String toString(RequestType type) { return Pages.typeName(type); }
            @Override public RequestType fromString(String text) { return null; }
        });
        typeBox.setValue(RequestType.TRANSPORTATION);
        typeBox.setMaxWidth(Double.MAX_VALUE);
        pickupField = input("Enter a street address");
        happenedArea = new TextArea();
        happenedArea.setPrefRowCount(3);
        happenedArea.setWrapText(true);
        happenedArea.getStyleClass().add("pg-input");
        peopleField = input("1");
        petsField = input("None");
        medicalField = input("None");
        phoneField = input("(803) 555-0100");

        if (editing != null) {
            if (!Pages.formTypes().contains(editing.getType())) typeBox.getItems().add(editing.getType());
            typeBox.setValue(editing.getType());
            pickupField.setText(editing.getLocation());
            happenedArea.setText(Pages.story(editing));
            peopleField.setText(String.valueOf(editing.getNumberOfPeopleNeeded()));
            petsField.setText(editing.getAnimalNotes() == null ? "" : editing.getAnimalNotes());
            medicalField.setText(editing.getMedicalNeeds());
            String phone = Pages.phone(editing);
            phoneField.setText(phone.equals("Not provided") ? "" : phone);
        } else if (user != null) {
            pickupField.setText(user.getAddress());
            peopleField.setText("1");
            phoneField.setText(user.getPhoneNumber());
        }

        errorBody = Pages.label("", "pg-error-body");
        errorBanner = new VBox(3, Pages.label("Check the highlighted field", "pg-error-title"), errorBody);
        errorBanner.getStyleClass().add("pg-error-banner");
        show(errorBanner, false);
        pickupError = Pages.label("Enter your pickup location.", "pg-error-text");
        show(pickupError, false);

        Button cancel = new Button("Cancel");
        cancel.getStyleClass().add("rh-btn-secondary");
        cancel.setOnAction(e -> {
            Pages.editing = false;
            Pages.go(editing == null ? "myrequests" : "requestdetails");
        });
        Button submit = new Button(editing == null ? "Submit request" : "Save changes");
        submit.getStyleClass().add("rh-btn-primary");
        submit.setDefaultButton(true);
        submit.setOnAction(e -> submit());
        HBox actions = new HBox(12, cancel, submit);
        actions.setAlignment(Pos.CENTER_RIGHT);

        VBox card = Pages.card(errorBanner,
                field("Request type *", typeBox),
                field("Pickup location *", pickupField, pickupError),
                field("What happened? *", happenedArea),
                row(field("People *", peopleField), field("Pets", petsField)),
                row(field("Medical needs", medicalField), field("Phone number *", phoneField)),
                actions);
        card.setSpacing(14);
        card.setMaxWidth(620);
        body.getChildren().add(card);
    }

    /**
     * Checks the form, then submits or saves the request.
     */
    private void submit() {
        pickupField.getStyleClass().remove("pg-input-error");
        show(pickupError, false);
        String problem = null;
        if (pickupField.getText().isBlank()) {
            problem = "Enter your pickup location before submitting.";
            pickupField.getStyleClass().add("pg-input-error");
            show(pickupError, true);
        } else if (happenedArea.getText().isBlank()) {
            problem = "Tell us what happened before submitting.";
        } else if (count(peopleField.getText()) < 1) {
            problem = "Enter how many people need help.";
        } else if (phoneField.getText().isBlank()) {
            problem = "Enter a phone number before submitting.";
        }
        if (problem != null) {
            showError(problem);
            return;
        }

        String description = happenedArea.getText().trim() + "\nPhone: " + phoneField.getText().trim();
        int people = count(peopleField.getText());
        ReliefRequest request = editing;
        if (request == null) {
            if (!Pages.app().submitReliefRequest(typeBox.getValue(), description, pickupField.getText().trim(), people)) {
                showError("Your request could not be submitted. Check the details and try again.");
                return;
            }
            request = Pages.app().getMyRequests().stream()
                    .max(Comparator.comparing(ReliefRequest::getDateSubmitted,
                            Comparator.nullsFirst(Comparator.naturalOrder())))
                    .orElse(null);
            if (request == null) {
                showError("Your request could not be submitted. Check the details and try again.");
                return;
            }
        } else {
            request.setType(typeBox.getValue());
            request.setLocation(pickupField.getText().trim());
            request.setDescription(description);
            request.setNumberOfPeopleNeeded(people);
        }
        String pets = petsField.getText().trim();
        request.setAnimalNotes(pets);
        request.setNumberOfAnimals(petCount(pets));
        String medical = medicalField.getText().trim();
        request.setMedicalNeeds(medical.equalsIgnoreCase("none") ? "" : medical);
        Pages.save();

        Pages.selectedRequest = request;
        boolean wasEditing = editing != null;
        Pages.editing = false;
        Pages.go(wasEditing ? "requestdetails" : "requestsubmitted");
    }

    /**
     * Shows the error banner.
     * @param message what to fix
     */
    private void showError(String message) {
        errorBody.setText(message);
        show(errorBanner, true);
    }

    /**
     * Makes a text field.
     * @param prompt placeholder text
     * @return the field
     */
    private static TextField input(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.getStyleClass().add("pg-input");
        return field;
    }

    /**
     * Puts a bold label above a control.
     * @param text label text
     * @param nodes the control and any error text
     * @return the field box
     */
    private static VBox field(String text, Node... nodes) {
        VBox box = new VBox(6, Pages.label(text, "pg-field-label"));
        box.getChildren().addAll(nodes);
        for (Node node : nodes) {
            if (node instanceof Control control) control.setMaxWidth(Double.MAX_VALUE);
        }
        return box;
    }

    /**
     * Puts two fields side by side.
     * @param left left field
     * @param right right field
     * @return the row
     */
    private static HBox row(VBox left, VBox right) {
        HBox.setHgrow(left, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);
        left.setMaxWidth(Double.MAX_VALUE);
        right.setMaxWidth(Double.MAX_VALUE);
        return new HBox(18, left, right);
    }

    /**
     * Shows or hides a node and its space.
     * @param node the node
     * @param visible true to show
     */
    private static void show(Node node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }

    /**
     * Reads a whole number.
     * @param text the text
     * @return the number, or 0
     */
    private static int count(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Counts pets from text like "1 dog" or "None".
     * @param text the pets text
     * @return the number of pets
     */
    private static int petCount(String text) {
        if (text.isBlank() || text.equalsIgnoreCase("none") || text.equals("0")) return 0;
        String digits = text.replaceAll("^\\D*(\\d+).*$", "$1");
        return digits.matches("\\d+") ? Integer.parseInt(digits) : 1;
    }
}
