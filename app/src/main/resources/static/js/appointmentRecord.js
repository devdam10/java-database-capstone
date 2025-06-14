/*
  Import a utility to generate a table row for each appointment
  Import the function that retrieves all appointment records for the logged-in user


  Get a reference to the <tbody> element of the appointments table (where rows will be added)
  Get a reference to the filter dropdown that allows selection between "upcoming" and "past"


  Function: loadAppointments
  Purpose: Load and display appointments based on the selected filter

   Fetch all appointments using getAppointmentRecord()
   If no appointments are returned:
    - Show a single table row with the message "No appointments found."

   Create a 'today' date (with time set to 00:00:00 for accurate comparison)

   Filter the appointments array:
    - If the filter is "upcoming", keep only appointments with a date >= today
    - If the filter is "past", keep only appointments with a date < today

   If the filtered list is empty:
    - Show a message row saying "No upcoming/past appointments found."

   Otherwise, clear the table body
    - For each filtered appointment:
      - Create a new row using getAppointments()
      - Append it to the table


  Add an event listener to the dropdown menu
  When the user selects a new filter:
    - Call loadAppointments with the selected filter value (either 'upcoming' or 'past')


  When the script first runs:
    - Call loadAppointments("upcoming") to load and show only future appointments by default

*/

import { createAppointmentRow } from "./components/appointmentRow.js";
import { getAllAppointments } from "./services/appointmentRecordService.js";

// Get DOM references
const tableBody = document.querySelector("#appointmentsTable tbody");
const filterDropdown = document.getElementById("filterDropdown");

// Load and display appointments
async function loadAppointments(filter = "upcoming") {
    const token = localStorage.getItem("token");

    try {
        // const appointments = await getAllAppointments(doctorId, token);
        const appointments = await getAllAppointments('null', 'null', token);

        if (!appointments || appointments.length === 0) {
            tableBody.innerHTML = `
        <tr>
          <td colspan="5" class="text-center text-gray-500 py-4">No appointments found.</td>
        </tr>`;
            return;
        }

        // Get today's date at midnight
        const today = new Date();
        today.setHours(0, 0, 0, 0);

        const filtered = appointments.filter((a) => {
            const date = new Date(a.appointmentDate);
            date.setHours(0, 0, 0, 0);
            return filter === "upcoming" ? date >= today : date < today;
        });

        // If no results after filtering
        if (filtered.length === 0) {
            tableBody.innerHTML = `
        <tr>
          <td colspan="5" class="text-center text-gray-500 py-4">No ${filter} appointments found.</td>
        </tr>`;
            return;
        }

        // Clear table and repopulate
        tableBody.innerHTML = "";
        filtered.forEach((appointment) => {
            const row = createAppointmentRow(appointment);
            tableBody.appendChild(row);
        });

    } catch (error) {
        console.error("Error loading appointments:", error);
        tableBody.innerHTML = `
      <tr>
        <td colspan="5" class="text-center text-red-500 py-4">Failed to load appointments.</td>
      </tr>`;
    }
}


// Run when DOM is fully loaded
document.addEventListener("DOMContentLoaded", () => {
    // Attach filter listener
    filterDropdown.addEventListener("change", (e) => {
        const selected = e.target.value;
        loadAppointments(selected);
    });

    // Initial load
    loadAppointments("upcoming");

});