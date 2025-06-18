// Import necessary modules
import { getPatientAppointments } from "./services/patientServices.js";
import { createPatientRecordRow, createPatientRecordRow2 } from "./components/patientRecordRow.js";

// DOM references
const tableBody = document.getElementById("patientTableBody");
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
        const appointments = await getPatientAppointments(patientId, doctorId, token);
        renderAppointments(appointments);
    }
    catch (error) {
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
        const row = createPatientRecordRow2(appointment);
        tableBody.appendChild(row);
    });
}
