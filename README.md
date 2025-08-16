# HDB Pilot Server

A Spring Boot backend service for HDB property management system.

## Tech Stack

- **Framework**: Spring Boot 2.7.18
- **Database**: MySQL 8.0+
- **ORM**: MyBatis-Plus 3.5.2
- **Build Tool**: Maven
- **Java Version**: JDK 21
- **Cloud Storage**: AWS S3

## Prerequisites

- JDK 21
- MySQL 8.0+
- Maven 3.6+
- AWS S3 account (for image uploads)

## Local Development Setup

### 1. Clone and Setup

```bash
git clone <repository-url>
cd hdbPilot-server
```

### 2. Database Configuration

Create a MySQL database named `hdbPilot` and update the database configuration:

```bash
# Copy dev config for local development
cp src/main/resources/application-dev.yml src/main/resources/application-local.yml
```

Edit `src/main/resources/application-local.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hdbPilot?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: your_username
    password: your_password
```

### 3. Environment Variables

Set the following environment variables or create a `.env` file:

```bash
# Database
DB_URL=jdbc:mysql://localhost:3306/hdbPilot?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USERNAME=your_username
DB_PASSWORD=your_password

# AWS S3 (for image uploads)
AWS_ACCESS_KEY=your_access_key
AWS_SECRET_KEY=your_secret_key
AWS_S3_BUCKET=hdb-pilot
AWS_REGION=ap-southeast-1

# Server
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=local
```

### 4. Initialize Database

Run the SQL script to create database tables:
```bash
mysql -u your_username -p hdbPilot < src/main/resources/sql/scheme.sql
```

### 5. Start the Server

```bash
# Using Maven wrapper
./mvnw spring-boot:run

# Or using Maven directly
mvn spring-boot:run
```

The server will start on `http://localhost:8080`

## API Documentation

Once the server is running, you can access:

- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## Project Structure

```
src/main/java/com/iss/hdbPilot/
├── controller/          # REST API controllers
├── service/            # Business logic layer
├── mapper/             # Data access layer
├── model/              # DTOs, entities, and VOs
├── config/             # Configuration classes
├── common/             # Common utilities
└── exception/          # Exception handling
```

## Features

- User authentication and authorization
- Property listing and management
- Image upload to AWS S3
- Property search and filtering
- Admin review system for properties
- Dashboard statistics

## Development

The project uses Spring Boot with MyBatis-Plus for database operations. All API responses follow a standardized format through `BaseResponse<T>` wrapper.

For detailed development guidelines and API specifications, refer to the Swagger documentation when the server is running.
