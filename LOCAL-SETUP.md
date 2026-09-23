# Local Windows setup — Greenwood Eco School

1. Install Java 17+.
2. Install XAMPP or WAMP.
3. Start **MySQL/MariaDB** in the XAMPP/WAMP control panel. Apache is not required for the Spring Boot application itself.
4. Install Maven and make sure `mvn -version` works in Command Prompt.
5. Open Command Prompt in this project folder.
6. Run `run-local.bat`, or manually run `mvn spring-boot:run` from the project root (the folder containing `pom.xml`).
7. Open `http://localhost:8080/login`.

### First login

Username: `admin`

Temporary password: `Greenwood@12345`

Change it immediately when prompted.

### If MySQL has a password

Set this before starting the application:

```bat
set GREENWOOD_DB_PASSWORD=YOUR_MYSQL_PASSWORD
```

### If MySQL uses another port

Set the complete JDBC URL, for example:

```bat
set GREENWOOD_DB_URL=jdbc:mysql://localhost:3307/SmartCampus?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
```

### phpMyAdmin

If you prefer to create the database manually, import `src/db/schema.sql` in phpMyAdmin. It only creates/selects the `SmartCampus` database. Then start the Spring Boot app; Flyway creates the tables and applies versioned migrations automatically.
