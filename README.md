# AI-Powered Job Portal Backend

A production-level robust backend for a modern Job Portal. Built with Java 21, Spring Boot 3, Spring Security (JWT), Spring Data JPA, MySQL, and OpenAPI (Swagger).

## Features
- **Role-based Authentication:** Secure JWT-based authentication for `CANDIDATE`, `RECRUITER`, and `ADMIN`.
- **Job Management:** Recruiters can post, update, and manage job listings.
- **Application Tracking:** Candidates can apply to jobs and track their applications. Recruiters can view and manage applicant statuses.
- **Smart Recommendations:** Suggests jobs to candidates based on matching skills.
- **File Upload:** Handles secure upload and storage of candidate resumes.
- **API Documentation:** Interactive Swagger UI for API exploration and testing.

## Technologies Used
- Java 21
- Spring Boot 3.2.4
- Spring Security & JWT
- Spring Data JPA (Hibernate)
- MySQL Database
- Lombok
- SpringDoc OpenAPI

## Prerequisites
- Java 21 installed
- Maven installed
- MySQL Server installed and running

## Setup Instructions

1. **Database Configuration**
   - Create a database in MySQL named `job_portal_db`.
   - Update `src/main/resources/application.properties` with your MySQL username and password:
     ```properties
     spring.datasource.username=root
     spring.datasource.password=password
     ```

2. **Build the Project**
   Navigate to the project root directory and run:
   ```bash
   mvn clean install
   ```

3. **Run the Application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access Swagger UI**
   Once the application is running, open your browser and navigate to:
   ```
   http://localhost:8080/swagger-ui.html
   ```

## Sample API Flow

1. **Register a Recruiter:**
   `POST /api/auth/signup`
   ```json
   {
     "email": "recruiter@company.com",
     "password": "password123",
     "role": "ROLE_RECRUITER",
     "firstName": "John",
     "lastName": "Doe",
     "companyName": "TechCorp"
   }
   ```

2. **Login:**
   `POST /api/auth/login`
   ```json
   {
     "email": "recruiter@company.com",
     "password": "password123"
   }
   ```
   *(Copy the JWT token from the response)*

3. **Post a Job (Requires Recruiter JWT):**
   `POST /api/jobs`
   ```json
   {
     "title": "Software Engineer",
     "description": "We are looking for a Java Developer...",
     "requirements": "3+ years of experience with Spring Boot",
     "location": "Remote",
     "salary": "$100k - $120k",
     "experienceLevel": "Mid-Level",
     "employmentType": "FULL_TIME",
     "skills": ["java", "spring boot", "mysql"]
   }
   ```

4. **Register a Candidate:**
   `POST /api/auth/signup`
   *(Role: `ROLE_CANDIDATE`, omit `companyName`)*

5. **Apply for Job (Requires Candidate JWT):**
   `POST /api/applications`
   ```json
   {
     "jobId": 1,
     "coverLetter": "I am very interested in this position..."
   }
   ```
