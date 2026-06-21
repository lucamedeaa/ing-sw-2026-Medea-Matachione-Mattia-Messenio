# Mesos

Final Project of Software Engineering at Politecnico di Milano, A.Y. 2025–2026.
Professor: Pierluigi San Pietro.

Mesos is a distributed Java implementation of the board game *Mesos*, developed with a client-server architecture and support for both textual and graphical user interfaces.

---

# Team Members

* Luca Medea
* Riccardo Mattia
* Luca Pietro Messenio
* Edoardo Matachione

---

# Implemented Requirements

The project implements:

* Complete game rules
* TUI
* JavaFX GUI
* Socket communication
* RMI communication
* Multiple concurrent matches
* Database leaderboard

---

# Advanced Features

| Feature                      | Status          |
| ---------------------------- | --------------- |
| Database leaderboard         | Implemented     |
| Multiple concurrent matches  | Implemented     |
| Persistence                  | Not implemented |
| Resilience to disconnections | Not implemented |

# Database

The PostgreSQL database is mandatory for server startup.

The server initializes the leaderboard service during boot and will not start if the database is unavailable.

The database is exclusively used for leaderboard management. Active matches, lobbies, and runtime game states are maintained in server memory.

For each completed match, the server stores:

* player nickname;
* final score;
* match date;
* number of players.

PostgreSQL is provided through Docker to ensure a reproducible execution environment.

## Database Setup

Make sure Docker is active:

* **Windows/macOS**: start Docker Desktop;
* **Linux**: ensure the Docker daemon is running.

Move to the project root directory:

```bash
cd path/to/mesos
```

Start the database:

```bash
docker compose up -d
```

Useful commands:

Stop the database:

```bash
docker compose down
```

Reset the database and remove stored data:

```bash
docker compose down -v
```

View container logs:

```bash
docker compose logs -f
```

If startup fails, ensure that port `5432` is not already used by another local PostgreSQL instance.

---

# Software Requirements

* Operating System: Windows, macOS, or Linux
* Java Development Kit: JDK 23
* Apache Maven
* Docker (required for PostgreSQL leaderboard support)

---

# Compilation

The project uses Maven for dependency management and build automation.

From the root directory of the project, run:

```bash
mvn clean package
```

Compiled JAR files will be generated inside the `target/` directory.

---

# Running the Project

Before starting the server, the PostgreSQL container must be running:

```bash
docker compose up -d
```

## From IDE (IntelliJ IDEA)

### Server

Run `ServerMain`.

### Client

Run `ClientMain`.

At startup, the client allows the player to:

* choose between TUI and GUI;
* insert the server IP address;
* configure Socket and RMI ports.

---

## From JAR

### Server

```bash
java -jar deliverables/final/jar/mesos-server.jar
```

Default ports:

* Socket: `1234`
* RMI Registry: `1099`

### Client

```bash
java -jar deliverables/final/jar/mesos-client.jar
```

At startup, the client allows the player to choose:

* TUI or GUI;
* Socket or RMI;
* server IP address;
* server port.

---

# Coverage

Coverage is primarily concentrated on the server-side model, where the core gameplay logic is implemented.

| Area            | Class | Method | Line | Branch |
| --------------- | ----- | ------ | ---- | ------ |
| Overall Project | 56%   | 43%    | 43%  | 32%    |
| Server          | 91%   | 77%    | 82%  | 79%    |
| Server Model    | 96%   | 88%    | 94%  | 88%    |
| Client Model    | 100%  | 48%    | 58%  | 43%    |

---

# Notes on Assets

*Mesos* is a board game developed and published by Cranio Creations Srl.

The graphical contents of this project that refer to the original board game product are used with the approval of Cranio Creations Srl exclusively for educational purposes.

Distribution, copying, reproduction, redistribution, publication, or commercial use of these contents and images outside the scope of this project is prohibited.
