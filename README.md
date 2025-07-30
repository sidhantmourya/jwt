# JWT Authentication Microservice with Spring Boot

This project is a standalone microservice for JWT (JSON Web Token) authentication using Spring Boot. It provides a secure way to manage user authentication and authorization that can be integrated into a larger microservices architecture. The application includes endpoints for user registration, login, token validation, token refreshing, and logout.

## Features

-   User registration and login
-   JWT-based authentication
-   Access and refresh token generation
-   Token validation and refreshing
-   Secure logout mechanism
-   Role-based authorization (can be extended)
-   Integration with MySQL for user data and Redis for refresh token storage
-   Containerized setup with Docker and Docker Compose

## Prerequisites

-   Java 21
-   Maven 3.x
-   Docker and Docker Compose

## Getting Started

### Using Docker Compose

The easiest way to get the application running is by using Docker Compose. This will set up the Spring Boot application, a MySQL database, and a Redis instance.

1.  **Build the application:**
    ```bash
    mvn clean install
    ```

2.  **Run with Docker Compose:**
    ```bash
    docker-compose -f deploy/docker-compose.yml up --build
    ```

The application will be available at `http://localhost:8081`.

### Running Locally

To run the application locally without Docker, you need to have MySQL and Redis running on your machine.

1.  **Configure `application.yml`:**
    Update the `src/main/resources/application.yml` file with your local MySQL and Redis connection details.

2.  **Run the application:**
    ```bash
    mvn spring-boot:run
    ```

## API Endpoints

All endpoints are available under the `/auth` path.

| Endpoint        | Method | Description                               | Request Body                |
| --------------- | ------ | ----------------------------------------- | --------------------------- |
| `/register`     | POST   | Register a new user                       | `RegisterRequestDTO`        |
| `/login`        | POST   | Authenticate a user and get tokens        | `RequestLoginDTO`           |
| `/me`           | GET    | Get the details of the authenticated user | -                           |
| `/validate`     | POST   | Validate the access token                 | -                           |
| `/refresh`      | POST   | Get a new access token using a refresh token | -                          |
| `/logout`       | POST   | Log out the user                          | -                           |

## Configuration

The application uses the following environment variables for configuration:

| Variable                     | Description                            |
| ---------------------------- | -------------------------------------- |
| `SPRING_DATASOURCE_URL`      | The JDBC URL for the MySQL database.   |
| `SPRING_DATASOURCE_USERNAME` | The username for the MySQL database.   |
| `SPRING_DATASOURCE_PASSWORD` | The password for the MySQL database.   |
| `SPRING_REDIS_HOST`          | The hostname of the Redis server.      |

These can be set in the `docker-compose.yml` file or as environment variables when running locally.

## Future Improvements

-   **Kubernetes Deployment:** The service is designed to be scalable and can be deployed to a Kubernetes cluster for better management and orchestration in a production environment.

## Dependencies

-   Spring Boot
-   Spring Security
-   Spring Data JPA
-   Spring Data Redis
-   MySQL Connector
-   JJWT (Java JWT)

