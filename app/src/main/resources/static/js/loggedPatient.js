/*
  Import functions to fetch all doctors, filter doctors, and book an appointment
  Import the function to create doctor cards for display


  When the page is fully loaded (DOMContentLoaded):
    - Call loadDoctorCards() to fetch and display all doctors initially


  Function: loadDoctorCards
  Purpose: Fetch all doctors from the backend and display them as cards

   Call getDoctors() to retrieve doctor data
   Clear any existing content from the container div
   Loop through each doctor and:
     - Create a visual card using createDoctorCard()
     - Append it to the content container
   Handle errors (e.g., show console message if fetch fails)


  Function: showBookingOverlay
  Purpose: Display a modal to book an appointment with a selected doctor

   Create a ripple effect at the clicked location for UI feedback
   Build a modal with:
     - Pre-filled doctor and patient details (disabled inputs)
     - Date picker for appointment
     - Dropdown menu of available time slots

   Append the modal to the document body and animate it
   Add click listener to the "Confirm Booking" button:
     - Collect selected date and time
     - Format the time (extract start time for appointment)
     - Prepare the appointment object with doctor, patient, and datetime
     - Use bookAppointment() to send the request to the backend
     - On success: Show alert, remove modal and ripple
     - On failure: Show error message to the user


  Add event listeners to:
    - The search bar (on input)
    - Time filter dropdown (on change)
    - Specialty filter dropdown (on change)

  All inputs call filterDoctorsOnChange() to apply the filters dynamically


  Function: filterDoctorsOnChange
  Purpose: Fetch and display doctors based on user-selected filters

   Read input values (name, time, specialty)
   Set them to 'null' if empty, as expected by backend
   Call filterDoctors(name, time, specialty)
   If doctors are returned:
     - Clear previous content
     - Create and display a card for each doctor
   If no results found, show a message: "No doctors found with the given filters."
   Handle and display any fetch errors


  Function: renderDoctorCards
  Purpose: Render a list of doctor cards passed as an argument
  Used to dynamically render pre-filtered results or external data
*/

// Imports
import { getDoctors, filterDoctors } from "./services/doctorServices.js";
import { bookAppointment } from "./services/appointmentRecordService.js";
import { createDoctorCard } from "./components/doctorCard.js";

// DOM Elements
const contentContainer = document.getElementById("content");
const searchInput = document.getElementById("searchBar");
const timeDropdown = document.getElementById("timeFilter");
const specialtyDropdown = document.getElementById("specialtyFilter");

// Fetch and display all doctors
async function loadDoctorCards() {
    try {
        const doctors = await getDoctors();

        const contentDiv = document.getElementById('content');
        if (!contentDiv) {
            //console.log('Element with id="content" not found in DOM.');
            return;
        }

        contentDiv.innerHTML = '';

        await renderDoctorCards(doctors);
    }
    catch (error) {
        console.error("Failed to load doctors:", error);
        contentContainer.innerHTML = `<p class="text-red-500">Unable to load doctors at this time.</p>`;
    }
}

// Render doctor cards
async function renderDoctorCards(doctors) {
    contentContainer.innerHTML = "";

    if (!doctors || doctors.length === 0) {
        contentContainer.innerHTML = `<p class="text-gray-500">No doctors found.</p>`;
        return;
    }

    // doctors.forEach((doctor) => {
    //     const card = createDoctorCard(doctor, showBookingOverlay);
    //     contentContainer.appendChild(card);
    // });

    for (const doctor of doctors) {
        const card = await createDoctorCard(doctor); // ✅ await the async function
        contentContainer.appendChild(card);
    }
}

// Display booking overlay/modal
export function showBookingOverlay(doctor, event) {
    const ripple = document.createElement("div");
    ripple.className = "ripple";
    ripple.style.left = `${event.clientX}px`;
    ripple.style.top = `${event.clientY}px`;
    document.body.appendChild(ripple);

    // Build modal
    const modal = document.createElement("div");
    modal.className = "booking-modal";
    modal.innerHTML = `
    <div class="modal-content">
      <h2>Book Appointment</h2>
      <label>Doctor: <input type="text" value="${doctor.name}" disabled></label>
      <label>Patient: <input type="text" value="${localStorage.getItem("patientName")}" disabled></label>
      <label>Date: <input type="date" id="appointmentDate"></label>
      <label>Time: 
        <select id="appointmentTime">
          <option value="09:00">09:00 AM</option>
          <option value="10:00">10:00 AM</option>
          <option value="11:00">11:00 AM</option>
          <option value="14:00">02:00 PM</option>
          <option value="15:00">03:00 PM</option>
        </select>
      </label>
      <button id="confirmBookingBtn">Confirm Booking</button>
      <button class="modal-close">Cancel</button>
    </div>
  `;

    document.body.appendChild(modal);

    // Event Listeners
    modal.querySelector(".modal-close").addEventListener("click", () => {
        modal.remove();
        ripple.remove();
    });

    modal.querySelector("#confirmBookingBtn").addEventListener("click", async () => {
        const token = localStorage.getItem("token");
        const date = modal.querySelector("#appointmentDate").value;
        const time = modal.querySelector("#appointmentTime").value;

        if (!date || !time) {
            alert("Please select both date and time.");
            return;
        }

        const appointmentDateTime = `${date}T${time}`;
        const appointment = {
            doctorId: doctor.id,
            patientId: localStorage.getItem("patientId"),
            appointmentDate: appointmentDateTime,
        };

        try {
            const result = await bookAppointment(appointment, token);
            if (result.success) {
                alert("Appointment booked successfully!");
                modal.remove();
                ripple.remove();
            } else {
                alert("Failed to book appointment: " + result.message);
            }
        } catch (error) {
            console.error("Booking error:", error);
            alert("An error occurred while booking the appointment.");
        }
    });
}

// Filtering logic
async function filterDoctorsOnChange() {
    const name = searchInput.value.trim() || null;
    const time = timeDropdown.value || null;
    const specialty = specialtyDropdown.value || null;

    filterDoctors(name, time, specialty)
        .then((data) => {
            console.log("data: " + data);

            if (data.doctors && data.doctors.length > 0) {
                renderDoctorCards(data.doctors);
            }
            else {
                contentContainer.innerHTML = `<p class="text-gray-500">No doctors found with the given filters.</p>`;
            }
        })
        .catch((error) => {
            console.error("Error filtering doctors:", error);
            contentContainer.innerHTML = `<p class="text-red-500">Failed to fetch filtered doctors.</p>`;
        });
}


// Load all doctors on page load
// Attach listeners for search and filters
document.addEventListener("DOMContentLoaded", () => {
    const searchInput = document.getElementById("searchBar");
    const timeDropdown = document.getElementById("timeFilter");
    const specialtyDropdown = document.getElementById("specialtyFilter");

    if (searchInput) searchInput.addEventListener("input", filterDoctorsOnChange);
    if (timeDropdown) timeDropdown.addEventListener("change", filterDoctorsOnChange);
    if (specialtyDropdown) specialtyDropdown.addEventListener("change", filterDoctorsOnChange);

    loadDoctorCards();
});


