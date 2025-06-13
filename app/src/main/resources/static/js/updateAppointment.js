/* 
 Function Overview:
   - `initializePage()`: Main function that initializes the page by fetching appointment and doctor details and populating the form for the update.
   - `updateAppointment()`: Updates the appointment details by sending the modified appointment data to the server.
   - `getDoctors()`: Fetches available doctors to populate the available times for the selected doctor.

 Key Variables:
   - `token`: Token stored in `localStorage` for user authentication. It's required to validate the user's session and permissions.
   - `appointmentId`, `patientId`, `doctorId`, `patientName`, `doctorName`, `appointmentDate`, `appointmentTime`: All are extracted from the URL parameters. They provide necessary data to pre-fill the form and allow for appointment updates.
   - `doctor`: The doctor object retrieved from the list of doctors. It contains the available times for scheduling.
   
 Page Initialization (`initializePage()`):
   - The page first checks if the `token` and `patientId` are available in `localStorage` or the URL query parameters.
   - If the `token` or `patientId` is missing, the user is redirected to the patient appointments page (`patientAppointments.html`).
   
 Fetch Doctors:
   - `getDoctors()` is called to fetch all doctors. This list helps populate the available times for the selected doctor.
   - The selected doctor (by `doctorId` from the URL) is found in the list, and if found, their available times are displayed as options in the form dropdown (`appointmentTime`).
   - If no doctor is found, an error message is shown.

 Pre-fill Appointment Details:
   - The form is pre-filled with the `patientName`, `doctorName`, `appointmentDate`, and `appointmentTime` from the URL.
   - Additionally, the available times for the selected doctor are dynamically added to the time selection dropdown.

 Handle Form Submission:
   - The form submission (`updateAppointmentForm`) is handled to prevent the default submission behavior using `e.preventDefault()`.
   - The updated appointment data is compiled, including the selected date and time.
   - If both the `appointmentDate` and `appointmentTime` are provided, an update request is sent to the server using the `updateAppointment()` function.
   - If the update is successful, the user is redirected back to the patient appointments page.
   - If the update fails, an error message is shown, explaining the failure.

 Error Handling:
   - If any errors occur while fetching the doctor list (`getDoctors()`), an error message is logged, and an alert is shown to the user.
   - If the form submission is unsuccessful (either due to missing data or server failure), the user is informed via an alert.

 Redirection and Flow:
   - If the appointment update is successful, the user is redirected to the patient appointments page.
   - If the session data (`token` or `patientId`) is missing, the user is redirected to the patient appointments page as a fallback to ensure they can re-authenticate.

 Purpose:
   - This script is used on the page that allows patients to update their existing appointments with a doctor. It ensures the correct data is pre-populated, the form is validated, and the update process is properly handled.

*/

import { getDoctors, updateAppointment } from "./appointmentService.js";

const urlParams = new URLSearchParams(window.location.search);
const token = localStorage.getItem("token");

const appointmentId = urlParams.get("appointmentId");
const patientId = urlParams.get("patientId") || localStorage.getItem("patientId");
const doctorId = urlParams.get("doctorId");
const patientName = urlParams.get("patientName");
const doctorName = urlParams.get("doctorName");
const appointmentDate = urlParams.get("date");
const appointmentTime = urlParams.get("time");

const appointmentDateInput = document.getElementById("appointmentDate");
const appointmentTimeSelect = document.getElementById("appointmentTime");
const patientNameInput = document.getElementById("patientName");
const doctorNameInput = document.getElementById("doctorName");
const updateForm = document.getElementById("updateAppointmentForm");
const errorMsg = document.getElementById("errorMessage");

async function initializePage() {
    if (!token || !patientId) {
        window.location.href = "patientAppointments.html";
        return;
    }

    patientNameInput.value = patientName || "";
    doctorNameInput.value = doctorName || "";
    appointmentDateInput.value = appointmentDate || "";

    try {
        const doctors = await getDoctors(token);
        const doctor = doctors.find((doc) => doc.id === doctorId);

        if (!doctor) {
            alert("Selected doctor not found.");
            return;
        }

        // Populate available times for selected doctor
        appointmentTimeSelect.innerHTML = ""; // Clear existing options
        doctor.availableTimes.forEach((time) => {
            const option = document.createElement("option");
            option.value = time;
            option.textContent = time;
            if (time === appointmentTime) option.selected = true;
            appointmentTimeSelect.appendChild(option);
        });
    } catch (error) {
        console.error("Failed to load doctors:", error);
        alert("Failed to load doctor information. Please try again later.");
    }
}

updateForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    const updatedDate = appointmentDateInput.value;
    const updatedTime = appointmentTimeSelect.value;

    if (!updatedDate || !updatedTime) {
        alert("Please select both appointment date and time.");
        return;
    }

    const updatedAppointment = {
        appointmentId,
        patientId,
        doctorId,
        date: updatedDate,
        time: updatedTime,
    };

    try {
        const response = await updateAppointment(updatedAppointment, token);
        if (response.success) {
            alert("Appointment updated successfully.");
            window.location.href = "patientAppointments.html";
        } else {
            alert(`Update failed: ${response.message || "Unknown error"}`);
        }
    } catch (error) {
        console.error("Error updating appointment:", error);
        alert("An error occurred while updating the appointment. Please try again.");
    }
});

// Initialize page on DOM load
document.addEventListener("DOMContentLoaded", initializePage);
