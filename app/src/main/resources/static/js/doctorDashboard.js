/*
  Import getAllAppointments to fetch appointments from the backend
  Import createPatientRow to generate a table row for each patient appointment


  Get the table body where patient rows will be added
  Initialize selectedDate with today's date in 'YYYY-MM-DD' format
  Get the saved token from localStorage (used for authenticated API calls)
  Initialize patientName to null (used for filtering by name)


  Add an 'input' event listener to the search bar
  On each keystroke:
    - Trim and check the input value
    - If not empty, use it as the patientName for filtering
    - Else, reset patientName to "null" (as expected by backend)
    - Reload the appointments list with the updated filter


  Add a click listener to the "Today" button
  When clicked:
    - Set selectedDate to today's date
    - Update the date picker UI to match
    - Reload the appointments for today


  Add a change event listener to the date picker
  When the date changes:
    - Update selectedDate with the new value
    - Reload the appointments for that specific date


  Function: loadAppointments
  Purpose: Fetch and display appointments based on selected date and optional patient name

  Step 1: Call getAllAppointments with selectedDate, patientName, and token
  Step 2: Clear the table body content before rendering new rows

  Step 3: If no appointments are returned:
    - Display a message row: "No Appointments found for today."

  Step 4: If appointments exist:
    - Loop through each appointment and construct a 'patient' object with id, name, phone, and email
    - Call createPatientRow to generate a table row for the appointment
    - Append each row to the table body

  Step 5: Catch and handle any errors during fetch:
    - Show a message row: "Error loading appointments. Try again later."


  When the page is fully loaded (DOMContentLoaded):
    - Call renderContent() (assumes it sets up the UI layout)
    - Call loadAppointments() to display today's appointments by default
*/

// doctorDashboard.js

import { getAllAppointments } from './services/appointmentRecordService.js';
import { createPatientRow } from './components/patientRows.js';
// import { createAppointmentRow } from "./components/appointmentRow.js";

// === Global Variables ===
const tableBody = document.getElementById('patientTableBody');
// let tableBody = null;
let selectedDate = null; // Today's date (YYYY-MM-DD)
// let selectedDate = null;
let patientName = null; // For search filtering
const token = localStorage.getItem('token'); // For authentication


// === Main Logic ===

/**
 * Fetch and display appointments based on selectedDate and patientName.
 */
async function loadAppointments(selectedDate = null) {
    try {
        const appointments = await getAllAppointments(selectedDate, patientName, token);

        // Clear current table rows
        tableBody.innerHTML = '';

        if (!appointments || appointments.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="5">No Appointments found for today.</td></tr>`;
            return;
        }

        // // Render each appointment as a row
        // appointments.forEach(app => {
        //     const patient = {
        //         id: app.patientId,
        //         name: app.patientName,
        //         phone: app.patientPhone,
        //         email: app.patientEmail
        //     };
        //     const row = createPatientRow(patient);
        //     tableBody.appendChild(row);
        // });

        // Render each appointment as a row
        appointments.forEach(appointment => {
            const p = appointment.patient;
            const patient = {
                id: p.id,
                name: p.name,
                phone: p.phone,
                email: p.email
            };
            const row = createPatientRow(patient, appointment.id, appointment.doctor.id);
            tableBody.appendChild(row);
        });
    } catch (error) {
        console.error('Failed to load appointments:', error);
        tableBody.innerHTML = `<tr><td colspan="5">Error loading appointments. Try again later.</td></tr>`;
    }
}

// Optional: UI setup function
function renderContent() {
    document.getElementById('datePicker').value = selectedDate;
}

// === Initialize on page load ===
document.addEventListener('DOMContentLoaded', () => {
    //tableBody = document.getElementById('patientTableBody');

    // === Event Listeners ===

    // Search bar filtering by patient name
    document.getElementById('searchBar').addEventListener('input', () => {
        const input = document.getElementById('searchBar').value.trim();
        patientName = input !== '' ? input : null;
        loadAppointments();
    });

    // "Today" button resets to today's appointments
    document.getElementById('todayButton').addEventListener('click', () => {
        selectedDate = new Date().toISOString().split('T')[0];
        document.getElementById('datePicker').value = selectedDate;
        //loadAppointments();

        loadAppointments(selectedDate);
    });

    // Date picker changes selected date
    document.getElementById('datePicker').addEventListener('change', (e) => {
        selectedDate = e.target.value;
        //loadAppointments();

        loadAppointments(selectedDate);
    });

    renderContent();
    loadAppointments();
});
