# 📊 Smart Clinic System – Schema Design

This document defines the **MySQL** and **MongoDB** schema designs for the Smart Clinic System. The hybrid design leverages structured relational data for core operations (e.g., users, appointments), while utilizing flexible NoSQL for unstructured or evolving data (e.g., prescriptions).

---

## 🗃️ MySQL Database Design

MySQL stores structured, validated, and interrelated data.


### 👨‍⚕️ Table: doctors

```sql
CREATE TABLE doctor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialty VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(10) NOT NULL
);
```

### 🧩 Table: doctor's available times
```sql
CREATE TABLE doctor_available_times (
    doctor_id BIGINT NOT NULL,
    available_times VARCHAR(255),
    CONSTRAINT fk_doctor FOREIGN KEY (doctor_id) REFERENCES doctor(id) ON DELETE CASCADE
);
```


### 🧑‍💼 Table: patients

```sql
CREATE TABLE patient (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone CHAR(10) NOT NULL,
    address VARCHAR(255) NOT NULL
);
```

### 👨‍💻 Table: admins

```sql
CREATE TABLE admin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);
```

### 📅 Table: appointments

```sql
CREATE TABLE appointment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    doctor_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    appointment_time DATETIME,
    status INT NOT NULL,
    CONSTRAINT fk_appointment_doctor FOREIGN KEY (doctor_id) REFERENCES doctor(id) ON DELETE CASCADE,
    CONSTRAINT fk_appointment_patient FOREIGN KEY (patient_id) REFERENCES patient(id) ON DELETE CASCADE
);
```

---

## 🍃 MongoDB Collection Design

MongoDB is used to store flexible or unstructured data like prescriptions, logs, and messages.

### 📄 Collection: prescriptions

```json
{
  "_id": "663f91fc8dfe902e5f5a5d90",
  "patientName": "John Doe",
  "appointmentId": 12345,
  "medication": "Amoxicillin",
  "dosage": "500mg twice a day",
  "doctorNotes": "Take after meals. Follow up in 7 days."
}

```

---
