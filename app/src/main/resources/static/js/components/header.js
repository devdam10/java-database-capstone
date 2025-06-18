// header.js

// Render the header dynamically based on user role and session
import { selectRole } from "../render.js";

export function renderHeader() {
    const headerDiv = document.getElementById("header");

    // If on homepage/root page, clear session and show basic header
    if (window.location.pathname.endsWith("/")) {
        localStorage.removeItem("userRole");
        localStorage.removeItem("token");
        headerDiv.innerHTML = `
          <header class="header">
            <div class="logo-section">
              <img src="/assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
              <span class="logo-title">Hospital CMS</span>
            </div>
          </header>`;
        return;
    }

    // Get role and token from localStorage
    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");

    // Initialize header HTML
    let headerContent = `
    <header class="header">
      <div class="logo-section">
        <img src="/assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
        <span class="logo-title">Hospital CMS</span>
      </div>
      <nav>`;

    // Handle invalid session: role exists but no token
    if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
        localStorage.removeItem("userRole");
        alert("Session expired or invalid login. Please log in again.");
        window.location.href = "/";
        return;
    }

    // Add role-specific navigation buttons
    if (role === "admin") {
        headerContent += `
      <button id="addDocBtn" class="adminBtn">Add Doctor</button>
      <a href="#" id="logoutBtn">Logout</a>`;
    }
    else if (role === "doctor") {
        headerContent += `
      <button id="homeBtn" class="doctorBtn">Home</button>
      <a href="#" id="logoutBtn">Logout</a>`;
    }
    else if (role === "patient") {
        headerContent += `
      <button id="patientLogin" class="patientBtn">Login</button>
      <button id="patientSignup" class="patientBtn">Sign Up</button>`;
    }
    else if (role === "loggedPatient") {
        headerContent += `
      <button id="homeBtn" class="patientBtn">Home</button>
      <button id="patientAppointments" class="patientBtn">Appointments</button>
      <a href="#" id="logoutPatientBtn">Logout</a>`;
    }
    else {
        // No role found, default to showing login/signup for patients
        headerContent += `
      <button id="patientLogin" class="adminBtn">Login</button>
      <button id="patientSignup" class="adminBtn">Sign Up</button>`;
    }

    // Close nav and header tags
    headerContent += `
      </nav>
    </header>`;

    // Insert header content into page
    headerDiv.innerHTML = headerContent;

    // Attach event listeners to dynamically created buttons
    attachHeaderButtonListeners();
}

// Attach event listeners for header buttons
function attachHeaderButtonListeners() {
    const addDocBtn = document.getElementById("addDocBtn");
    if (addDocBtn) {
        addDocBtn.addEventListener("click", () => openModal("addDoctor"));
    }

    const logoutBtn = document.getElementById("logoutBtn");
    if (logoutBtn) {
        logoutBtn.addEventListener("click", (e) => {
            e.preventDefault();
            logout();
        });
    }

    const logoutPatientBtn = document.getElementById("logoutPatientBtn");
    if (logoutPatientBtn) {
        logoutPatientBtn.addEventListener("click", (e) => {
            e.preventDefault();
            logoutPatient();
        });
    }

    const patientLoginBtn = document.getElementById("patientLogin");
    if (patientLoginBtn) {
        patientLoginBtn.addEventListener("click", () => {
            openModal("patientLogin");
        });
    }

    const patientSignupBtn = document.getElementById("patientSignup");
    if (patientSignupBtn) {
        patientSignupBtn.addEventListener("click", () => {
            openModal("patientSignup");
        });
    }

    const homeBtn = document.getElementById("homeBtn");
    if (homeBtn) {
        homeBtn.addEventListener("click", () => {
            // You can define behavior if needed, else inline onclick is enough
            const role = localStorage.getItem("userRole");
            selectRole(role);
        });
    }

    const patientAppointmentsBtn = document.getElementById("patientAppointments");
    if (patientAppointmentsBtn) {
        patientAppointmentsBtn.addEventListener("click", () => {
            // Same as above, inline navigation covers this

            window.location.href='../../pages/patientAppointments.html';
        });
    }
}

// Logout for admin, doctor, loggedPatient
function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("userRole");
    window.location.href = "/";
}

// Logout for patient (loggedPatient)
function logoutPatient() {
    localStorage.removeItem("token");
    localStorage.setItem("userRole", "patient");
    window.location.href = "../../pages/patientDashboard.html";
}

// Dummy openModal function (replace with your modal code)
function openModal(modalName) {
    // Implementation depends on your modal system
    console.log(`Open modal: ${modalName}`);
}

// // Dummy selectRole function if used by doctor home button
// function selectRole(role) {
//     localStorage.setItem("userRole", role);
//     window.location.href = "/pages/doctorDashboard.html"; // adjust as needed
// }

// Call renderHeader on script load
renderHeader();
