# RecordHub

RecordHub is a Spring Boot web application for managing songs, playlists, and simple music sharing between users. The application combines a traditional Thymeleaf web interface with Spring Security authentication, PostgreSQL persistence, password reset by email, and a small REST API.

The project was built as a course project with a wider scope than a basic CRUD application. In addition to song management, it includes user registration, login, friendships, playlist sharing, REST endpoints, and deployment configuration for Railway.

Live site: https://web-production-179d0.up.railway.app/login

GitHub repository: https://github.com/LilaFej/RecordHub.git

## Purpose

RecordHub solves a practical music organization problem: keeping songs in one place, grouping them into playlists, and allowing users to interact with other users through a lightweight friend system. Instead of a plain list of songs, the application gives users a small music platform where they can:

- create an account and log in securely
- manage songs and assign them to playlists
- search songs by genre and playlist
- send friend requests and view friends' playlists
- reset passwords through an email-based token flow
- access songs through REST endpoints as JSON

## Features

### Authentication and Security

- Spring Security form login
- Custom login page with Thymeleaf
- User registration with validation
- BCrypt password hashing
- Role support for `USER` and `ADMIN`
- Password policy requiring at least 8 characters and 1 uppercase letter
- Password reset flow using email tokens

### Song and Playlist Management

- Add, edit, delete, and list songs
- Assign songs to playlists
- Search songs by genre and playlist
- Playlist ownership checks so users can only attach songs to their own playlists
- Starter seed data for a fresh database

### Social Features

- Send friend requests
- Accept friend requests
- View accepted friends
- Open a friend's shared playlists

### REST API

- `GET /api/songs` returns all songs
- `GET /api/songs/{id}` returns one song by id

### Deployment and Database

- PostgreSQL configuration for production
- Environment variable based configuration for Railway
- Procfile included for deployment
- Public cloud deployment on Railway

## Advanced Spring Boot Features

These parts were learned and implemented beyond the most basic Spring Boot course material:

- Spring Security with custom login flow
- Password reset token workflow using email
- PostgreSQL deployment with environment variables
- REST controller alongside MVC controllers
- Railway deployment setup with Procfile
- Friend system with JPA relationships and status handling

## Tech Stack

- Java 17
- Spring Boot 4.0.5
- Spring MVC
- Thymeleaf
- Spring Data JPA
- Spring Security
- PostgreSQL
- Hibernate
- Maven
- Railway
- Mailtrap SMTP for testing password reset emails

## Project Structure

```text
src/main/java/fi/recordhub/
├── config/       # Security and shared MVC configuration
├── controller/   # MVC and REST controllers
├── model/        # JPA entities and form models
├── repository/   # Spring Data JPA repositories
├── service/      # Business logic and helpers
└── RecordhubApplication.java

src/main/resources/
├── templates/    # Thymeleaf pages
└── application.properties

src/test/java/fi/recordhub/
├── controller/   # REST tests
├── repository/   # Repository tests
└── RecordhubApplicationTests.java
```

## Main Pages

- `/login` - login page
- `/register` - create a new account
- `/songlist` - song list with search filters
- `/addsong` - add a new song
- `/friends/list` - manage friendships
- `/reset/request` - request a password reset link

## Database and Deployment Configuration

The project is configured for PostgreSQL in production. Railway environment variables are used for the database connection and mail settings.

Example production configuration:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update
server.port=${PORT:8080}
```

The deployment also uses:

```text
web: java -Dserver.port=$PORT -jar target/recordhub-0.0.1-SNAPSHOT.jar
```

## Testing

The project contains multiple test cases, including:

- application context loading test
- REST tests for fetching songs
- repository tests for creating, deleting, and searching songs
- repository tests for creating, deleting, and searching playlists

Run tests with:

```bash
./mvnw test
```

On Windows PowerShell:

```powershell
.\mvnw.cmd test
```

Build the project with:

```bash
./mvnw clean package
```

On Windows PowerShell:

```powershell
.\mvnw.cmd clean package
```

## Demo Accounts

The seeded admin account is:

- Username: `admin`
- Password: `AdminOnly`

Users can also create their own accounts from the registration page.

## Notes

- Password reset email delivery is configured through SMTP environment variables.
- Mailtrap is suitable for testing but not intended as a permanent production mail solution.
- The project is deployed to Railway with PostgreSQL, which satisfies the requirement for a real cloud database.

## Author

- LilaFej
- GitHub: https://github.com/LilaFej/RecordHub