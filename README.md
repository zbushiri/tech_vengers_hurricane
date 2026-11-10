<h1 align="center">Gamecock Relief Network</h1>

<p align="center">
  <b>Team Tech-vengers</b> · CSCE 247 Software Engineering · University of South Carolina
</p>

Gamecock Relief Network is a Java disaster-relief application for residents,
volunteers, shelter operators, and coordinators.

## App Preview

View the planned login, resident dashboard, relief request, and request form
screens in the [Gamecock Relief Network App Preview](docs/app-preview.pdf).

## What It Does

- Creates and saves user accounts
- Lets residents submit relief requests
- Lets volunteers find and claim requests
- Tracks shelters, available space, and household check-ins
- Finds nearby shelters and displays driving directions
- Saves application data in JSON files

## What You Need

- Java 25
- Maven
- VS Code with the Java Extension Pack
- A free OpenRouteService API key for map features

## Get a Free API Key

1. Create an account at [OpenRouteService](https://openrouteservice.org/dev/#/signup).
2. Open the **Tokens** page and create a free token.
3. Clone this repository:

```bash
git clone https://github.com/zbushiri/tech_vengers_hurricane.git
cd tech_vengers_hurricane
```

4. Open the project folder in VS Code.
5. Create your environment file:

```bash
cp .env.example .env
```

6. Open `.env` and replace `your_api_key` with your token:

```text
HURRICANE_API=your_api_key
```

The `.env` file is ignored by Git. Each user should use their own free key, as
recommended by OpenRouteService.

## Run the Application

Compile the project:

```bash
mvn compile
```

Run the terminal application:

```bash
mvn compile exec:java
```

In VS Code, open **Run and Debug** and select:

- `UI` for the terminal application
- `MapUI` for the shelter map
- `Scenario` for the sample walkthrough
- `Verify saved scenario` to reload the walkthrough data

## Troubleshooting

- If the map says the API key is missing, make sure the file is named `.env`
  and contains `HURRICANE_API=your_api_key` with your real key.
- Run the map with the **MapUI** option in VS Code so the `.env` file is loaded.
- If Maven cannot find a dependency, run `mvn compile` while connected to the
  internet.

## Project Documents

📄 [Requirements Document](https://github.com/zbushiri/tech_vengers_hurricane/blob/main/docs/requirements.pdf)

📊 [Requirements Spreadsheet](https://docs.google.com/spreadsheets/d/1-6v7CzIGUt1K67bx0IGyv2CTm_Y1jSjVxjm9yyDrl70/edit?usp=sharing)

📐 [UML Class Diagram](https://github.com/zbushiri/tech_vengers_hurricane/blob/main/docs/uml-class-diagram.pdf)

🔁 [UML Sequence Diagram 1 – Resident Submits a Relief Request](https://github.com/zbushiri/tech_vengers_hurricane/blob/main/docs/sequence1.pdf)

🔁 [UML Sequence Diagram 2 – Volunteer Claims a Relief Request](https://github.com/zbushiri/tech_vengers_hurricane/blob/main/docs/sequence2.pdf)

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
