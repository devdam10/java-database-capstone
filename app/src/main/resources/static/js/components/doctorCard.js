// Import external functions assumed available from other modules
import { showBookingOverlay, showBookingOverlayBackup } from "../loggedPatient.js";
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
            handleBookNowClick(e);
        });

        actionsDiv.appendChild(bookNow);
        actionsDiv.addEventListener("click", async (e) => {
            handleBookNowClick(e);
        });

        async function handleBookNowClick(e) {
            const token = localStorage.getItem("token");

            if (!token) {
                alert("Session expired. Please log in again.");
                // Optionally redirect to login page here
                return;
            }

            try {
                const patientData = await getPatientData(token);
                //showBookingOverlay(e, doctor, patientData);

                const booking = {
                    doctorId: doctor.id,
                    appointmentTimes: doctor.availableTimes, // Default to first available time
                    patientId: patientData.id,
                    patientName: patientData.name,
                };

                // Call the booking overlay function with the booking and doctor details
                showBookingOverlayBackup(booking, doctor, e);
            } 
            catch (error) {
                alert("Failed to fetch patient data. Please try again.");
                console.error(error);
            }
        }
    }

    // Append info and actions containers to main card
    card.appendChild(infoDiv);
    card.appendChild(actionsDiv);

    return card;
}
