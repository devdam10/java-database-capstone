// File: app/src/main/resources/static/js/services/index.js

import { openModal } from "../components/modals.js";
import { DOCTORS_API, PATIENTS_API, ADMIN_API } from "../config/config.js";
import {renderContent, selectRole } from "../render.js";

window.onload = function () {
  const adminBtn = document.getElementById('adminBtn');

  if (adminBtn) {
    adminBtn.addEventListener('click', () => {
      openModal('adminLogin');
    });
  }

  const doctorBtn = document.getElementById('doctorBtn');
  if (doctorBtn) {
    doctorBtn.addEventListener('click', () => {
      openModal('doctorLogin');
    });
  }

  const patientBtn = document.getElementById('patientBtn');
  if (patientBtn) {
    patientBtn.addEventListener('click', () => {
      // openModal('patientLogin');
      localStorage.setItem("userRole", "patient");

      window.location.href = `../../pages/patientDashboard.html`;
    });
  }
};

// Admin Login Handler
window.adminLoginHandler = async function () {
  try {
    const usernameInput = document.getElementById('adminUsername');
    const passwordInput = document.getElementById('adminPassword');

    if (!usernameInput || !passwordInput) {
      alert("Login inputs not found");
      return;
    }

    const username = usernameInput.value.trim();
    const password = passwordInput.value;

    const admin = { username, password };

    const response = await fetch(ADMIN_API + "/login", {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(admin),
    });

    if (response.ok) {
      const data = await response.json();
      localStorage.setItem('token', data.token);
      localStorage.setItem('userRole', 'admin');

      setTimeout(function () {
        selectRole('admin');
      }, 2000);
    }
    else {
      alert("Invalid credentials!");
    }
  }
  catch (error) {
    alert("An error occurred during login. Please try again.");
    console.error(error);
  }
};

// Doctor Login Handler
window.doctorLoginHandler = async function () {
  try {
    const emailInput = document.getElementById('doctorEmail');
    const passwordInput = document.getElementById('doctorPassword');

    if (!emailInput || !passwordInput) {
      alert("Login inputs not found");
      return;
    }

    const email = emailInput.value.trim();
    const password = passwordInput.value;

    const doctor = { email, password };

    const response = await fetch(DOCTORS_API + '/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(doctor),
    });

    if (response.ok) {
      const data = await response.json();
      localStorage.setItem('token', data.token);
      localStorage.setItem('userRole', 'doctor');

      setTimeout(function () {
        selectRole('doctor');
      }, 2000);
    }
    else {
      alert("Invalid credentials!");
    }
  }
  catch (error) {
    alert("An error occurred during login. Please try again.");
    console.error(error);
  }
};

// Patient Login Handler
// window.patientLoginHandler = async function () {
//   try {
//     const emailInput = document.getElementById('patientEmail');
//     const passwordInput = document.getElementById('patientPassword');
//
//     if (!emailInput || !passwordInput) {
//       alert("Login inputs not found");
//       return;
//     }
//
//     const email = emailInput.value.trim();
//     const password = passwordInput.value;
//
//     const doctor = { email, password };
//
//     const response = await fetch(PATIENTS_API + '/login', {
//       method: 'POST',
//       headers: { 'Content-Type': 'application/json' },
//       body: JSON.stringify(doctor),
//     });
//
//     if (response.ok) {
//       const data = await response.json();
//       localStorage.setItem('token', data.token);
//       localStorage.setItem('userRole', 'patient');
//
//       setTimeout(function () {
//         selectRole('patient');
//       }, 2000);
//     }
//     else {
//       alert("Invalid credentials!");
//     }
//   }
//   catch (error) {
//     alert("An error occurred during login. Please try again.");
//     console.error(error);
//   }
// };

// Helper function to save role and proceed (assumed imported or globally defined elsewhere)
// function selectRole(role) {
//   localStorage.setItem('userRole', role);
//   // Add any page redirection or UI changes needed after login here
//   // e.g. window.location.href = '/dashboard';
// }
