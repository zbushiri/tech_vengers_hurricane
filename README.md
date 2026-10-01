<h1 align="center">Gamecock Relief Network</h1>

<p align="center">
  <b>Team Tech-vengers</b> · CSCE 247 Software Engineering · University of South Carolina
</p>

When a hurricane hits, help is everywhere, but it's scattered across group chats, phone calls, social media posts, and word of mouth. Residents don't know which shelters are open, volunteers don't know who needs them, and coordinators are left piecing it all together.

Gamecock Relief Network exists to fix that. It is a Java-based disaster-relief coordination system that gives residents, volunteers, shelter operators, and coordinators **one organized place** to request help, locate resources, share safety information, and support their community during an emergency.

Think of it as a **command center for your community**. No more guessing where to go or who to call, just one place to find shelter, ask for help, and show up for each other.

## Current Foundation

This milestone is a backend-first semester-project foundation. The repository currently includes:

- Java 17, JavaFX 21, and Maven setup
- Five model foundations: `User`, `Volunteer`, `Shelter`, `ReliefRequest`, and `HurricaneEvent`
- Gson JSON loading plus add, edit, and delete operations
- A `HurricaneReliefSystem` Facade for controllers to use
- A backend `Driver` that checks all five sample datasets without opening the GUI
- Short `/* ... */` comments showing where teammates should finish business rules
- The original FXML screens, JSON datasets, requirements, and UML diagrams

The screens are still starter screens. Model validation, request-priority rules, duplicate detection, volunteer eligibility, automated tests, and controller integration remain planned backlog work.

## Project Structure

```text
hurricanejavafx/   JavaFX application and integrated backend foundation
  src/main/java/com/techvengershurricane/
    Driver.java
    data/JsonDataAccess.java
    model/
    system/HurricaneReliefSystem.java
json/              Original sample datasets
docs/              Requirements, UML, walkthrough, and backlog
```

The components connect like this:

```text
Driver now / JavaFX controllers later
              -> HurricaneReliefSystem
                  -> JsonDataAccess
                      -> json/*.json
```

## Build and Run

Install Java 17 and Maven, then run:

```bash
cd hurricanejavafx
mvn compile
mvn exec:java
```

The Driver is read-only. With the included files it reports 2 users, 2 volunteers, 2 shelters, 2 requests, and 3 hurricanes. Use copies of the JSON files when demonstrating add, edit, or delete operations.

## Requirements

📄 [Requirements Document](docs/requirements-document.pdf)

📊 [Requirements Spreadsheet](https://docs.google.com/spreadsheets/d/1-6v7CzIGUt1K67bx0IGyv2CTm_Y1jSjVxjm9yyDrl70/edit?usp=sharing)

## Code Design

📐 [UML Class Diagram](docs/uml-class-diagram.pdf)

🔁 [UML Sequence Diagram 1 – Resident Submits a Relief Request](docs/uml-sequence-diagram1.pdf)

🔁 [UML Sequence Diagram 2 – Volunteer Claims a Relief Request](docs/uml-sequence-diagram2.pdf)

## Project Board

📌 [Project Board](https://github.com/users/zbushiri/projects/2/views/1)

`PB` means **Product Backlog**. For example, PB-05 is Product Backlog task 5.

Simple workflow:

1. Every task starts in **Product Backlog**.
2. Selected sprint work moves to **Sprint Backlog** and gets one owner.
3. Started work moves to **In Progress**.
4. Work ready for a teammate check moves to **Review**.
5. Work moves to **Done** after its acceptance check passes.

See the [completion walkthrough](docs/HOW_TO_FINISH.md) and [full backlog with acceptance checks](docs/PROJECT_BOARD_BACKLOG.md).

## Our Team

| Team Member | Role | GitHub |
|-------------|------|--------|
| Tavien Smith | Documentation & Repo Lead | [@CSE-TavienSmith](https://github.com/CSE-TavienSmith) |
| Zaki Bushiri | Dev Lead | [@zbushiri](https://github.com/zbushiri) |
| Nate Sheffield | Head Designer | [@nathanielsheffield775-jpg](https://github.com/nathanielsheffield775-jpg) |
| Robert Albetel | Backend & Data Developer | [@algalda](https://github.com/algalda) |
| Ryan Newhouse | QA & Testing Lead | [@ryannewhouse11](https://github.com/ryannewhouse11) |
