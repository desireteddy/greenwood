# Greenwood Eco School Smart Campus LMS

This package is a Spring Boot + Thymeleaf + MySQL LMS for Greenwood Eco School.

## First administrator login

On a fresh database, the application creates an administrator automatically if no ADMIN account exists:

- Username: `admin`
- Temporary password: `Greenwood@12345`
- Role: `ADMIN`

The administrator is forced to change the temporary password at first login.

For safer deployment, set these environment variables before starting the application:

- `GREENWOOD_ADMIN_USERNAME`
- `GREENWOOD_ADMIN_PASSWORD`
- `GREENWOOD_DB_URL`
- `GREENWOOD_DB_USERNAME`
- `GREENWOOD_DB_PASSWORD`

The bootstrap only creates the first administrator when an ADMIN account does not already exist; it does not overwrite an existing administrator password.

## Administrator system settings

After login, open **System settings** from the Administration section. No source-code changes are required for normal school configuration. The administrator can change:

- School name
- School tagline
- Dashboard welcome title and message
- School logo URL
- Contact email and phone
- School address
- Current academic year
- Footer text

Account passwords are managed from **Accounts** (for other users) and **My password** (for the current administrator).

## Local Windows setup with XAMPP/WAMP

XAMPP/WAMP can provide MySQL/MariaDB locally, but this application itself is Java/Spring Boot, not PHP. Start the database service in XAMPP/WAMP, then run the Spring Boot application with Java/Maven.

### Requirements

1. Install Java 17 or newer.
2. Install XAMPP or WAMP and start MySQL/MariaDB.
3. Make sure MySQL is listening on port 3306 unless you intentionally change `GREENWOOD_DB_URL`.
4. Install Maven, or use the Maven wrapper if one is added to your development environment.

### Database

You can create the database manually by importing `src/db/schema.sql` in phpMyAdmin. The application uses Flyway as the single source of truth for tables and migrations. `src/db/schema.sql` only creates/selects the `SmartCampus` database; the application creates the tables automatically through Flyway.

### Start the application

Open a terminal in the project directory:

```text
mvn spring-boot:run
```

Then open:

```text
http://localhost:8080/login
```

If your MySQL root account has a password, set:

```text
GREENWOOD_DB_PASSWORD=your_mysql_password
```

If port 8080 is already in use, add `server.port=8081` to `application.properties`.

## Build a deployable JAR

From the project directory:

```text
mvn clean package
java -jar target/Smart-Campus-1.0.0.jar
```

Do not commit the generated `target/` directory to GitHub. It is build output and can contain stale copies of classes and JARs.

## GitHub Pages warning

GitHub Pages is a static hosting service. It cannot run the Spring Boot Java backend, database, login sessions, or server-side Thymeleaf application. GitHub's documentation explicitly states that Pages does not support server-side languages such as PHP, Ruby, or Python, and Pages is intended for static files. Therefore, do **not** upload the live LMS backend and database to GitHub Pages as if it were a complete production server.

For a real online LMS, host the Spring Boot backend and MySQL-compatible database on a server/cloud platform. GitHub can still host the source code and, if desired, a separate static marketing/front-end site.

## GitHub repository

Recommended repository structure:

```text
/src                Spring Boot application
/README-GREENWOOD.md
/.gitignore
```

Do not commit database passwords, production credentials, or private student data.

## Roles

- **ADMIN**: school-wide administration, users, students, lecturers, courses, system settings and academic operations.
- **LECTURER**: teaching workflows such as attendance and marks, subject to application permissions.
- **STUDENT**: personal learning records, attendance, marks and transcript views.

Server-side authorization is enforced with Spring Security; hiding a menu item is not the security boundary.
