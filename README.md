<div align="center">

<img src="https://readme-typing-svg.demolab.com/?font=DM+Mono&size=30&duration=2600&pause=900&color=006C47&center=true&vCenter=true&width=680&lines=Student+Management+System;Spring+Boot+%2B+JPA+%2B+PostgreSQL;CRUD+%C2%B7+Analytics+%C2%B7+CSV+%C2%B7+Swagger;From+Java+Swing+Desktop+%E2%86%92+Cloud+Web+App" alt="Student Management System" />

### A full-stack **student records platform** for Silicon University — rebuilt from a legacy Java Swing + Oracle desktop app into a modern, deployable **Spring Boot** web application.

<br/>

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Production-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![H2](https://img.shields.io/badge/H2-Dev%20DB-1E88E5?style=for-the-badge&logo=h2&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

![CI](https://img.shields.io/github/actions/workflow/status/arman080325/student-management-system/ci.yml?branch=main&style=for-the-badge&label=CI&logo=githubactions&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)

<br/>

**[🚀 Live Demo](#)** &nbsp;·&nbsp; **[📖 API Docs (Swagger)](#-api-reference)** &nbsp;·&nbsp; **[⚙️ Run Locally](#-getting-started)** &nbsp;·&nbsp; **[🏗️ Architecture](#-architecture)**

</div>

<br/>

## 📌 About The Project

**Student Management System** is a records platform built for *Silicon University* — create, search, edit, and analyse student records through a clean REST API paired with a fast, responsive web dashboard (light & dark themes).

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

### 🗃️ Records
- Full **CRUD** with server-generated, **collision-free roll numbers**
- **Server-side pagination & sorting** — click any column header to sort; page through large datasets efficiently
- Live **search & filter** by name, roll number, course, or branch
- **Bulk selection & delete**, plus one-click **CSV export**
- Field-level **server-side validation** (email, phone, percentage bounds, required fields)
- **Auto-seeded demo data** on first boot — never a blank table

### 📊 Analytics Dashboard
- Headline stat cards — total students, average Class XII %, courses, active branches
- **Distribution charts** — students by course and by branch
- **Top performer** spotlight

### 🎨 Experience
- **Light / Dark / System** theme — persisted, applied pre-paint (no flash)
- Zero-build **vanilla HTML / CSS / JS** dashboard with an editorial type system
- Colour-coded avatars, performance badges, and a live record counter
- **Keyboard shortcuts** (`/` search · `n` new · `g d` dashboard · `?` help)

### 🛡️ Security & Ops
- Hardened response headers: **CSP**, `X-Frame-Options`, `X-Content-Type-Options`, `Referrer-Policy`, `Permissions-Policy`
- Destructive **"clear all"** disabled in production via feature flag
- Actuator **health endpoint** for uptime monitoring
- **Zero hard-coded secrets** — fully environment-driven config

### 🧩 Developer Experience
- Interactive **Swagger UI** API docs, auto-generated
- Full **integration test suite** (MockMvc, full Spring context)
- **CI pipeline** via GitHub Actions on every push / PR
- Clean **layered architecture** with DTOs isolating persistence from the web layer

<br/>

## 🧰 Tech Stack

| Layer | Technology |
|---|---|
| **Language / Runtime** | Java 21 |
| **Framework** | Spring Boot 3.3.4 (Web, Data JPA, Validation, Actuator) |
| **Database** | PostgreSQL (production, via Neon) · H2 in-memory (local dev) |
| **API Docs** | springdoc-openapi (Swagger UI) |
| **Frontend** | Vanilla HTML / CSS / JavaScript — no build tooling |
| **Boilerplate reduction** | Lombok |
| **Build tool** | Maven (bundled wrapper — no local install needed) |
| **CI/CD** | GitHub Actions |
| **Deployment** | Render (container) + Neon Postgres |

<br/>

## 🏗️ Architecture

A clean, layered design keeps persistence, business rules, and the web layer decoupled via DTOs:

```
  HTTP  →  Controller  →  Service  →  Repository  →  Entity  →  PostgreSQL / H2
             (REST)       (rules)    (Spring Data)    (JPA)
                 ↑            ↑
          StudentRequest / StudentResponse (DTOs)
```

**Key design decisions**

- **Roll numbers** are derived from a database sequence in the entity's `@PrePersist` hook — guaranteeing uniqueness, unlike the original's random-string approach.
- **Real column types** — `LocalDate` for dates, numeric `Double` for Class XII percentage — replacing the legacy all-`VARCHAR` schema.
- **DTO separation** — `StudentRequest` / `StudentResponse` keep the JPA entity off the wire.
- **Centralised error handling** via `GlobalExceptionHandler`.
- **No secrets in source** — credentials come from environment variables; local dev runs on a zero-setup in-memory database.
- **Aadhaar field intentionally dropped** — storing a government ID in a public demo is unnecessary PII exposure.

<br/>

## 📂 Project Structure

```
sms/
├── src/main/java/online/armanportfolio/sms/
│   ├── controller/   # StudentController, MetaController — REST endpoints
│   ├── service/      # StudentService — business logic, stats, CSV
│   ├── repository/   # StudentRepository — Spring Data JPA
│   ├── model/        # Student — JPA entity
│   ├── dto/          # StudentRequest / StudentResponse / PagedResponse / StudentStatsResponse
│   ├── exception/    # GlobalExceptionHandler, StudentNotFoundException
│   ├── config/       # DataSeeder, SecurityHeadersFilter, OpenApiConfig, JpaConfig
│   └── StudentManagementApplication.java
├── src/main/resources/
│   ├── static/       # index.html, css/, js/ — the dashboard (no build step)
│   ├── application.properties        # dev profile (H2)
│   └── application-prod.properties   # prod profile (PostgreSQL)
├── src/test/java/.../StudentApiIntegrationTest.java
├── .github/workflows/ci.yml
└── pom.xml
```

<br/>

## 🚀 Getting Started

### Prerequisites
- **JDK 21** — that's it. Maven ships via the wrapper, and local dev uses an in-memory database.

### Run it
```bash
git clone https://github.com/arman080325/student-management-system.git
cd student-management-system

./mvnw spring-boot:run      # macOS / Linux
.\mvnw spring-boot:run       # Windows PowerShell
```

Then open:

| URL | What you'll find |
|---|---|
| `http://localhost:8080/` | 🖥️ The student dashboard |
| `http://localhost:8080/swagger-ui.html` | 📘 Interactive API documentation |
| `http://localhost:8080/actuator/health` | 💓 Health check |
| `http://localhost:8080/h2-console` | 🗄️ H2 console *(dev only)* |

> The app boots against an in-memory H2 database and **auto-seeds demo records** on first run — no setup required.

<br/>

## ⚙️ Configuration

Production configuration is fully environment-variable driven — see `.env.example`:

| Variable | Description |
|---|---|
| `SPRING_PROFILES_ACTIVE` | Set to `prod` to switch to PostgreSQL |
| `SPRING_DATASOURCE_URL` | JDBC URL, e.g. `jdbc:postgresql://<host>/<db>?sslmode=require` |
| `SPRING_DATASOURCE_USERNAME` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | Database password |
| `PORT` | HTTP port (auto-provided by Render) |

The destructive **"clear all"** endpoint is controlled by `app.allow-clear-all` — enabled in dev, **disabled in production**.

<br/>

## 📡 API Reference

Base path: **`/api/students`**

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/students` | Paginated list — `?page=&size=&sortBy=&sortDir=&field=&search=` |
| `GET` | `/api/students/{id}` | Fetch a single student |
| `GET` | `/api/students/count` | Total record count |
| `GET` | `/api/students/stats` | Aggregate figures for the dashboard |
| `GET` | `/api/students/export` | Download all records as CSV |
| `POST` | `/api/students` | Create — roll number is server-generated |
| `PUT` | `/api/students/{id}` | Update an existing student |
| `DELETE` | `/api/students/{id}` | Delete a single student |
| `DELETE` | `/api/students/bulk` | Delete a list of IDs |
| `DELETE` | `/api/students` | Clear all *(disabled in production)* |
| `GET` | `/api/meta` | Runtime feature flags |

📘 **Full interactive docs, schemas, and a try-it-out console live at `/swagger-ui.html`.**

<br/>

## 🧪 Testing

```bash
./mvnw verify
```

Runs `StudentApiIntegrationTest` — a full-context suite (Spring Boot + MockMvc) covering creation with generated roll number, validation failures, listing, 404 handling, and deletion. The same suite runs on every push via **GitHub Actions**.

<br/>

## 🔐 Security

- Baseline security headers on **every** response (CSP, `X-Content-Type-Options`, `X-Frame-Options`, `Referrer-Policy`, `Permissions-Policy`)
- All input validated server-side with Jakarta Bean Validation
- No credentials ever committed — configuration is fully environment-driven
- Bulk-delete-everything disabled on the public deployment
- Actuator exposes only the `health` endpoint in production

<br/>

## ☁️ Deployment

Deployed as a single container on **[Render](https://render.com)**, backed by a **[Neon](https://neon.tech)** serverless PostgreSQL database.

```
GitHub push → GitHub Actions (build + test) → Render (container) → Neon PostgreSQL
```

Set the environment variables from [Configuration](#️-configuration) on the Render service, point `SPRING_DATASOURCE_URL` at your Neon connection string, and deploy.

<br/>

## 🗺️ Roadmap

- [ ] CSV / Excel **import** (export already shipped)
- [ ] Role-based authentication (admin vs. read-only)
- [ ] Student photo uploads
- [ ] Per-branch analytics drill-down

<br/>

## 👤 Author

<div align="center">

**Arman Ahemad Khan**

[![Portfolio](https://img.shields.io/badge/Portfolio-arman--portfolio.online-006C47?style=for-the-badge&logo=googlechrome&logoColor=white)](https://arman-portfolio.online)
[![GitHub](https://img.shields.io/badge/GitHub-arman080325-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/arman080325)

</div>

## 📄 License

Distributed under the **MIT License**.
