# How to Finish the Semester Project

This walkthrough follows the supplied **CSCE 247: Setting Up Your Semester Project** video and the pasted Git Project Board/Product Backlog rubric.

## What the video requires now

The instructor says this milestone is about a strong backend, not graphics. The video demonstrates this order:

1. Use one meaningful GitHub repository for the whole semester.
2. Create the project with JavaFX and Maven so dependencies are manageable.
3. Match the Java version in `pom.xml` to the team's installed Java version.
4. Add a JSON library as a Maven dependency. The video demonstrates JSON Simple; this repository uses Gson for the same job.
5. Add the matching module requirement. This project correctly uses `requires com.google.gson;`.
6. Keep backend work in a separate `model` package.
7. Export packages that another module or the later GUI will use.
8. Run a small Driver/Hello World check before building more features.
9. Finish and test the backend before connecting the GUI in a later sprint.
10. Push the project to GitHub, enable Insights with a student account, add the TAs/ISAs as collaborators, create the Scrum project, and maintain a useful README.

The team integrated the backend foundation directly into the existing `hurricanejavafx` Maven project. This keeps one simple application structure and preserves the original FXML screens and JSON datasets.

## Foundation map

```text
hurricanejavafx/
  pom.xml                         Maven, JavaFX, Gson, and Driver setup
  src/main/java/module-info.java  Module requirements and exports
  src/main/java/com/techvengershurricane/
    Driver.java                   Backend runner/check
    model/                        Class foundations
    data/JsonDataAccess.java      JSON load/add/edit/delete
    system/HurricaneReliefSystem.java  System/Facade
```

The connection is:

```text
Driver now / JavaFX controller later
              -> HurricaneReliefSystem
                  -> JsonDataAccess
                      -> json/*.json
```

## Run the backend check

From `hurricanejavafx`:

```bash
mvn compile
mvn exec:java
```

Expected sample counts are 2 users, 2 volunteers, 2 shelters, 2 requests, and 3 hurricanes.

## Finish each model

Every model contains short `/* TODO ... */` comments showing the next work location.

### User

- Add validation for required account fields.
- Add login/account methods only after deciding how passwords will be hashed.
- Complete safety-status and associated-person behavior.
- Do not store real passwords or private information in sample JSON.

### Volunteer

- Add methods for skills, equipment, transportation, and availability.
- Validate background-check status before a request can be claimed.
- Decide which request types match which skills.

### Shelter

- Reject occupancy below zero or above capacity.
- Add open/full/closed status rules.
- Add accommodation and operator-management methods.

### ReliefRequest

- Validate required type, description, and location.
- Calculate priority from the team's agreed rules.
- Find likely duplicates by location and request details.
- Restrict status changes to valid transitions.

### HurricaneEvent

- Validate start/end dates and event status.
- Add affected-area methods.
- Decide how closed events affect new requests.

## Finish JSON access

`JsonDataAccess` already provides working load, add, edit, and delete foundations for every model type.

1. Copy the JSON folder before testing writes.
2. Pass the copied folder to `Driver` or `HurricaneReliefSystem`.
3. Test add with a new, unique ID.
4. Test edit using that same ID.
5. Test delete and confirm only that record is removed.
6. Add clear error messages for missing, malformed, or unwritable files.
7. Never test delete operations against the team's only copy of sample data.

## Finish the Facade

Put complete user actions in `HurricaneReliefSystem`, not in controllers or the JSON class.

- `submitRequest`: validate, check duplicates, calculate priority, then save.
- `claimRequest`: verify the volunteer, assign the request, then save.
- Add shelter search/filter methods.
- Add safety-status update methods.
- Add coordinator review methods for suspicious requests.

When JavaFX work begins, controllers should call the Facade and should never open JSON files directly.

## Finish the Driver

Keep the Driver simple and readable. It should demonstrate:

1. Loading all five datasets.
2. Adding one test record.
3. Editing that record.
4. Deleting that record.
5. Submitting a request.
6. Claiming a request.

Use copied test JSON files so the original data remains unchanged.

## GitHub and Scrum checklist

- Create the GitHub Project and attach it to the repository.
- Add instructor/TA/ISA access before the deadline.
- Put real names beside each GitHub username in the board description.
- Create every task in `PROJECT_BOARD_BACKLOG.md` in **Product Backlog** first.
- Pull only selected tasks into the current Sprint Backlog.
- Assign one owner and acceptance check to each sprint task.
- Use pull requests so another teammate reviews shared code.
- Keep the README and diagrams synchronized with the implementation.

## Definition of done

A task is done when it compiles, uses the correct package, preserves the JSON structure, has been checked by another teammate, and its acceptance check passes.
