/*
  Import the prescription service functions: savePrescription and getPrescription

  Wait for the DOM content to load before running the script
  Inside the event listener:
    - Get references to form input elements and the heading
    - Extract query parameters from the URL:
        - appointmentId: identifies the current appointment
        - mode: determines if the page is in "add" or "view" mode
        - patientName: used to pre-fill the patient name
    - Also retrieve the stored token from localStorage for API calls


  If the heading element exists:
    - Check the "mode" query parameter:
      - If mode is "view": set heading to "View Prescription"
      - Else (default): set heading to "Add Prescription"


  If patientName is available from the URL and the input exists:
    - Set the patientNameInput field with the given name


  If both appointmentId and token are available:
    - Call getPrescription to fetch prescription data from the server
    - If a prescription exists (response.prescription is a non-empty array):
        - Extract the first prescription object
        - Pre-fill all form fields with the data: medication, dosage, notes, etc.
        - Use fallback values (like empty string "") for safety

  Catch and log any errors during fetch to handle cases where no prescription exists


  If mode is "view":
    - Disable all input fields (make them read-only)
    - Hide the save button so the user cannot submit changes


  Attach a click event listener to the "save" button

  On click:
    - Prevent the default form submission behavior
    - Construct a prescription object from input field values
    - Call savePrescription with the object and the token
    - If the save is successful:
        - Show a success alert
        - Redirect or call selectRole('doctor') to return to doctor view
    - If saving fails:
        - Show an error alert with the message
*/

import { savePrescription, getPrescription } from "./services/prescriptionServices.js";
import { selectRole } from "./render.js";

document.addEventListener("DOMContentLoaded", async () => {
    // Get references to form elements
    const heading = document.getElementById("form-heading");
    const patientNameInput = document.getElementById("patientName");
    const medicationInput = document.getElementById("medicineNames");
    const dosageInput = document.getElementById("dosageInstructions");
    const notesInput = document.getElementById("additionalNotes");
    const saveBtn = document.getElementById("saveBtn");
    const cancelBtn = document.getElementById("cancelBtn");

    // Extract query parameters
    const urlParams = new URLSearchParams(window.location.search);
    const appointmentId = urlParams.get("appointmentId");
    const mode = urlParams.get("mode") || "add";
    const patientName = urlParams.get("patientName") || "";

    // Get stored token
    const token = localStorage.getItem("token");

    // Update heading text based on mode
    if (heading) {
        heading.textContent = mode === "view" ? "View Prescription" : "Add Prescription";
    }

    // Pre-fill patient name if available
    if (patientNameInput && patientName) {
        patientNameInput.value = patientName;
    }

    // Fetch and populate prescription data if available
    if (appointmentId && token) {
        try {
            const prescriptions = await getPrescription(appointmentId, token);

            if (prescriptions && Array.isArray(prescriptions) && prescriptions.length > 0) {
                const data = prescriptions[0];
                medicationInput.value = data.medication || "";
                dosageInput.value = data.dosage || "";
                notesInput.value = data.notes || "";
            }
        }
        catch (err) {
            console.error("No prescription found or error retrieving it:", err);
        }
    }

    // If view mode, disable inputs and hide the save button
    if (mode === "view") {
        if (patientNameInput) patientNameInput.disabled = true;
        if (medicationInput) medicationInput.disabled = true;
        if (dosageInput) dosageInput.disabled = true;
        if (notesInput) notesInput.disabled = true;
        if (saveBtn) saveBtn.style.display = "none";
    }
    else {
        if (saveBtn) saveBtn.style.display = "inline-block";
    }

    // Save button click handler
    if (saveBtn) {
        saveBtn.addEventListener("click", async (e) => {
            e.preventDefault();

            const patientName = patientNameInput.value.trim();
            const medications = medicationInput.value.trim();
            const dosage = dosageInput.value.trim();
            const notes = notesInput.value.trim();

            // If empty fields then show alert
            if(patientName.length === 0 || medications.length === 0 || dosage.length === 0) {
                alert("Please enter all required fields!");
                return;
            }

            const prescription = {
                appointmentId,
                patientName: patientNameInput.value,
                medication: medicationInput.value,
                dosage: dosageInput.value,
                notes: notesInput.value,
            };

            const result = await savePrescription(prescription, token);

            if (result.success) {
                alert("Prescription saved successfully.");
                // Redirect or go back to doctor view
                if (typeof selectRole === "function") {
                    selectRole("doctor");
                }
                else {
                    //window.location.href = "dashboard.html"; // fallback
                    window.location.href = `/doctorDashboard/${token}`;
                }
            }
            else {
                alert("Error saving prescription: " + result.message);
            }
        });
    }

    // Cancel button handler
    if(cancelBtn){
        cancelBtn.addEventListener("click", async (e) => {
            e.preventDefault();

            window.history.back();
        });
    }
});
