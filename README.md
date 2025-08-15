# 🕵️ Scotland Yard — Java Game Engine & UI

A complete **Java implementation** of the Scotland Yard board game rules with a JavaFX UI and pluggable AI players.  
Implements the **full** game mechanics — including single, double, and secret moves — with variable ticket counts, configurable game length, and ferry routes, all backed by an immutable rules engine.

---

## Features

- **Full Scotland Yard Ruleset** — Single, double, and secret moves (including ferry routes)
- **Configurable Game Length** — Travel log size determines when the game ends
- **Custom Ticket Counts** — Set starting tickets for Mr X and detectives
- **Win Condition Logic** — Stalemates, captures, and turn-based victory checks
- **Immutable Game State** — Board snapshots and travel logs for safe state tracking
- **AI Plugin API** — Implement `Ai` and auto-load via classpath scanning
- **Graph-Based Map** — Loads from text files with node coordinates and transports
- **JavaFX UI** — Interactive map and ticket-driven move selection

---

## Technologies Used

- **Language:** Java 11+  
- **Libraries:** JavaFX, Guava, FastClasspathScanner, Atlassian Fugue  
- **Architecture:** Immutable game state, observer pattern, plugin-based AI loading

---

## Rules & Clarifications (Must-Read)

This project implements the **full** version of Scotland Yard with the following adjustments and clarifications:

- **Police/Bobbies** are **not** modeled.
- **Ferry** routes are modeled and require a **SECRET** ticket.
- **Variable starting tickets** for Mr X and detectives (user-specified). Standard ticket rules apply:
  - When a **detective moves**, the **ticket used is given to Mr X**.
  - **Mr X’s used tickets are discarded** (not transferred).
- **Variable game length**:
  - The physical board uses “rounds,” which differ from **Mr X’s Travel Log slots** (double moves occupy two slots).
  - For practicality, the game length here is **the number of Travel Log slots**; the game **ends when the log is full**.
- **Mr X cannot move into a detective’s location.**
- **Loss conditions**:
  - If it’s **Mr X’s turn** and Mr X has **no legal move**, **Mr X loses**.
  - If it’s the **detectives’ turn** and **none** can move, **detectives lose** (movable detectives play; immobile ones are skipped).
- `Ticket.SECRET` represents the **black** ticket for **secret moves** (including ferry).
- **Double moves & secret moves** have nuanced rules—this engine handles the complexities.

---

## Controls

| Action | Description |
|--------|-------------|
| Select a ticket | Choose the transport/secret for the move |
| Click a destination | Move to an adjacent valid node using the chosen ticket |
| Use **DOUBLE** (Mr X) | Take two consecutive moves (consumes `DOUBLE` plus both transport tickets) |
| **SECRET** (Mr X) | Hide transport type and optionally use ferry |

> Exact UI interactions depend on the JavaFX controller, but the flow is ticket → destination.

---

## Installation & Build

### Requirements
- **Java 11+**
- **JavaFX SDK** (if not bundled with your JDK)
- **Maven** (used to run the project)

### Build & Run (Maven Only)
```bash
# Clean, compile, and run the JavaFX app
mvn clean javafx:run

# Run tests
mvn test
