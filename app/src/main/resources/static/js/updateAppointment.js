// import { getDoctors, updateAppointment } from "./components/appointmentServices.js";

import { getDoctor, getDoctors } from "./services/doctorServices.js";
import { updateAppointment } from "./services/appointmentRecordService.js";

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

// let updatedTime = null;


async function initializePage() {
    if (!token || !patientId) {
        // window.location.href = "patientAppointments.html";
        window.history.back();
        return;
    }

    // Show updateBtn locale storage role is of a doctor
    const updateBtn = document.getElementById("updateBtn");
    if (localStorage.getItem("userRole") === "doctor") {
        updateBtn.style.display = "inline-block"; 
    }
    // Hide update button for patients and loggedPatients
    else {
      updateBtn.style.display = "none";

      // Disable the form inputs for patients
      appointmentDateInput.disabled = true;
      appointmentTimeSelect.disabled = true;
      patientNameInput.disabled = true;
      doctorNameInput.disabled = true;
    }

    patientNameInput.value = patientName || "";
    doctorNameInput.value = doctorName || "";
    appointmentDateInput.value = appointmentDate || "";

    try {
        const doctor = await getDoctor(doctorId);

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

            // Extract start time from "11:00-12:00"
            const startTime = time.split("-")[0];

            // Normalize both times to HH:mm format for comparison
            const normalizedAppointmentTime = appointmentTime.slice(0, 5); // "11:00:00" → "11:00"

            if (startTime === normalizedAppointmentTime) {
                option.selected = true;
            }
            
            appointmentTimeSelect.appendChild(option);
        });
    }
    catch (error) {
        console.error("Failed to load doctors:", error);
        alert("Failed to load doctor information. Please try again later.");
    }

    // Set up cancel button to redirect back to patient appointments page
    const cancelBtn = document.getElementById("cancelBtn");
    cancelBtn.addEventListener("click", () => {
        // When the cancel button is clicked, cancel the appoint from the server side then redirect to the patient appointments page


        // Redirect to patient appointments page
        window.history.back();
    });
}

updateForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    const updatedDate = appointmentDateInput.value;
    const updatedTime = appointmentTimeSelect.value;

    const id = appointmentId || urlParams.get("appointmentId");

    if (!updatedDate || !updatedTime) {
        alert("Please select both appointment date and time.");
        return;
    }

    // combine the updated date and time into a single Date object
    const datetime = new Date(`${updatedDate}T${updatedTime.split('-')[0]}:00`);
    datetime.setHours(datetime.getHours() - 5);

    const updatedAppointment = {
        id,
        patientId,
        doctorId,
        appointmentTime: datetime.toISOString(),
    };

    try {
        const response = await updateAppointment(updatedAppointment, token);
        if (response.success) {
            alert("Appointment updated successfully.");
            window.location.href = "../pages/patientAppointments.html";
        } 
        else {
            alert(`Update failed: ${response.message || "Unknown error"}`);
        }
    } 
    catch (error) {
        console.error("Error updating appointment:", error);
        alert("An error occurred while updating the appointment. Please try again.");
    }
});


// // Setup listeners for form select on change
// appointmentTimeSelect.addEventListener("change", (e) => {
//     updatedTime = e.target.value.trim();

//     const array = ['11:00-12:00', '14:00-15:00', '15:00-16:00'];
//     if (array.includes(updatedTime)) {
//         console.log("Selected time is available:", updatedTime);
//     } else {
//         console.log("Selected time is not available:", updatedTime);
//     }
// });


// Initialize page on DOM load
document.addEventListener("DOMContentLoaded", initializePage);
