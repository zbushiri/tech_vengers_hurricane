# GitHub Projects Product Backlog

The live board is [tech_vengers_hurricane SCRUM Board](https://github.com/users/zbushiri/projects/2). All cards below were created in **Product Backlog** first. The workflow stages are **Product Backlog**, **Sprint Backlog**, **In Progress**, **Review**, and **Done**.

`PB` simply means **Product Backlog**. The number gives each task a short ID that matches its board card.

## Simple board rules

1. All new tasks start in Product Backlog.
2. Sprint-planning tasks move to Sprint Backlog and get one owner.
3. Active work moves to In Progress.
4. Work ready for a teammate check moves to Review.
5. Work moves to Done only after its acceptance check passes.

Add a board-description table mapping each real name to the correct GitHub username. Add the instructor, TAs, and ISAs with the required access; do not guess their usernames.

| ID | Product backlog task | Acceptance check |
|---|---|---|
| PB-01 | Create and attach the GitHub Scrum Project | Project is linked to the team repository and contains the five columns. |
| PB-02 | Add team, instructor, TA, and ISA access | Every required person can open the repository/project and real names map to usernames. |
| PB-03 | Confirm Maven, JavaFX, and Java versions | A clean checkout compiles on team machines. |
| PB-04 | Add and verify JSON Simple setup | Dependency resolves and `requires json.simple` compiles. |
| PB-05 | Review/create User class and method stubs | Fields and signatures match the JSON/UML; unfinished behavior has short TODO comments. |
| PB-06 | Review/create Volunteer class and method stubs | Skills, equipment, availability, transport, and check methods are represented. |
| PB-07 | Review/create Shelter class and method stubs | Capacity, occupancy, status, accommodations, and operator methods are represented. |
| PB-08 | Review/create ReliefRequest class and method stubs | Submission, status, priority, people, animals, duplicate, and claim methods are represented. |
| PB-09 | Review/create HurricaneEvent class and method stubs | Dates, status, and affected-area methods are represented. |
| PB-10 | Complete User and Volunteer classes | Validation and agreed account/volunteer rules work. |
| PB-11 | Complete Shelter class | Capacity, occupancy, status, and accommodation rules work. |
| PB-12 | Complete ReliefRequest class | Priority, duplicate detection, valid status changes, and assignment work. |
| PB-13 | Complete HurricaneEvent class | Date, area, and status behavior works. |
| PB-14 | Complete JSON load for all model types | All five original datasets load without losing fields. |
| PB-15 | Complete JSON add for multiple model types | Unique records can be added and saved. |
| PB-16 | Complete JSON edit for multiple model types | Matching records are updated by ID. |
| PB-17 | Complete JSON delete for multiple model types | Matching records are removed without damaging other records. |
| PB-18 | Complete the System/Facade | Controllers can use one class for all main backend operations. |
| PB-19 | Complete resident submit-request workflow | Request is validated, prioritized, duplicate-checked, and saved. |
| PB-20 | Complete volunteer claim-request workflow | Eligible volunteer can claim an open request and changes persist. |
| PB-21 | Complete Driver demonstration | Driver demonstrates load/add/edit/delete and important workflows using copied test data. |
| PB-22 | Add backend tests | Models, JSON operations, and Facade workflows have repeatable passing checks. |
| PB-23 | Integrate JavaFX controllers in a later GUI sprint | Controllers call the Facade and never access JSON directly. |
| PB-24 | Update README and UML documentation | Setup, architecture, class diagram, and submit/claim sequences match the implementation. |
| PB-25 | Prepare final integration and demonstration | Clean checkout compiles and required scenarios can be demonstrated. |
| PB-26 | Conduct sprint planning and assign task owners | The team selects sprint work, assigns one owner per task, and agrees on each acceptance check. |

## Suggested first sprint

- PB-01 through PB-04
- PB-05 through PB-09
- PB-14 through PB-18
- PB-21

Move only the tasks the team actually commits to completing during that sprint.
