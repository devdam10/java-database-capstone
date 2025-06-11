# 📊 Smart Clinic System – Schema Design

This document defines the **MySQL** and **MongoDB** schema designs for the Smart Clinic System. The hybrid design leverages structured relational data for core operations (e.g., users, appointments), while utilizing flexible NoSQL for unstructured or evolving data (e.g., prescriptions, logs, messages).

---

## 🗃️ MySQL Database Design

MySQL stores structured, validated, and interrelated data.

### 🧩 Table: users

```sql
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone_number VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('patient', 'doctor', 'admin') NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### 👨‍⚕️ Table: doctors

```sql
CREATE TABLE doctors (
    id INT PRIMARY KEY,
    specialization VARCHAR(100),
    bio TEXT,
    location VARCHAR(100),
    FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);
```

### 🧑‍💼 Table: patients

```sql
CREATE TABLE patients (
    id INT PRIMARY KEY,
    date_of_birth DATE,
    gender ENUM('male', 'female', 'other'),
    medical_history TEXT,
    FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);
```

### 👨‍💻 Table: admins

```sql
CREATE TABLE admins (
    id INT PRIMARY KEY,
    staff_code VARCHAR(50) UNIQUE,
    FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);
```

### 📅 Table: appointments

```sql
CREATE TABLE appointments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    doctor_id INT NOT NULL,
    patient_id INT NOT NULL,
    appointment_time DATETIME NOT NULL,
    duration_minutes INT DEFAULT 60,
    status ENUM('scheduled', 'completed', 'cancelled') DEFAULT 'scheduled',
    notes TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    UNIQUE(doctor_id, appointment_time)
);
```

### 🏥 Table: clinic_locations

```sql
CREATE TABLE clinic_locations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    phone VARCHAR(20)
);
```

### 💳 Table: payments

```sql
CREATE TABLE payments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    method ENUM('credit_card', 'cash', 'insurance'),
    status ENUM('pending', 'paid', 'failed') DEFAULT 'pending',
    paid_at DATETIME,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id)
);
```

---

## 🍃 MongoDB Collection Design

MongoDB is used to store flexible or unstructured data like prescriptions, logs, and messages.

### 📄 Collection: prescriptions

```json
{
  "_id": "ObjectId('6641efc31ab3e6f7c1234567')",
  "appointmentId": 42,
  "patientId": 17,
  "doctorId": 3,
  "issuedAt": "2025-06-10T09:00:00Z",
  "medications": [
    {
      "name": "Amoxicillin",
      "dosage": "250mg",
      "frequency": "3 times a day",
      "duration_days": 7
    },
    {
      "name": "Ibuprofen",
      "dosage": "200mg",
      "frequency": "as needed",
      "duration_days": 5
    }
  ],
  "doctorNotes": "Monitor for side effects and report any unusual symptoms.",
  "pharmacy": {
    "name": "Main Street Pharmacy",
    "location": "123 Main St, Kingston"
  },
  "refillAvailable": false
}
```

### 📜 Collection: logs

```json
{
  "_id": "ObjectId('6641f123987abcdef123456')",
  "userId": 17,
  "role": "patient",
  "action": "logged_in",
  "timestamp": "2025-06-10T08:30:00Z",
  "metadata": {
    "ip": "192.168.1.25",
    "device": "Chrome on macOS"
  }
}
```

### 💬 Collection: messages

```json
{
  "_id": "ObjectId('6641f999888aaa777bbb333')",
  "senderId": 17,
  "receiverId": 3,
  "senderRole": "patient",
  "receiverRole": "doctor",
  "sentAt": "2025-06-10T10:15:00Z",
  "messageText": "Hello Doctor, I have a question about my medication.",
  "attachments": [
    {
      "fileName": "lab-results.pdf",
      "fileType": "application/pdf",
      "url": "https://example.com/files/lab-results.pdf"
    }
  ],
  "read": false,
  "tags": ["urgent", "medication"]
}
```

---

## 💡 Design Considerations

- **TPT (Table-Per-Type)**: All user roles derive from `users`, avoiding data duplication.
- **Foreign Keys**: Maintain data integrity and cascade deletes where appropriate.
- **Appointments**: Prevent overlapping appointments with unique constraint.
- **NoSQL Use**: Prescriptions, logs, and messages have flexible and evolving structures, making MongoDB ideal.
- **Messages**: Stored as documents with metadata, attachments, and tags to allow rich communication.
- **Scalability**: This hybrid schema supports both structured operations and evolving clinical data.
