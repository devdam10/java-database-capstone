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
