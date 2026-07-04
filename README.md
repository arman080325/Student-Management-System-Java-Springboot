<div align="center">

<img src="https://readme-typing-svg.demolab.com/?font=JetBrains+Mono&size=32&duration=2500&pause=900&color=14C07D&center=true&vCenter=true&width=650&lines=Student+Management+System;Spring+Boot+%2B+JPA+%2B+PostgreSQL;CRUD+%C2%B7+Search+%C2%B7+Swagger+%C2%B7+Dark+Mode;From+Java+Swing+Desktop+%E2%86%92+Cloud+Web+App" alt="Typing SVG" />

### A full-stack **student records console**, rebuilt from a legacy Java Swing + Oracle desktop app into a modern, deployable **Spring Boot** web application.

<br/>

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Production-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![H2](https://img.shields.io/badge/H2-Dev%20DB-1E88E5?style=for-the-badge&logo=h2&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

![CI](https://img.shields.io/github/actions/workflow/status/arman080325/student-management-system/ci.yml?branch=main&style=for-the-badge&label=CI&logo=githubactions&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)
![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen?style=for-the-badge&logo=git&logoColor=white)

<br/>

**[🚀 Live Demo](#)** &nbsp;·&nbsp; **[📖 API Docs (Swagger)](#-api-reference)** &nbsp;·&nbsp; **[⚙️ Run Locally](#-getting-started)** &nbsp;·&nbsp; **[🏗️ Architecture](#-architecture)**

<br/>

<img src="https://user-images.githubusercontent.com/74038190/212284100-561aa473-3905-4a80-b561-0d28506553ee.gif" width="70%">

</div>

<br/>

## 📌 About The Project

**Student Management System** is a records console built for *Silicon University* — create, search, edit, and manage student records through a clean REST API paired with a fast, console-styled web dashboard.

It began life as a **Java Swing + Oracle desktop application**. This version is a ground-up rebuild as a cloud-ready **Spring Boot** service, fixing the original's structural weaknesses along the way:

| Legacy Desktop App | This Rebuild |
|---|---|
| Random roll numbers, collision-prone | Sequence-generated, guaranteed-unique roll numbers |
| Everything stored as `VARCHAR` | Proper types — `LocalDate`, numeric percentages |
| Oracle-only, tightly coupled | PostgreSQL in prod, zero-setup H2 for local dev |
| No API — UI directly hits the DB | Layered REST API, DTOs, and a decoupled frontend |
| Stored Aadhaar (govt ID) in plaintext | Deliberately removed — unnecessary PII in a public demo |
| No tests, no CI | Full integration test suite + GitHub Actions on every push |

<br/>

## ✨ Features

<table>
<tr>
<td width="50%" valign="top">

### 🗃️ Core Functionality
- ✅ Full **CRUD** for student records
- 🔢 Server-generated, **collision-free roll numbers**
- 🔍 Live **search & filter** — by name, roll no, course, or branch
- 🧾 Field-level **server-side validation** (email, phone, percentage bounds, required fields)
- 🌱 **Auto-seeded demo data** on first boot (never shows a blank table)

</td>
<td width="50%" valign="top">

### 🎨 Frontend Experience
- 🌗 **Dark / Light / System** theme — persisted, applied pre-paint (no flash)
- ⚡ Zero-build **vanilla HTML/CSS/JS** dashboard
- ⌨️ Console-inspired UI with a blinking cursor accent
- 📊 Live **record counter** chip
- 🧭 Instant client-side reset & clear-all (env-gated)

</td>
</tr>
<tr>
<td width="50%" valign="top">

### 🛡️ Security & Ops
- 🧱 Hardened response headers: **CSP**, `X-Frame-Options`, `X-Content-Type-Options`, `Referrer-Policy`, `Permissions-Policy`
- 🚫 Destructive **"clear all"** disabled in production via feature flag
- 💓 Actuator **health endpoint** for uptime monitoring
- 🔐 Zero hard-coded secrets — 100% environment-driven config

</td>
<td width="50%" valign="top">

### 🧩 Developer Experience
- 📘 Interactive **Swagger UI** API docs, auto-generated
- 🧪 Full **integration test suite** (MockMvc, full Spring context)
- 🔁 **CI pipeline** via GitHub Actions on every push/PR
- 🏗️ Clean **layered architecture** with DTOs isolating persistence from the web layer

</td>
</tr>
</table>

<br/>

## 🧰 Tech Stack

<div align="center">

| Layer | Technology |
|---|---|
| **Language / Runtime** | Java 21 |
| **Framework** | Spring Boot 3.3.4 (Web, Data JPA, Validation, Actuator) |
| **Database** | PostgreSQL (production, via Neon) · H2 in-memory (local dev) |
| **API Docs** | springdoc-openapi (Swagger UI) |
| **Frontend** | Vanilla HTML / CSS / JavaScript — no build tooling required |
| **Boilerplate reduction** | Lombok |
| **Build tool** | Maven (bundled wrapper — no local install needed) |
| **CI/CD** | GitHub Actions |
| **Deployment target** | Render (container) + Neon Postgres |

</div>

<br/>

## 🏗️ Architecture

A clean, layered design keeps persistence, business rules, and the web layer decoupled via DTOs:

```
                 ┌─────────────┐      ┌──────────┐      ┌────────────┐      ┌─────────┐
  HTTP Request → │  Controller │  →   │ Service  │  →   │ Repository │  →   │ Entity  │ → PostgreSQL / H2
                 │  (REST API) │      │ (rules + │      │ (Spring    │      │  (JPA)  │
                 │             │  ←   │  DTOs)   │  ←   │ Data JPA)  │  ←   │         │
                 └─────────────┘      └──────────┘      └────────────┘      └─────────┘
                        ↓
                 StudentRequest / StudentResponse (DTOs)
```

**Key design decisions**

- 🔢 **Roll numbers** are derived from a database sequence in an entity's `@PrePersist` hook — guaranteeing uniqueness, unlike the original's random-string approach.
- 🗓️ **Real column types** — `LocalDate` for dates of birth, numeric `Double` for Class XII percentage — replacing the legacy all-`VARCHAR` schema.
- 🧬 **DTO separation** — `StudentRequest` / `StudentResponse` keep the JPA entity out of the wire format entirely.
- 🚦 **Centralized error handling** via a `GlobalExceptionHandler` and a dedicated `StudentNotFoundException`.
- 🔑 **No secrets in source** — all credentials come from environment variables; local dev runs against a zero-setup in-memory database out of the box.
- 🪪 **Aadhaar field intentionally dropped** from the legacy schema — storing a government identity number in a public-facing demo is unnecessary PII exposure.

<br/>

## 📂 Project Structure

```
sms/
├── src/main/java/online/armanportfolio/sms/
│   ├── controller/       # StudentController, MetaController — REST endpoints
│   ├── service/          # StudentService — business logic
│   ├── repository/       # StudentRepository — Spring Data JPA
│   ├── model/            # Student — JPA entity
│   ├── dto/              # StudentRequest / StudentResponse
│   ├── exception/        # GlobalExceptionHandler, StudentNotFoundException
│   ├── config/           # DataSeeder, SecurityHeadersFilter, OpenApiConfig, JpaConfig
│   └── StudentManagementApplication.java
├── src/main/resources/
│   ├── static/           # index.html, css/, js/ — the dashboard (no build step)
│   ├── application.properties        # dev profile (H2)
│   └── application-prod.properties   # prod profile (PostgreSQL)
├── src/test/java/.../StudentApiIntegrationTest.java
├── .github/workflows/ci.yml
├── .env.example
└── pom.xml
```

<br/>

## 🚀 Getting Started

### Prerequisites

- **JDK 21**
- That's it — Maven ships via the included wrapper (`mvnw` / `mvnw.cmd`), and local dev uses an in-memory database, so there's nothing else to install or configure.

### Run it

```bash
# clone the repo
git clone https://github.com/arman080325/student-management-system.git
cd student-management-system

# macOS / Linux
./mvnw spring-boot:run

# Windows PowerShell
.\mvnw spring-boot:run
```

Then open:

| URL | What you'll find |
|---|---|
| `http://localhost:8080/` | 🖥️ The student records dashboard |
| `http://localhost:8080/swagger-ui.html` | 📘 Interactive API documentation |
| `http://localhost:8080/actuator/health` | 💓 Health check endpoint |
| `http://localhost:8080/h2-console` | 🗄️ H2 database console *(dev only)* |

> 💡 The app boots against an in-memory H2 database and **auto-seeds a handful of demo student records** on first run — no manual setup required.

<br/>

## ⚙️ Configuration

Production configuration is 100% environment-variable driven — see `.env.example`:

| Variable | Description |
|---|---|
| `SPRING_PROFILES_ACTIVE` | Set to `prod` to switch to PostgreSQL |
| `SPRING_DATASOURCE_URL` | JDBC URL, e.g. `jdbc:postgresql://<host>/<db>?sslmode=require` |
| `SPRING_DATASOURCE_USERNAME` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | Database password |
| `PORT` | HTTP port (auto-provided by Render) |

The destructive **"clear all"** endpoint is controlled by `app.allow-clear-all` — enabled by default in dev, **disabled in production**.

<br/>

## 📡 API Reference

Base path: **`/api/students`**

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/students` | List all students — supports `?field=&search=` for filtered search |
| `GET` | `/api/students/{id}` | Fetch a single student by ID |
| `GET` | `/api/students/count` | Get the total record count |
| `POST` | `/api/students` | Create a new student — roll number is server-generated |
| `PUT` | `/api/students/{id}` | Update an existing student |
| `DELETE` | `/api/students/{id}` | Delete a single student |
| `DELETE` | `/api/students` | Clear all records *(disabled in production)* |
| `GET` | `/api/meta` | Runtime feature flags (e.g. whether clear-all is enabled) |

Every input field is validated server-side (required fields, email format, phone format, percentage bounds 0–100), with structured error responses handled by a global exception handler.

📘 **Full interactive documentation, request/response schemas, and a try-it-out console are available at `/swagger-ui.html`.**

<br/>

## 🧪 Testing

```bash
./mvnw verify
```

This runs `StudentApiIntegrationTest` — a full-context integration suite (Spring Boot + MockMvc) covering:

- ✅ Successful creation with a generated roll number
- ✅ Validation failures on bad input
- ✅ Listing and searching
- ✅ 404 handling for missing records
- ✅ Deletion

The identical suite runs automatically on every push and pull request via **GitHub Actions** (`.github/workflows/ci.yml`).

<br/>

## 🔐 Security

- 🧱 Baseline security headers on **every** response: `Content-Security-Policy`, `X-Content-Type-Options`, `X-Frame-Options`, `Referrer-Policy`, `Permissions-Policy`
- ✅ All input validated server-side with Jakarta Bean Validation
- 🙈 No credentials ever committed — configuration is fully environment-driven
- 🚫 Bulk-delete is disabled by default on the public deployment
- 📉 Actuator only exposes the `health` endpoint in production

<br/>

## ☁️ Deployment

Deployed as a single container on **[Render](https://render.com)**, backed by a **[Neon](https://neon.tech)** serverless PostgreSQL database.

```
GitHub push → GitHub Actions CI (build + test) → Render (container build) → Neon PostgreSQL
```

Set the environment variables from the [Configuration](#️-configuration) section on your Render service, point `SPRING_DATASOURCE_URL` at your Neon connection string, and deploy.

<br/>

## 🗺️ Roadmap

- [ ] Pagination for large student lists
- [ ] CSV/Excel bulk import & export
- [ ] Role-based authentication (admin vs. read-only)
- [ ] Student photo uploads
- [ ] Dockerfile checked into the repo for one-command local containerized runs

<br/>

## 🤝 Contributing

Contributions are welcome! Feel free to open an issue or submit a pull request.

```bash
# fork, then:
git checkout -b feature/your-feature
git commit -m "Add your feature"
git push origin feature/your-feature
# open a PR
```

<br/>

## 📄 License

Distributed under the **MIT License**. See `LICENSE` for details.

<br/>

## 👤 Author

<div align="center">

**Arman Ahemad Khan**

[![Portfolio](https://img.shields.io/badge/Portfolio-arman--portfolio.online-14C07D?style=for-the-badge&logo=googlechrome&logoColor=white)](https://arman-portfolio.online)
[![GitHub](https://img.shields.io/badge/GitHub-arman080325-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/arman080325)

<br/>

⭐ **If this project helped you, consider giving it a star!** ⭐

<img src="https://user-images.githubusercontent.com/74038190/212284158-e840e285-664b-44d7-b79b-e264b5e54825.gif" width="100%">

</div>