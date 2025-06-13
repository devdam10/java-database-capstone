/* 
 Import modules:
   - getPatientAppointments: Fetches all appointments of a given patient.
   - createPatientRecordRow: Creates a table row for each appointment entry.

 DOM element references:
   - tableBody: The body of the table where patient appointments will be listed.
   - token: Retrieved from localStorage to authorize the API call.
   - URL parameters: Used to extract the specific `patientId` and `doctorId` to filter data accordingly.

 Page Initialization:
   - On DOMContentLoaded, call `initializePage()` to load and display data.

 Fetch appointments:
   - Ensure the user token exists.
   - Call `getPatientAppointments()` with the patient ID and token, specifying the user as `"doctor"` to access the correct backend logic.
   - Filter appointments further by checking if `doctorId` matches the one in the query string.

 Render Appointments:
   - Clear any previous content in the table.
   - Always make the "Actions" column visible in the table.
   - If no appointments exist, show a "No Appointments Found" message.
   - Otherwise, loop through filtered data and render each appointment row using `createPatientRecordRow()`.

 Error Handling:
   - All major operations are wrapped in try-catch blocks.
   - Errors are logged to the console for debugging.
   - Alerts are used to notify the user of failure to load data.

*/

// Import necessary modules
import { getPatientAppointments } from "../services/patientService.js";
import { createPatientRecordRow } from "../utils/tableRowFactory.js";

// DOM references
const tableBody = document.querySelector("#patientAppointmentsTable tbody");
const token = localStorage.getItem("token");

// Extract URL parameters for filtering
const urlParams = new URLSearchParams(window.location.search);
const patientId = urlParams.get("patientId");
const doctorId = urlParams.get("doctorId");

// Initialize page after DOM loaded
document.addEventListener("DOMContentLoaded", initializePage);

async function initializePage() {
    if (!token) {
        alert("Authentication token not found. Please log in.");
        return;
    }
    if (!patientId || !doctorId) {
        alert("Required parameters missing in URL.");
        return;
    }

    try {
        // Fetch appointments for the patient with role "doctor" for backend access
        const appointments = await getPatientAppointments(patientId, token, "doctor");

        // Filter by doctorId from URL
        const filteredAppointments = appointments.filter(
            (appt) => appt.doctorId === doctorId
        );

        renderAppointments(filteredAppointments);
    } catch (error) {
        console.error("Error loading patient appointments:", error);
        alert("Failed to load patient appointments. Please try again later.");
    }
}

function renderAppointments(appointments) {
    tableBody.innerHTML = ""; // Clear previous rows

    // Make sure the Actions column is visible (adjust if hidden by CSS)
    const actionsHeader = document.querySelector("th.actions");
    if (actionsHeader) actionsHeader.style.display = "";

    if (!appointments.length) {
        tableBody.innerHTML = `
      <tr>
        <td colspan="6" class="text-center text-gray-600">
          No Appointments Found.
        </td>
      </tr>`;
        return;
    }

    appointments.forEach((appointment) => {
        // createPatientRecordRow returns a <tr> element for each appointment
        const row = createPatientRecordRow(appointment);
        tableBody.appendChild(row);
    });
}
