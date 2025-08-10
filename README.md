# JWT Authentication Microservice with Spring Boot

This project is a production-ready standalone microservice for JWT (JSON Web Token) authentication using Spring Boot. It provides a secure, scalable way to manage user authentication and authorization that can be integrated into a larger microservices architecture. The application includes comprehensive security features, structured logging, and monitoring capabilities.

## Features

### 🔐 Core Authentication (Implemented)
-   User registration and login with secure password handling
-   JWT-based authentication with access token generation
-   Role-based authorization (extensible)
-   User profile management
-   Basic logout functionality

### 📊 Logging & Monitoring (Implemented)
-   **Structured JSON logging** with Logstash encoder for production environments
-   **Security event logging** with dedicated security.log file
-   **Audit trail logging** with comprehensive audit.log for compliance
-   **Request correlation** with unique trace IDs for request tracking
-   **Client IP detection** with support for proxy headers (X-Forwarded-For, CF-Connecting-IP)
-   **Performance monitoring** with request duration tracking

### 🛡️ Security Features (Implemented)
-   Multiple security filter chains for different authentication requirements
-   Comprehensive security event logging (authentication attempts, token validation, etc.)
-   Suspicious activity detection and logging
-   IP-based request tracking and monitoring

### 🏗️ Infrastructure (Implemented)
-   Integration with MySQL for user data persistence
-   Containerized setup with Docker and Docker Compose
-   Health checks for all services
-   Production-ready configuration with Spring profiles

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

### Currently Implemented Endpoints

| Endpoint        | Method | Description                               | Request Body                |
| --------------- | ------ | ----------------------------------------- | --------------------------- |
| `/register`     | POST   | Register a new user                       | `RegisterRequestDTO`        |
| `/login`        | POST   | Authenticate a user and get access token | `RequestLoginDTO`           |
| `/me`           | GET    | Get the details of the authenticated user | -                           |
| `/logout`       | POST   | Log out the user (basic implementation)   | `RequestLogoutDTO`          |

### Future Enhancement Endpoints (Not Yet Implemented)

| Endpoint        | Method | Description                               | Status                      |
| --------------- | ------ | ----------------------------------------- | --------------------------- |
| `/validate`     | POST   | Validate the access token                 | **Planned** - Future enhancement |
| `/refresh`      | POST   | Get a new access token using refresh token | **Planned** - Future enhancement |

> **Note:** The `/validate` and `/refresh` endpoints are reserved for future implementation.

## Configuration

The application uses the following environment variables for configuration:

| Variable                     | Description                            |
| ---------------------------- | -------------------------------------- |
| `SPRING_DATASOURCE_URL`      | The JDBC URL for the MySQL database.   |
| `SPRING_DATASOURCE_USERNAME` | The username for the MySQL database.   |
| `SPRING_DATASOURCE_PASSWORD` | The password for the MySQL database.   |
| `SPRING_REDIS_HOST`          | The hostname of the Redis server.      |

These can be set in the `docker-compose.yml` file or as environment variables when running locally.

## Logging Structure

The application implements comprehensive structured logging with multiple log files for different purposes:

### Log Files
- **`jwt-service.log`** - General application logs with request/response information
- **`security.log`** - Security-related events such as authentication attempts, token validation, etc.
- **`audit.log`** - Business audit trail for compliance and monitoring

### Log Format
- **Development:** Colored console output with readable formatting
- **Production:** Structured JSON logs with correlation IDs for log aggregation systems

### Key Logging Features
- **Trace ID correlation** for tracking requests across the application
- **MDC (Mapped Diagnostic Context)** for contextual information
- **Async logging** for improved performance
- **Automatic log rotation** with size and time-based policies
- **Configurable log levels** per environment

## Future Enhancements

### 🔄 Token Management
-   **Refresh Token Implementation:** Complete implementation of the `/refresh` endpoint with secure refresh token rotation
-   **Token Validation Service:** Dedicated `/validate` endpoint for token verification by other microservices
-   ~~**Token Blacklisting:** Integration with Redis for maintaining invalidated token lists~~ **Implemented**

~~### 🗄️ Redis Integration~~**Implemented**
~~-   **Enhanced Logout Functionality:** Redis-backed token invalidation for immediate logout across all sessions~~
~~-   **Session Management:** Redis-based session storage for improved scalability~~
~~-   **Rate Limiting:** Redis-powered rate limiting for API endpoints~~

### ☸️ Kubernetes Deployment
-   **Helm Charts:** Ready-to-deploy Kubernetes manifests with Helm chart support
-   **Auto-scaling:** Horizontal Pod Autoscaler (HPA) configuration
-   **Service Mesh Integration:** Istio/Envoy integration for advanced traffic management
-   **Config Management:** Kubernetes ConfigMaps and Secrets integration

~~### 🔍 Advanced Monitoring~~ (**Implemented**)
~~-   **Log Aggregation:** ELK Stack (Elasticsearch, Logstash, Kibana) integration~~

## Dependencies

-   Spring Boot
-   Spring Security
-   Spring Data JPA
-   Spring Data Redis
-   MySQL Connector
-   JJWT (Java JWT)

