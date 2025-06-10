# Smart Clinic Management System – Schema & Architecture

## Section 1: Architecture Summary

This Spring Boot–based system combines both **MVC** and **RESTful** design patterns to handle different types of user interactions. 

- **Thymeleaf** is used to build the web dashboards for Admin and Doctor users, delivering dynamic HTML pages.
- **REST APIs** power the remaining modules, supporting frontend clients such as patient-facing apps and external services.

The backend connects to two databases:
- **MySQL** handles structured, relational data like patients, doctors, appointments, and admin details using **JPA entities**.
- **MongoDB** stores prescription data in a more flexible, document-based format using **MongoDB models**.

All incoming requests—whether from the web dashboards or REST APIs—are handled by their respective controllers (MVC or REST). These controllers pass the requests to a shared **service layer**, which performs the business logic and delegates persistence operations to the correct repository, based on whether the data belongs to MySQL or MongoDB.

---

## Section 2: Numbered Flow of Data and Control

1. **User accesses Admin Dashboard, Doctor Dashboard, or interacts with REST endpoints**  
   This could be a doctor checking appointments via the dashboard or a patient using a mobile app to book one.

2. **Request is routed to the appropriate controller (MVC or REST)**  
   Web interface requests go to Thymeleaf controllers, while API calls go to REST controllers.

3. **Controller forwards the request to the common service layer**  
   The controller doesn’t handle logic directly; it delegates the task to the service layer for processing.

4. **Service layer applies business logic or validation**  
   The service may handle rules like checking availability, formatting data, or handling errors.

5. **Service layer calls the appropriate repository**  
   Depending on the type of data:
   - For structured data (appointments, patients, etc.), it uses **JPA repositories** for MySQL.
   - For prescriptions, it uses **Mongo repositories**.

6. **Repository queries the database and returns data to the service**  
   The database operation (create, read, update, delete) is performed and the result is returned.

7. **Service returns the result to the controller, which sends a response to the user**  
   The controller prepares a Thymeleaf HTML view or JSON response and delivers it back to the client.

---
