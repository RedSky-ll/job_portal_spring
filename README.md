# Job Portal API

This is a comprehensive Job Portal backend application built with Java and the Spring Boot framework. It provides a full suite of features for job seekers, employers, and administrators, delivered through a RESTful API.

## Key Features

- **User Authentication:** Secure user registration and login using JWT (JSON Web Tokens).
- **OAuth2 Integration:** Supports social login with Google.
- **Role-Based Access Control:** Differentiated permissions for Job Seekers, Employers, and Admins.
- **Profile Management:** Users can create and manage their profiles, including uploading avatars and resumes.
- **Job Management:** Employers can create, update, view, and delete job postings.
- **Job Search & Application:** Job seekers can search for available jobs and submit their applications.
- **Application Tracking:** Employers can view and manage applications for their job postings.
- **Real-time Notifications:** WebSocket integration for instant notifications.
- **Email Service:** Integrated mail service for notifications like password resets.
- **File Storage:** Handles uploads for user avatars and resumes.

## Tech Stack

- **Framework:** Spring Boot 3.5.5
- **Language:** Java 21
- **Database:** MySQL & H2 (for testing/dev)
- **Data Access:** Spring Data JPA with Hibernate
- **Security:** Spring Security, JWT, OAuth2
- **Real-time:** Spring WebSocket
- **Caching:** Spring Data Redis
- **Build Tool:** Apache Maven
- **Libraries:**
  - Lombok for reducing boilerplate code.
  - Thumbnailator for image thumbnail generation.
  - MapStruct for bean mapping.

## Prerequisites

Before you begin, ensure you have the following installed:
- **JDK 21** or later
- **Apache Maven**
- **MySQL Server**
- **Redis Server**

## Getting Started

Follow these instructions to get the project up and running on your local machine.

### 1. Clone the Repository

```bash
git clone <your-repository-url>
cd job-portal
```

### 2. Configure the Database

1.  Log in to your MySQL server and create a new database.
    ```sql
    CREATE DATABASE test;
    ```
2.  Open the main configuration file located at `src/main/resources/application.yaml`.
3.  Update the `spring.datasource` properties with your MySQL username and password.

    ```yaml
    spring:
      datasource:
        url: jdbc:mysql://localhost:3306/test?useSSL=false&serverTimezone=UTC
        username: <your-mysql-username>
        password: <your-mysql-password>
    ```

### 3. Configure Environment Variables (Recommended)

It is highly recommended to externalize sensitive information like database passwords, email credentials, and JWT secrets instead of hardcoding them in `application.yaml`. You can use environment variables or a `application-local.yaml` file for this.

Key properties to configure:
- `spring.datasource.username`
- `spring.datasource.password`
- `spring.mail.password`
- `spring.security.oauth2.client.registration.google.client-secret`
- `jwt.secret`

### 4. Run the Application

You can run the application using the Maven wrapper:

```bash
./mvnw spring-boot:run
```

The application will start on port `8443` with SSL enabled by default. You can access it at `https://localhost:8443`.

## API Endpoints

The API controllers are located in the following packages:
- `ir.parsakav.jobportal.accounts.api`
- `ir.parsakav.jobportal.admin.api`
- `ir.parsakav.jobportal.auth.api`
- `ir.parsakav.jobportal.jobs.api`

Explore these packages to see the available endpoints for different functionalities.

## License

This project is proprietary. All rights reserved.
