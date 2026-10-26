<h1 align="center">Gamecock Relief Network</h1>

<p align="center">
  <b>Team Tech-vengers</b> · CSCE 247 Software Engineering · University of South Carolina
</p>

Gamecock Relief Network is a Java disaster-relief system for residents, volunteers, shelter operators, and coordinators. The current project includes JavaFX screens, model classes, JSON data access, sample data, and the original requirements and UML documents.

## Build and Run

Use Java 25 and Maven:

```bash
mvn test
mvn compile exec:java
mvn javadoc:javadoc
```

### Map API Setup

Each teammate needs a local `.env` file in the main project folder:

```text
HURRICANE_API=your_api_key
```

Use `.env.example` as the template. The real `.env` file is ignored by Git.
Then select `MapUI` in VS Code and press Run. Select `UI` for the interactive terminal app.
The map accepts `--from="25 Calhoun Street, Charleston, SC 29401"` as a Java launch argument.
For terminal launches outside VS Code, export `HURRICANE_API` in your shell; Maven does not load `.env` automatically.

### Hurricane Scarlett walkthrough

With the API key configured, select **Scenario** in VS Code. It copies the original sample JSON
into `demo-data/`, creates the Carter household, displays seven explained recommendations,
selects a shelter by driving distance, exports both trip legs, denies Rosa completion,
and lets Felix check in the household and complete the request.

Select **Verify saved scenario** in VS Code to restart and reload the saved account, assignment,
household, pet, check-in and completed status. JSON snapshots for `before`, `submitted`,
`assigned`, `checked-in`, and `after` are under `demo-data/snapshots/`. Trip sheets are under
`demo-data/trip-sheets/`. These generated files are ignored by Git.

Equivalent shell commands (run at the repository root with `HURRICANE_API` exported):

```bash
mvn compile exec:java -Dapp.mainClass=model.ScenarioUI
mvn exec:java -Dapp.mainClass=model.ScenarioUI -Dexec.args="--verify"
```

An existing Carter demonstration is never silently overwritten. To rehearse again, pass a fresh
folder such as `--data-dir=demo-data/take-2` in the Scenario launch arguments, then use the same
folder with `--verify`. The normal terminal UI saves to `json/`; the walkthrough uses its isolated copy.

### Interactive workflow

- Residents submit named household members (including themselves), ages, pets and medical needs.
- Rosa logs in, opens **View open requests**, reviews recommendation reasons, and claims a request.
- Under **View my assignments**, she selects the nearest suitable shelter and exports a trip sheet.
- Volunteers can start work but cannot mark requests complete.
- Felix uses **Manage shelter** to inspect occupants, check in an arriving household, then complete it.
- Check-in rechecks human/pet capacity and rejects duplicate arrivals. Human occupancy excludes pets.

Existing sample shelters have aggregate human occupancy without historical names. The occupant list
shows newly recorded admissions and identifies the unnamed legacy count. Fictional pet capacities
are explicit in `json/shelters.json`; old files without a pet capacity require an operator to configure
one before accepting animals.

### Verification and design

`mvn test` runs deterministic tests with explicitly fictional test routes, including a full workflow
and reload in a fresh JVM. Live routes require the API key and are not replaced by fabricated
nearest-shelter results when the service is unavailable. The nearby recommendation threshold is
25 driving miles; specialty matching uses the volunteer's listed request-type skill.

`mvn javadoc:javadoc` generates the API reference under `target/reports/apidocs/` with strict
Javadoc validation. The build uses Java 25. `src/` and `pom.xml` are at the repository root.

`HurricaneReliefApp` is the facade for UI actions. `AccountFactory` selects the account type;
the existing singleton catalogs own loaded data. `RoutingService` supplies driving routes and
allows deterministic tests. Requests carry household/pet details and a destination; shelters
record `ShelterAdmission` objects linked by request ID. JSON saves preserve these relationships.
Update the class and sequence diagrams in `docs/` to reflect these additions before submission.

## Project Documents

📄 [Requirements Document](docs/requirements.pdf)

📊 [Requirements Spreadsheet](https://docs.google.com/spreadsheets/d/1-6v7CzIGUt1K67bx0IGyv2CTm_Y1jSjVxjm9yyDrl70/edit?usp=sharing)

📐 [UML Class Diagram](docs/uml-class-diagram.pdf)

🔁 [UML Sequence Diagram 1 – Resident Submits a Relief Request](docs/sequence1.pdf)

🔁 [UML Sequence Diagram 2 – Volunteer Claims a Relief Request](docs/sequence2.pdf)

## Presentation

🎥 [YouTube Presentation](https://youtu.be/24qM7z8QZqo)

## Project Board

📌 [Project Board](https://github.com/users/zbushiri/projects/2/views/1)

`PB` means **Product Backlog**.

## Our Team

| Team Member | Role | GitHub |
|-------------|------|--------|
| Tavien Smith | Documentation & Repo Lead | [@CSE-TavienSmith](https://github.com/CSE-TavienSmith) |
| Zaki Bushiri | Dev Lead | [@zbushiri](https://github.com/zbushiri) |
| Nate Sheffield | Head Designer | [@nathanielsheffield775-jpg](https://github.com/nathanielsheffield775-jpg) |
| Robert Albetel | Backend & Data Developer | [@algalda](https://github.com/algalda) |
| Ryan Newhouse | QA & Testing Lead | [@ryannewhouse11](https://github.com/ryannewhouse11) |
