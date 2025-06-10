# User Story Template

**Title:**
_As a [user role], I want [feature/goal], so that [reason]._

**Acceptance Criteria:**
1. [Criteria 1]
2. [Criteria 2]
3. [Criteria 3]

**Priority:** [High/Medium/Low]
**Story Points:** [Estimated Effort in Points]
**Notes:**
- [Additional information or edge cases]


---


## Admin User Stories

**Title:**
*As an admin, I want to log into the portal with my username and password, so that I can manage the platform securely.*

**Acceptance Criteria:**

1. Admin can access a login page.
2. Login requires valid username and password.
3. Admin is redirected to the dashboard after successful login.
4. Invalid login credentials show an appropriate error message.

**Priority:** High
**Story Points:** 3
**Notes:**

* Consider rate-limiting or CAPTCHA for brute-force protection.

---

**Title:**
*As an admin, I want to log out of the portal, so that I can protect system access.*

**Acceptance Criteria:**

1. Admin can click a logout button.
2. System ends the session and redirects to the login page.
3. Attempting to access protected routes after logout redirects to login.

**Priority:** High
**Story Points:** 2
**Notes:**

* Ensure session is fully invalidated on logout.

---

**Title:**
*As an admin, I want to add doctors to the portal, so that they can be available to users.*

**Acceptance Criteria:**

1. Admin can access a form to input doctor details.
2. On submission, doctor profile is created and visible in the system.
3. Form validations prevent submission of incomplete or invalid data.

**Priority:** High
**Story Points:** 5
**Notes:**

* Include fields like name, specialty, availability, and contact info.

---

**Title:**
*As an admin, I want to delete a doctor's profile from the portal, so that I can remove outdated or incorrect records.*

**Acceptance Criteria:**

1. Admin sees a list of doctors with a delete option.
2. System prompts for confirmation before deletion.
3. Deleted doctor profile is removed from user-facing listings.

**Priority:** Medium
**Story Points:** 3
**Notes:**

* Consider a soft-delete mechanism for audit purposes.

---

**Title:**
*As an admin, I want to run a stored procedure in the MySQL CLI to get the number of appointments per month, so that I can track usage statistics.*

**Acceptance Criteria:**

1. Admin has access to a guide or script for running the stored procedure.
2. Stored procedure returns a monthly breakdown of appointments.
3. Output includes month, year, and count of appointments.

**Priority:** Medium
**Story Points:** 2
**Notes:**

* Ensure the stored procedure is tested and secure.

---


## Patient User Stories

**Title:**
*As a patient, I want to view a list of doctors without logging in, so that I can explore options before registering.*

**Acceptance Criteria:**

1. Patients can access a public page listing available doctors.
2. List includes doctor names, specialties, and basic information.
3. No login is required to view this information.

**Priority:** High
**Story Points:** 3
**Notes:**

* Limit access to booking and profile details until user is logged in.

---

**Title:**
*As a patient, I want to sign up using my email and password, so that I can book appointments.*

**Acceptance Criteria:**

1. Patients can access a registration page.
2. Valid email and password are required to sign up.
3. System sends a confirmation email or displays success message upon successful registration.

**Priority:** High
**Story Points:** 3
**Notes:**

* Include basic input validation (email format, password strength, etc.).

---

**Title:**
*As a patient, I want to log into the portal, so that I can manage my bookings.*

**Acceptance Criteria:**

1. Patients can log in with their registered credentials.
2. Upon successful login, patients are directed to their dashboard.
3. Failed login attempts show error messages.

**Priority:** High
**Story Points:** 2
**Notes:**

* Consider "Remember Me" and "Forgot Password" options for user convenience.

---

**Title:**
*As a patient, I want to log out of the portal, so that I can secure my account.*

**Acceptance Criteria:**

1. Patients can log out with a single click.
2. The session is terminated, and the user is redirected to the homepage or login page.
3. Protected routes are inaccessible after logout.

**Priority:** High
**Story Points:** 1
**Notes:**

* Ensure proper session handling to prevent unauthorized access.

---

**Title:**
*As a patient, I want to log in and book an hour-long appointment to consult with a doctor, so that I can receive medical care.*

**Acceptance Criteria:**

1. Logged-in patients can view doctor availability.
2. Patient can select a time slot and confirm booking.
3. Booking confirmation is shown with appointment details.
4. Duration of appointment is set to one hour by default.

**Priority:** High
**Story Points:** 5
**Notes:**

* Check for time slot conflicts or double bookings before confirming.

---

**Title:**
*As a patient, I want to view my upcoming appointments, so that I can prepare accordingly.*

**Acceptance Criteria:**

1. Logged-in patients can access an "Upcoming Appointments" section.
2. List shows appointment date, time, and doctor name.
3. Option to cancel or reschedule (if implemented) is visible.

**Priority:** Medium
**Story Points:** 2
**Notes:**

* Sort upcoming appointments in chronological order.

---


## Doctor User Stories

**Title:**
*As a doctor, I want to log into the portal to manage my appointments.*

**Acceptance Criteria:**

1. Doctors can log in using valid credentials.
2. On successful login, doctor is redirected to their dashboard.
3. Login failure displays appropriate error messages.

**Priority:** High
**Story Points:** 2
**Notes:**

* Include multi-factor authentication for added security (optional enhancement).

---

**Title:**
*As a doctor, I want to log out of the portal, so that I can protect my data.*

**Acceptance Criteria:**

1. Doctor can log out via a visible logout button.
2. Session ends and user is redirected to the login or home page.
3. Protected pages are inaccessible post logout.

**Priority:** High
**Story Points:** 1
**Notes:**

* Ensure token/session is fully invalidated after logout.

---

**Title:**
*As a doctor, I want to view my appointment calendar, so that I can stay organized.*

**Acceptance Criteria:**

1. Doctor can view a calendar with scheduled appointments.
2. Each appointment entry displays patient name, time, and purpose.
3. Calendar is updated in real-time as appointments are added/removed.

**Priority:** High
**Story Points:** 4
**Notes:**

* Calendar should support daily/weekly/monthly views.

---

**Title:**
*As a doctor, I want to mark my unavailability, so that patients only see available slots.*

**Acceptance Criteria:**

1. Doctor can block specific time slots or days as unavailable.
2. Blocked times are hidden or disabled in the patient booking interface.
3. Doctor can modify or remove unavailable times.

**Priority:** High
**Story Points:** 4
**Notes:**

* Consider recurring unavailability (e.g., lunch breaks, holidays).

---

**Title:**
*As a doctor, I want to update my profile with specialization and contact information, so that patients have up-to-date information.*

**Acceptance Criteria:**

1. Doctor can access and edit profile details.
2. Updates are saved and reflected on patient-facing views.
3. Required fields (e.g., specialization) must be validated.

**Priority:** Medium
**Story Points:** 3
**Notes:**

* Include fields for profile image, clinic address, and phone number (optional).

---

**Title:**
*As a doctor, I want to view patient details for upcoming appointments, so that I can be prepared.*

**Acceptance Criteria:**

1. Doctor can view patient name, appointment reason, and contact info.
2. Patient details are accessible only for confirmed appointments.
3. Details are presented clearly and securely.

**Priority:** High
**Story Points:** 3
**Notes:**

* Ensure patient data is protected and access-controlled.

---





