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
