/* 
 Import the necessary service functions:
   - getPatientAppointments: To fetch all appointments of a patient.
   - getPatientData: To retrieve patient profile data using a stored token.
   - filterAppointments: To filter the patient's appointments based on criteria.

 Initialize important variables:
   - tableBody: Reference the table body element where appointments will be displayed.
   - token: Retrieve the authentication token from localStorage.
   - allAppointments: Array to store all fetched appointment data.
   - filteredAppointments: Array to store filtered results when searching.
   - patientId: To hold the ID of the current patient once retrieved.

 Set up the page when it loads:
   - Wait for the DOM to fully load using the 'DOMContentLoaded' event.
   - Ensure a token exists; if not, stop the execution.
   - Use getPatientData() to fetch the current patient's details.
   - Store the returned patient ID for filtering.
   - Use getPatientAppointments() to fetch all appointments linked to this patient.
   - Filter the results by matching the patientId.
   - Pass the filtered list to the renderAppointments() function.

 Display appointments in a table:
   - Clear existing rows in the table body.
   - Always show the “Actions” column by modifying its CSS if needed.
   - If no appointments exist, display a single row message like “No Appointments Found.”
   - If appointments exist:
     - Loop through each appointment.
     - Display columns: Patient name ("You"), Doctor name, Date, Time, and Action.
     - If the appointment is editable (status 0), add an edit icon or button.
     - Attach an event listener to the edit icon to allow editing the appointment.

 Redirect user to edit their appointment:
   - When the edit icon is clicked:
     - Build a URL with query parameters including: 
       appointmentId, patientId, patientName, doctorName, doctorId, date, and time.
     - Redirect the user to updateAppointment.html with the prepared query string.

Add filtering functionality for search and dropdown:
   - Set up listeners on:
     - A search bar to search by doctor or name.
     - A filter dropdown (e.g., allAppointments, past, upcoming).
   - When a filter changes:
     - Use filterAppointments() service with the search and filter values.
     - Again, ensure only appointments for the current patientId are included.
     - Re-render the filtered appointments using renderAppointments().

*/

// Import required service functions
import {
    getPatientAppointments,
    getPatientData,
    filterAppointments,
} from "./services/patientServices.js";

import { convertDate } from "./util.js"; // Import utility function for date formatting

// DOM Elements & Variables
const tableBody = document.getElementById("patientTableBody");
const searchInput = document.getElementById("searchBar");
const filterDropdown = document.getElementById("appointmentFilter");

let token = localStorage.getItem("token");
let allAppointments = [];
let filteredAppointments = [];
let patientId = null;

// Initialize on page load
document.addEventListener("DOMContentLoaded", async () => {
    if (!token) return; // Stop if no auth token

    try {
        // Fetch patient profile data
        const patientData = await getPatientData(token);
        patientId = patientData.id;

        // Fetch all appointments linked to this patient
        allAppointments = await getPatientAppointments(patientId, null, token);

        // Render initial appointments list
        renderAppointments(allAppointments);
    }
    catch (error) {
        console.error("Failed to load patient data or appointments:", error);
        tableBody.innerHTML = `<tr><td colspan="6" class="text-red-600">Failed to load appointments.</td></tr>`;
    }
});

// Render appointment rows in the table
function renderAppointments(appointments) {
    tableBody.innerHTML = ""; // Clear previous rows

    // Make sure Actions column is always visible (adjust CSS if needed)
    const actionsHeader = document.querySelector("th.actions");
    if (actionsHeader) actionsHeader.style.display = "";

    if (!appointments.length) {
        tableBody.innerHTML = `<tr><td colspan="6" class="text-center text-gray-600">No Appointments Found.</td></tr>`;
        return;
    }

    appointments.forEach((appointment) => {
        const tr = document.createElement("tr");

        // Columns: Patient ("You"), Doctor, Date, Time, Status, Actions
        tr.innerHTML = `
          <!--<td>You</td>-->
          <td>${appointment.patientName}</td>
          <td>${appointment.doctorName}</td>
          <td>${convertDate(appointment.appointmentTime)}</td>
          <td>${convertDate(null, appointment.appointmentTime)}</td>
          <!--<td>${appointment.status === 0 ? "Editable" : "Locked"}</td>-->
          <td class="actions">
            ${
                appointment.status === 0
                    ? `<button class="edit-btn" data-id="${appointment.id}">✏️ Edit</button>`
                    : `<span>—</span>`
            }
          </td>
        `;

        // Attach click listener on edit button if editable
        if (appointment.status === 0) {
            tr.querySelector(".edit-btn").addEventListener("click", () =>
                redirectToEdit(appointment)
            );
        }

        tableBody.appendChild(tr);
    });
}

// Redirect to appointment edit page with query params
function redirectToEdit(appt) {
    const params = new URLSearchParams({
        appointmentId: appt.id,
        patientId: patientId,
        patientName: "You",
        doctorName: appt.doctorName,
        doctorId: appt.doctorId,
        date: appt.date,
        time: appt.time,
    });

    window.location.href = `updateAppointment.html?${params.toString()}`;
}

// Filtering on search input and dropdown change
searchInput.addEventListener("input", handleFilterChange);
filterDropdown.addEventListener("change", handleFilterChange);

async function handleFilterChange() {
    const searchTerm = searchInput.value.trim() || '';
    const filteredCondition = filterDropdown.value || "all";

    try {
        const filtered = await filterAppointments(searchTerm, filteredCondition, token);

        renderAppointments(filtered);
    } catch (error) {
        console.error("Failed to filter appointments:", error);
        tableBody.innerHTML = `<tr><td colspan="6" class="text-red-600">Failed to load filtered appointments.</td></tr>`;
    }
}

