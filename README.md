# Camunda Spring Boot Integration Project

This project demonstrates a **Camunda 7 process** integrated with an **external system** (stub REST API).
It supports:

* Receiving a client request via REST API
* Checking client status in an external system
* Conditional flow depending on external status
* Logging all actions to a database

---

## 🏗️ Technology Stack

* Java 17, Spring Boot 3
* Camunda BPM 7
* H2 / PostgreSQL database
* Spring Data JPA
* WebClient (Spring WebFlux) for external calls
* Docker & Docker Compose

---

## 📂 Project Structure

```
src/
 ├─ main/
 │   ├─ java/uz/sap/task/
 │   │   ├─ delegate/CheckExternalDelegate.java
 │   │   ├─ model/AuditLog.java
 │   │   └─ repository/AuditLogRepository.java
 │   ├─ resources/
 │   │   ├─ application.yml
 │   │   └─ processes/process.bpmn
```

---

## ⚙️ Setup & Run

### 1. Build the project

```bash
mvn clean package
```

---

### 2. Start with Docker Compose

```bash
docker-compose up --build
```

**Docker services:**

* `app` – Spring Boot + Camunda app (port 8080)
* `db` – PostgreSQL database (port 5432)
* `external` – Mock external service (port 9000)

---

### 3. Start the process via REST

```bash
POST http://localhost:8080/engine-rest/process-definition/key/checkClientProcess/start
Content-Type: application/json

{
  "variables": {
    "clientId": { "value": "12345", "type": "String" }
  }
}
```

---

### 4. Access Camunda Web Apps

* **Tasklist:** [http://localhost:8080/camunda/app/tasklist](http://localhost:8080/camunda/app/tasklist)
* **Cockpit:** [http://localhost:8080/camunda/app/cockpit](http://localhost:8080/camunda/app/cockpit)
* **Admin:** [http://localhost:8080/camunda/app/admin](http://localhost:8080/camunda/app/admin)

Default credentials:

```
Username: demo
Password: demo
```

---

## 📄 BPMN Process

**Process ID:** `checkClientProcess`

**Flow:**

1. Start Event
2. Service Task `Check External` → `CheckExternalDelegate`
3. Exclusive Gateway:

    * `externalStatus == "SUCCESS"` → End Success
    * `externalStatus != "SUCCESS"` → End Error
4. End Event

**Note:** Service Task can be marked `asyncBefore="true"` if you want to create a **Job Definition**.

---

## 🗂️ Audit Logging

All external checks are saved to DB via `AuditLogRepository`.
Table schema (PostgreSQL/H2):

| Column            | Type      |
| ----------------- | --------- |
| id                | Long PK   |
| processInstanceId | String    |
| action            | String    |
| payload           | Text      |
| createdAt         | Timestamp |

---

## ⚡ Notes & Troubleshooting

* **Cannot instantiate delegate:** Ensure BPMN uses `camunda:delegateExpression="${checkExternalDelegate}"` and the class is annotated `@Component("checkExternalDelegate")`.
* **WebClient errors:** Add dependency `spring-boot-starter-webflux` in `pom.xml`.
* **TTL warning:** Add `camunda:historyTimeToLive="30"` to process to avoid `ENGINE-12018`.
* **Job definition warning:** Optional; mark Service Task as `asyncBefore="true"` to create jobs.

---

## 📦 Dependencies

* `spring-boot-starter-web`
* `spring-boot-starter-webflux`
* `spring-boot-starter-data-jpa`
* `camunda-bpm-spring-boot-starter`
* `camunda-bpm-spring-boot-starter-rest`
* `h2` / `postgresql`

---

## 🏁 Summary

This project demonstrates:

* BPMN process orchestration with Camunda
* External REST integration via WebClient
* Conditional flows and error handling
* Audit logging into a database
* Dockerized deployment for quick startup
