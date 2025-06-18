import { getRole } from "./util.js";

export function selectRole(role) {
    // Handles redirection after user selects or logs in with a role
    const token = localStorage.getItem("token");

    switch (role) {
        case "admin":
            if (token) {
                window.location.href = `/adminDashboard?token=${token}`;
            }
            else {
                alert("Authentication token missing for admin access.");
                window.location.href = "/";
            }
            break;

        case "patient":
            //window.location.href = `../pages/patientDashboard.html`;
            break;

        case "doctor":
            if (token) {
                window.location.href = `/doctorDashboard?token=${token}`;
            }
            else {
                alert("Authentication token missing for doctor access.");
                window.location.href = "/";
            }
            break;

        case "loggedPatient":
            if (token) {
                window.location.href = "../pages/loggedPatientDashboard.html";
            }
            else {
                alert("Authentication token missing for logged in patient access.");
                window.location.href = "/";
            }
            break;

        default:
            // If role unknown, redirect to home or login
            window.location.href = "/";
            break;
    }
}

// Renders content or redirects based on user role
export function renderContent() {
    const role = getRole(); // Assume getRole() is globally available or imported

    if (!role) {
        // No role found, redirect to role selection or login page
        window.location.href = "/";
        return;
    }

    // Role-specific rendering logic here
    // Example:
    switch (role) {
        case "admin":
            // Load admin-specific content or redirect
            //selectRole("admin");
            break;
        case "patient":
            // Load patient-specific content or redirect
            //selectRole("patient");
            break;
        case "doctor":
            // Load doctor-specific content or redirect
            //selectRole("doctor");
            break;
        case "loggedPatient":
            // Load logged-in patient content
            //selectRole("loggedPatient");
            break;
        default:
            window.location.href = "/";
            break;
    }
}

// Make it accessible globally
window.renderContent = renderContent;

// // Run when DOM is fully loaded
// document.addEventListener("DOMContentLoaded", () => {
//     renderContent();
// });