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

        // If the user is a doctor, show the edit button
        // const role = localStorage.getItem("userRole");
        // if (role === "doctor") {
        //   tr.querySelector(".edit-btn").addEventListener("click", () =>
        //       redirectToEdit(appointment)
        //   );
        // }

        tableBody.appendChild(tr);
    });
}

// Redirect to appointment edit page with query params
function redirectToEdit(appointment) {
    const appointmentDateTime = new Date(appointment.appointmentTime);

    const params = new URLSearchParams({
        appointmentId: appointment.id,
        patientId: patientId,
        patientName: appointment.patientName,
        doctorName: appointment.doctorName,
        doctorId: appointment.doctorId,
        // date: appointment.date,
        // time: appointment.time,
        date: appointmentDateTime.toISOString().split("T")[0], // "yyyy-MM-dd"
        time: appointmentDateTime.toTimeString().split(" ")[0], // "HH:mm:ss"
    });

    window.location.href = `../pages/updateAppointment.html?${params.toString()}`;
}

// Filtering on search input and dropdown change
searchInput.addEventListener("input", handleFilterChange);
filterDropdown.addEventListener("change", handleFilterChange);

async function handleFilterChange() {
    const searchTerm = searchInput.value.trim() || '';
    const filteredCondition = filterDropdown.value || '';

    try {
        const filtered = await filterAppointments(searchTerm, filteredCondition, token);
        renderAppointments(filtered);
    }
    catch (error) {
        console.error("Failed to filter appointments:", error);
        tableBody.innerHTML = `<tr><td colspan="6" class="text-red-600">Failed to load filtered appointments.</td></tr>`;
    }
}

