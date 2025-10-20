---

# Camunda Spring Boot Интеграционный Проект

Этот проект демонстрирует **процесс Camunda 7**, интегрированный с **внешней системой** (stub REST API).
Он поддерживает:

* Приём заявки через REST API
* Проверку статуса клиента во внешней системе
* Условное ветвление в зависимости от статуса
* Логирование всех действий в базу данных

---

## 🏗️ Технологический стек

* Java 17, Spring Boot 3
* Camunda BPM 7
* База данных H2 / PostgreSQL
* Spring Data JPA
* WebClient (Spring WebFlux) для внешних вызовов
* Docker & Docker Compose

---

## 📂 Структура проекта

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

## ⚙️ Установка и запуск

### 1. Сборка проекта

```bash
mvn clean package
```

---

### 2. Запуск через Docker Compose

```bash
docker-compose up --build
```

**Docker сервисы:**

* `app` – Spring Boot + Camunda (порт 8080)
* `db` – база данных PostgreSQL (порт 5432)
* `external` – mock внешний сервис (порт 9000)

---

### 3. Запуск процесса через REST

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

### 4. Доступ к Camunda Web Apps

* **Tasklist:** [http://localhost:8080/camunda/app/tasklist](http://localhost:8080/camunda/app/tasklist)
* **Cockpit:** [http://localhost:8080/camunda/app/cockpit](http://localhost:8080/camunda/app/cockpit)
* **Admin:** [http://localhost:8080/camunda/app/admin](http://localhost:8080/camunda/app/admin)

Стандартные учетные данные:

```
Имя пользователя: demo
Пароль: demo
```

---

## 📄 BPMN процесс

**ID процесса:** `checkClientProcess`

**Поток выполнения:**

1. Start Event
2. Service Task `Check External` → `CheckExternalDelegate`
3. Exclusive Gateway:

    * `externalStatus == "SUCCESS"` → End Success
    * `externalStatus != "SUCCESS"` → End Error
4. End Event

**Примечание:** Service Task можно пометить как `asyncBefore="true"`, чтобы создать **Job Definition**.

---

## 🗂️ Логирование действий

Все внешние проверки сохраняются в базу через `AuditLogRepository`.
Схема таблицы (PostgreSQL/H2):

| Колонка           | Тип       |
| ----------------- | --------- |
| id                | Long PK   |
| processInstanceId | String    |
| action            | String    |
| payload           | Text      |
| createdAt         | Timestamp |

---

## ⚡ Примечания и устранение ошибок

* **Cannot instantiate delegate:** Убедитесь, что BPMN использует `camunda:delegateExpression="${checkExternalDelegate}"` и класс аннотирован `@Component("checkExternalDelegate")`.
* **WebClient ошибки:** Добавьте зависимость `spring-boot-starter-webflux` в `pom.xml`.
* **TTL предупреждение:** Добавьте `camunda:historyTimeToLive="30"` в процесс, чтобы избежать `ENGINE-12018`.
* **Job definition предупреждение:** Опционально; отметьте Service Task как `asyncBefore="true"`, чтобы создать job.

---

## 📦 Зависимости

* `spring-boot-starter-web`
* `spring-boot-starter-webflux`
* `spring-boot-starter-data-jpa`
* `camunda-bpm-spring-boot-starter`
* `camunda-bpm-spring-boot-starter-rest`
* `h2` / `postgresql`

---

## 🏁 Резюме

Проект демонстрирует:

* Оркестрацию BPMN процессов с Camunda
* Внешнюю интеграцию через REST с помощью WebClient
* Условные ветвления и обработку ошибок
* Логирование действий в базу данных
* Dockerized развертывание для быстрого старта

---