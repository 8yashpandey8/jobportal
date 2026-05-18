# API Usage Guide - Step-by-Step Flow

This guide simulates exactly what it will look like when you interact with the backend using Postman, Swagger UI, or a Frontend Application. 

Follow this sequence to see the full capabilities of the Job Portal.

---

## 1. Register a Recruiter

**Request (POST `http://localhost:8080/api/auth/signup`)**
```json
{
  "email": "hr@google.com",
  "password": "password123",
  "role": "ROLE_RECRUITER",
  "firstName": "Sundar",
  "lastName": "Pichai",
  "companyName": "Google"
}
```

**Response (200 OK)**
```json
{
  "message": "User registered successfully!"
}
```

---

## 2. Login as Recruiter

**Request (POST `http://localhost:8080/api/auth/login`)**
```json
{
  "email": "hr@google.com",
  "password": "password123"
}
```

**Response (200 OK)**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoci... (this is your JWT token)",
  "id": 1,
  "email": "hr@google.com",
  "role": "ROLE_RECRUITER"
}
```
*(You will use this `token` in the `Authorization` header as `Bearer <token>` for recruiter actions).*

---

## 3. Recruiter Posts a Job

**Headers:** `Authorization: Bearer <recruiter_token>`
**Request (POST `http://localhost:8080/api/jobs`)**
```json
{
  "title": "Senior Java Developer",
  "description": "Looking for an experienced Java developer with Spring Boot skills.",
  "requirements": "5+ years of Java, Spring Boot, Microservices.",
  "location": "Remote",
  "salary": "$130,000",
  "experienceLevel": "Senior",
  "employmentType": "FULL_TIME",
  "skills": ["java", "spring boot", "microservices"]
}
```

**Response (200 OK)**
```json
{
  "id": 1,
  "title": "Senior Java Developer",
  "description": "Looking for an experienced Java developer with Spring Boot skills.",
  "requirements": "5+ years of Java, Spring Boot, Microservices.",
  "location": "Remote",
  "salary": "$130,000",
  "experienceLevel": "Senior",
  "employmentType": "FULL_TIME",
  "status": "OPEN",
  "recruiterName": "Sundar Pichai",
  "companyName": "Google",
  "skills": ["java", "microservices", "spring boot"],
  "postedAt": "2024-05-18T10:00:00"
}
```

---

## 4. Register a Candidate

**Request (POST `http://localhost:8080/api/auth/signup`)**
```json
{
  "email": "candidate@gmail.com",
  "password": "password123",
  "role": "ROLE_CANDIDATE",
  "firstName": "Alex",
  "lastName": "Smith"
}
```

**Response (200 OK)**
```json
{
  "message": "User registered successfully!"
}
```

---

## 5. Login as Candidate

**Request (POST `http://localhost:8080/api/auth/login`)**
```json
{
  "email": "candidate@gmail.com",
  "password": "password123"
}
```

**Response (200 OK)**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjYW5ka... (candidate JWT token)",
  "id": 2,
  "email": "candidate@gmail.com",
  "role": "ROLE_CANDIDATE"
}
```
*(You will use this `token` in the `Authorization` header as `Bearer <token>` for candidate actions).*

---

## 6. Candidate Views Recommended Jobs

**Headers:** `Authorization: Bearer <candidate_token>`
**Request (GET `http://localhost:8080/api/recommendations/jobs`)**

**Response (200 OK)**
*(Since the candidate has no skills listed yet, it will return all open jobs)*
```json
[
  {
    "id": 1,
    "title": "Senior Java Developer",
    "companyName": "Google",
    "location": "Remote",
    "salary": "$130,000",
    ...
  }
]
```

---

## 7. Candidate Applies for the Job

**Headers:** `Authorization: Bearer <candidate_token>`
**Request (POST `http://localhost:8080/api/applications`)**
```json
{
  "jobId": 1,
  "coverLetter": "I have 6 years of experience in Java and Spring Boot. I would love to join your team!"
}
```

**Response (200 OK)**
```json
{
  "id": 1,
  "jobId": 1,
  "jobTitle": "Senior Java Developer",
  "companyName": "hr@google.com",
  "candidateName": "Alex Smith",
  "candidateEmail": "candidate@gmail.com",
  "resumeUrl": null,
  "status": "APPLIED",
  "coverLetter": "I have 6 years of experience in Java and Spring Boot. I would love to join your team!",
  "appliedAt": "2024-05-18T10:15:00"
}
```

---

## 8. Recruiter Views Applications for the Job

**Headers:** `Authorization: Bearer <recruiter_token>`
**Request (GET `http://localhost:8080/api/applications/job/1`)**

**Response (200 OK)**
```json
[
  {
    "id": 1,
    "jobId": 1,
    "jobTitle": "Senior Java Developer",
    "companyName": "hr@google.com",
    "candidateName": "Alex Smith",
    "candidateEmail": "candidate@gmail.com",
    "resumeUrl": null,
    "status": "APPLIED",
    "coverLetter": "I have 6 years of experience in Java and Spring Boot. I would love to join your team!",
    "appliedAt": "2024-05-18T10:15:00"
  }
]
```

---

## 9. Recruiter Updates Application Status (e.g. Shortlisted)

**Headers:** `Authorization: Bearer <recruiter_token>`
**Request (PATCH `http://localhost:8080/api/applications/1/status?status=SHORTLISTED`)**

**Response (200 OK)**
```json
{
  "id": 1,
  "status": "SHORTLISTED",
  ... (other fields)
}
```

---

## Swagger UI
To interact with these endpoints directly from a webpage, run your application (`mvn spring-boot:run`) and go to:
**`http://localhost:8080/swagger-ui.html`**

You can click "Authorize" at the top of the Swagger page, paste your `token`, and then try out all the APIs listed above directly from the browser!
