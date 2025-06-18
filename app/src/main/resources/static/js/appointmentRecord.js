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