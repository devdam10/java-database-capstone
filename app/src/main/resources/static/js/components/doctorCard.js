/*
Import the overlay function for booking appointments from loggedPatient.js

  Import the deleteDoctor API function to remove doctors (admin role) from docotrServices.js

  Import function to fetch patient details (used during booking) from patientServices.js

  Function to create and return a DOM element for a single doctor card
    Create the main container for the doctor card
    Retrieve the current user role from localStorage
    Create a div to hold doctor information
    Create and set the doctor’s name
    Create and set the doctor's specialization
    Create and set the doctor's email
    Create and list available appointment times
    Append all info elements to the doctor info container
    Create a container for card action buttons
    === ADMIN ROLE ACTIONS ===
      Create a delete button
      Add click handler for delete button
     Get the admin token from localStorage
        Call API to delete the doctor
        Show result and remove card if successful
      Add delete button to actions container
   
    === PATIENT (NOT LOGGED-IN) ROLE ACTIONS ===
      Create a book now button
      Alert patient to log in before booking
      Add button to actions container
  
    === LOGGED-IN PATIENT ROLE ACTIONS === 
      Create a book now button
      Handle booking logic for logged-in patient   
        Redirect if token not available
        Fetch patient data with token
        Show booking overlay UI with doctor and patient info
      Add button to actions container
   
  Append doctor info and action buttons to the car
  Return the complete doctor card element
*/

// Import external functions assumed available from other modules
import { showBookingOverlay } from "../loggedPatient.js";
import { deleteDoctor } from "../services/doctorServices.js";
import { getPatientData } from "../services/patientServices.js";

export async function createDoctorCard(doctor) {
    // Main card container
    const card = document.createElement("div");
    card.classList.add("doctor-card");

    // Get current user role from localStorage
    const role = localStorage.getItem("userRole");

    // Doctor info container
    const infoDiv = document.createElement("div");
    infoDiv.classList.add("doctor-info");

    // Doctor name
    const name = document.createElement("h3");
    name.textContent = doctor.name;

    // Doctor specialization
    const specialization = document.createElement("p");
    specialization.textContent = `Specialty: ${doctor.specialty}`;

    // Doctor email
    const email = document.createElement("p");
    email.textContent = `Email: ${doctor.email}`;

    // Availability - assume doctor.availability is an array of strings
    const availability = document.createElement("p");
    // availability.textContent = `Availability: ${doctor.availability.join(", ")}`;
    availability.textContent = `Availability: ${doctor.availableTimes?.join(", ") || "Not Available"}`;


    // Append info elements to infoDiv
    infoDiv.appendChild(name);
    infoDiv.appendChild(specialization);
    infoDiv.appendChild(email);
    infoDiv.appendChild(availability);

    // Actions container for buttons
    const actionsDiv = document.createElement("div");
    actionsDiv.classList.add("card-actions");

    if (role === "admin") {
        // Delete button for admins
        const removeBtn = document.createElement("button");
        removeBtn.textContent = "Delete";
        removeBtn.addEventListener("click", async () => {
            const confirmDelete = confirm(`Are you sure you want to delete ${doctor.name}?`);
            if (!confirmDelete) return;

            const token = localStorage.getItem("token");
            if (!token) {
                alert("Admin token missing. Please log in again.");
                return;
            }

            try {
                await deleteDoctor(doctor.id, token);
                alert(`${doctor.name} has been deleted.`);
                card.remove();
            }
            catch (error) {
                alert("Failed to delete doctor. Please try again.");
                console.error(error);
            }
        });
        actionsDiv.appendChild(removeBtn);
    }
    else if (role === "patient") {
        // Book Now button for non-logged in patients
        const bookNow = document.createElement("button");
        bookNow.textContent = "Book Now";
        bookNow.addEventListener("click", () => {
            alert("Please log in to book an appointment.");
        });
        actionsDiv.appendChild(bookNow);
    }
    else if (role === "loggedPatient") {
        // Book Now button for logged-in patients
        const bookNow = document.createElement("button");
        bookNow.textContent = "Book Now";
        bookNow.addEventListener("click", async (e) => {
            const token = localStorage.getItem("token");
            if (!token) {
                alert("Session expired. Please log in again.");
                // Optionally redirect to login page here
                return;
            }

            try {
                const patientData = await getPatientData(token);
                showBookingOverlay(e, doctor, patientData);
            } catch (error) {
                alert("Failed to fetch patient data. Please try again.");
                console.error(error);
            }
        });
        actionsDiv.appendChild(bookNow);
    }

    // Append info and actions containers to main card
    card.appendChild(infoDiv);
    card.appendChild(actionsDiv);

    return card;
}
