<div align="center">

<img src="https://readme-typing-svg.demolab.com/?font=DM+Mono&size=30&duration=2600&pause=900&color=006C47&center=true&vCenter=true&width=720&lines=Student+Management+System;Spring+Boot+%2B+JPA+%2B+PostgreSQL;CRUD+%C2%B7+Analytics+%C2%B7+CSV+%C2%B7+Swagger;Java+Swing+Desktop+%E2%86%92+Cloud+Web+App" alt="Student Management System" />

### A full-stack **student records platform** for Silicon University — rebuilt from a legacy Java Swing + Oracle desktop app into a modern, deployable **Spring Boot** web application.

<br/>

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Neon-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Render-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

![CI](https://img.shields.io/github/actions/workflow/status/arman080325/Student-Management-System-Java-Springboot/ci.yml?branch=main&style=for-the-badge&label=CI&logo=githubactions&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)
![PRs Welcome](https://img.shields.io/badge/PRs-welcome-006C47?style=for-the-badge&logo=git&logoColor=white)

<br/>

[![Live Demo](https://img.shields.io/badge/🚀_LIVE_DEMO-Open_App-006C47?style=for-the-badge)](https://student-management-system-java-springboot.onrender.com)
&nbsp;
[![Swagger](https://img.shields.io/badge/📖_API_Docs-Swagger_UI-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)](https://student-management-system-java-springboot.onrender.com/swagger-ui.html)

**[⚙️ Run Locally](#-getting-started)** &nbsp;·&nbsp; **[🏗️ Architecture](#-architecture)** &nbsp;·&nbsp; **[📡 API](#-api-reference)** &nbsp;·&nbsp; **[⌨️ Shortcuts](#-keyboard-shortcuts)**

<br/>

<img src="https://user-images.githubusercontent.com/74038190/212284100-561aa473-3905-4a80-b561-0d28506553ee.gif" width="70%">

</div>

<br/>

## 📌 About The Project

**Student Management System** is a records platform built for *Silicon University* — create, search, sort, analyse, and export student records through a clean REST API paired with a fast, responsive dashboard (light **and** dark themes).

It began life as a **Java Swing + Oracle desktop application**. This version is a ground-up rebuild as a cloud-ready **Spring Boot** service — deployed on Render + Neon — fixing the original's structural weaknesses along the way:

| 🗿 Legacy Desktop App | ✨ This Rebuild |
|---|---|
| Random roll numbers, collision-prone | Sequence-generated, **guaranteed-unique** roll numbers |
| Everything stored as `VARCHAR` | Proper types — `LocalDate`, numeric percentages |
| Oracle-only, tightly coupled | **PostgreSQL** in prod, zero-setup **H2** for local dev |
| No API — UI hits the DB directly | **Layered REST API**, DTOs, decoupled frontend |
| Hard-coded DB password in source | **Zero secrets** — fully environment-driven |
| Stored Aadhaar (govt ID) in plaintext | **Removed** — no needless PII in a public demo |
| No tests, no CI | **Integration suite + GitHub Actions** on every push |
| Single desktop window | **Deployed to the cloud** with a public URL |

<br/>

## 🖼️ Screenshots

> _Add your captures under `docs/` and swap the links below._

| 🌞 Records (Light) | 🌚 Dashboard (Dark) |
|:---:|:---:|
| <img src="docs/records-light.png" width="100%"/> | <img src="docs/dashboard-dark.png" width="100%"/> |

<br/>

## ✨ Features

<table>
<tr>
<td width="50%" valign="top">

### 🗃️ Records
- ✅ Full **CRUD** for student records
- 🔢 Server-generated, **collision-free roll numbers**
- 📑 **Server-side pagination** + **column sorting**
- 🔍 Live **search & filter** — name, roll no, course, branch
- ☑️ **Bulk select & delete**
- 📤 One-click **CSV export**
- 🧾 Field-level **server-side validation**
- 🌱 **Auto-seeded** demo data on first boot

</td>
<td width="50%" valign="top">

### 📊 Analytics Dashboard
- 🔢 Headline **stat cards** — totals, average XII %, courses, branches
- 📈 **Distribution charts** — students by course & branch
- 🏆 **Top performer** spotlight
- 🎨 Colour-coded **avatars** & performance **badges**

</td>
</tr>
<tr>
<td width="50%" valign="top">

### 🎨 Experience
- 🌗 **Light / Dark / System** theme — persisted, no flash
- ⚡ Zero-build **vanilla HTML/CSS/JS**
- ✒️ Editorial type system (Fraunces · Hanken Grotesk · DM Mono)
- ⌨️ **Keyboard shortcuts** for power users
- 📱 Fully **responsive** down to mobile

</td>
<td width="50%" valign="top">

### 🛡️ Security · Ops · DX
- 🧱 Hardened headers: **CSP**, `X-Frame-Options`, `nosniff`, `Referrer-Policy`
- 🚫 Destructive **"clear all"** disabled in production
- 💓 Actuator **health endpoint**
- 📘 Interactive **Swagger UI**
- 🧪 **Integration tests** + 🔁 **GitHub Actions CI**

</td>
</tr>
</table>

<br/>

## 🧰 Tech Stack

<div align="center">

| Layer | Technology |
|---|---|
| **Language / Runtime** | Java 21 |
| **Framework** | Spring Boot 3.3.4 — Web · Data JPA · Validation · Actuator |
| **Database** | PostgreSQL (prod, via Neon) · H2 in-memory (dev) |
| **API Docs** | springdoc-openapi (Swagger UI) |
| **Frontend** | Vanilla HTML / CSS / JavaScript — no build tooling |
| **Boilerplate** | Lombok |
| **Build** | Maven (bundled wrapper) |
| **CI/CD** | GitHub Actions |
| **Deployment** | Docker → Render · Neon Postgres |

</div>

<br/>

## 🏗️ Architecture

A clean, layered design keeps persistence, business rules, and the web layer decoupled via DTOs:

```
  HTTP  →  Controller  →  Service  →  Repository  →  Entity  →  PostgreSQL / H2
             (REST)       (rules)    (Spring Data)    (JPA)
                 ↑            ↑
          StudentRequest / StudentResponse / PagedResponse / StatsResponse
```

**Key design decisions**

- 🔢 **Roll numbers** derive from a DB sequence in the entity's `@PrePersist` hook — guaranteed unique, unlike the original's random strings.
- 🗓️ **Real column types** — `LocalDate` for DOB, numeric `Double` for Class XII % — replacing the legacy all-`VARCHAR` schema.
- 🧬 **DTO separation** keeps the JPA entity off the wire (`StudentRequest` / `StudentResponse`).
- 🚦 **Centralised error handling** via `GlobalExceptionHandler` with structured JSON errors.
- 🔑 **No secrets in source** — env-driven config; dev runs on a zero-setup in-memory DB.
- 🪪 **Aadhaar dropped** — no government IDs in a public demo.

<br/>

## 📂 Project Structure

```
.
├── src/main/java/online/armanportfolio/sms/
│   ├── controller/   # StudentController, MetaController
│   ├── service/      # StudentService — CRUD, stats, CSV export
│   ├── repository/   # StudentRepository — Spring Data JPA
│   ├── model/        # Student — JPA entity
│   ├── dto/          # StudentRequest · StudentResponse · PagedResponse · StudentStatsResponse
│   ├── exception/    # GlobalExceptionHandler, StudentNotFoundException
│   ├── config/       # DataSeeder · SecurityHeadersFilter · OpenApiConfig · JpaConfig
│   └── StudentManagementApplication.java
├── src/main/resources/
│   ├── static/       # index.html · css/ · js/ — the dashboard (no build step)
│   ├── application.properties        # dev profile (H2)
│   └── application-prod.properties   # prod profile (PostgreSQL)
├── src/test/java/.../StudentApiIntegrationTest.java
├── .github/workflows/ci.yml
├── Dockerfile · render.yaml
└── pom.xml
```

<br/>

## 🚀 Getting Started

### Prerequisites
**JDK 21** — that's it. Maven ships via the wrapper, and local dev uses an in-memory database.

### Run it
```bash
git clone https://github.com/arman080325/Student-Management-System-Java-Springboot.git
cd Student-Management-System-Java-Springboot

./mvnw spring-boot:run      # macOS / Linux
.\mvnw spring-boot:run       # Windows PowerShell
```

Then open:

| URL | What you'll find |
|---|---|
| `http://localhost:8080/` | 🖥️ The student dashboard |
| `http://localhost:8080/swagger-ui.html` | 📘 Interactive API docs |
| `http://localhost:8080/actuator/health` | 💓 Health check |
| `http://localhost:8080/h2-console` | 🗄️ H2 console *(dev only)* |

> 💡 The app auto-seeds demo records on first run — no setup required.

<br/>

## ⚙️ Configuration

Production config is 100% environment-variable driven — see `.env.example`:

| Variable | Description |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `prod` to switch to PostgreSQL |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://<host>/<db>?sslmode=require` |
| `SPRING_DATASOURCE_USERNAME` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | Database password |
| `PORT` | HTTP port (auto-provided by Render) |

The destructive **"clear all"** endpoint is gated by `app.allow-clear-all` — on in dev, **off in production**.

<br/>

## 📡 API Reference

Base path: **`/api/students`**

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/students` | **Paginated** list — `?page=&size=&sortBy=&sortDir=&field=&search=` |
| `GET` | `/api/students/{id}` | Fetch a single student |
| `GET` | `/api/students/count` | Total record count |
| `GET` | `/api/students/stats` | Aggregate figures for the dashboard |
| `GET` | `/api/students/export` | Download all records as **CSV** |
| `POST` | `/api/students` | Create — roll number is server-generated |
| `PUT` | `/api/students/{id}` | Update an existing student |
| `DELETE` | `/api/students/{id}` | Delete a single student |
| `DELETE` | `/api/students/bulk` | Delete a list of IDs (JSON body) |
| `DELETE` | `/api/students` | Clear all *(disabled in production)* |
| `GET` | `/api/meta` | Runtime feature flags |

📘 **Full schemas + a try-it-out console live at [`/swagger-ui.html`](https://student-management-system-java-springboot.onrender.com/swagger-ui.html).**

<br/>

## ⌨️ Keyboard Shortcuts

| Key | Action |
|:---:|---|
| <kbd>/</kbd> | Focus search |
| <kbd>n</kbd> | New student |
| <kbd>g</kbd> then <kbd>d</kbd> | Go to Dashboard |
| <kbd>g</kbd> then <kbd>r</kbd> | Go to Records |
| <kbd>?</kbd> | Show shortcuts |
| <kbd>Esc</kbd> | Close dialog |

<br/>

## 🧪 Testing

```bash
./mvnw verify
```

Runs `StudentApiIntegrationTest` — a full-context suite (Spring Boot + MockMvc) covering creation with a generated roll number, validation failures, listing, 404 handling, and deletion. The same suite runs on every push via **GitHub Actions**.

<br/>

## 🔐 Security

- 🧱 Baseline security headers on **every** response — `Content-Security-Policy`, `X-Content-Type-Options`, `X-Frame-Options`, `Referrer-Policy`, `Permissions-Policy`
- ✅ All input validated server-side with Jakarta Bean Validation
- 🙈 No credentials committed — configuration is env-driven
- 🚫 Bulk "clear all" disabled on the public deployment
- 📉 Actuator exposes only `health` in production

<br/>

## ☁️ Deployment

Live as a single Docker container on **[Render](https://render.com)**, backed by **[Neon](https://neon.tech)** serverless PostgreSQL.

```
git push  →  GitHub Actions (build + test)  →  Render (Docker build)  →  Neon PostgreSQL
```

Point `SPRING_DATASOURCE_URL` at your Neon connection string, set the env vars from [Configuration](#️-configuration), set health check to `/actuator/health`, and deploy.

> ℹ️ The free tier sleeps after ~15 min idle, so the first request after a quiet spell may take ~30–50s to wake.

<br/>

## 🗺️ Roadmap

- [x] Server-side pagination & sorting
- [x] CSV export
- [x] Analytics dashboard
- [x] Dockerised deployment on Render + Neon
- [ ] CSV / Excel **import**
- [ ] Role-based authentication (admin vs. read-only)
- [ ] Student photo uploads

<br/>

## 🤝 Contributing

Contributions welcome — open an issue or a PR.

```bash
git checkout -b feature/your-feature
git commit -m "Add your feature"
git push origin feature/your-feature
```

<br/>

## 👤 Author

<div align="center">

**Arman Ahemad Khan** — *Full-Stack Developer & Ethical Hacker*

[![Portfolio](https://img.shields.io/badge/Portfolio-arman--portfolio.online-006C47?style=for-the-badge&logo=googlechrome&logoColor=white)](https://arman-portfolio.online)
[![GitHub](https://img.shields.io/badge/GitHub-arman080325-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/arman080325)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-arman--ahemad--khan-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/arman-ahemad-khan)

<br/>

⭐ **If this project helped or inspired you, drop a star!** ⭐

<img src="https://user-images.githubusercontent.com/74038190/212284158-e840e285-664b-44d7-b79b-e264b5e54825.gif" width="100%">

</div>

## 📄 License

Distributed under the **MIT License**. See [`LICENSE`](LICENSE) for details.