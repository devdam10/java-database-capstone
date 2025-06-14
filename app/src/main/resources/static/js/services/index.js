/*
  Import the openModal function to handle showing login popups/modals
  Import the base API URL from the config file
  Define constants for the admin and doctor login API endpoints using the base URL

  Use the window.onload event to ensure DOM elements are available after page load
  Inside this function:
    - Select the "adminLogin" and "doctorLogin" buttons using getElementById
    - If the admin login button exists:
        - Add a click event listener that calls openModal('adminLogin') to show the admin login modal
    - If the doctor login button exists:
        - Add a click event listener that calls openModal('doctorLogin') to show the doctor login modal


  Define a function named adminLoginHandler on the global window object
  This function will be triggered when the admin submits their login credentials

  Step 1: Get the entered username and password from the input fields
  Step 2: Create an admin object with these credentials

  Step 3: Use fetch() to send a POST request to the ADMIN_API endpoint
    - Set method to POST
    - Add headers with 'Content-Type: application/json'
    - Convert the admin object to JSON and send in the body

  Step 4: If the response is successful:
    - Parse the JSON response to get the token
    - Store the token in localStorage
    - Call selectRole('admin') to proceed with admin-specific behavior

  Step 5: If login fails or credentials are invalid:
    - Show an alert with an error message

  Step 6: Wrap everything in a try-catch to handle network or server errors
    - Show a generic error message if something goes wrong


  Define a function named doctorLoginHandler on the global window object
  This function will be triggered when a doctor submits their login credentials

  Step 1: Get the entered email and password from the input fields
  Step 2: Create a doctor object with these credentials

  Step 3: Use fetch() to send a POST request to the DOCTOR_API endpoint
    - Include headers and request body similar to admin login

  Step 4: If login is successful:
    - Parse the JSON response to get the token
    - Store the token in localStorage
    - Call selectRole('doctor') to proceed with doctor-specific behavior

  Step 5: If login fails:
    - Show an alert for invalid credentials

  Step 6: Wrap in a try-catch block to handle errors gracefully
    - Log the error to the console
    - Show a generic error message
*/

// File: app/src/main/resources/static/js/services/index.js

import { openModal } from "../components/modals.js";
import { DOCTORS_API, PATIENTS_API, ADMIN_API } from "../config/config.js";
import {renderContent, selectRole } from "../render.js";

// Assuming base API URL is set elsewhere or use relative paths here
const DOCTOR_API = '/doctor/login';

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
      openModal('patientLogin');
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

    const response = await fetch(DOCTORS_API, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(doctor),
    });

    if (response.ok) {
      const data = await response.json();
      localStorage.setItem('token', data.token);
      selectRole('doctor');
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
window.patientLoginHandler = async function () {
  try {
    const emailInput = document.getElementById('patientEmail');
    const passwordInput = document.getElementById('patientPassword');

    if (!emailInput || !passwordInput) {
      alert("Login inputs not found");
      return;
    }

    const email = emailInput.value.trim();
    const password = passwordInput.value;

    const doctor = { email, password };

    const response = await fetch(PATIENTS_API, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(doctor),
    });

    if (response.ok) {
      const data = await response.json();
      localStorage.setItem('token', data.token);
      selectRole('doctor');
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

// Helper function to save role and proceed (assumed imported or globally defined elsewhere)
// function selectRole(role) {
//   localStorage.setItem('userRole', role);
//   // Add any page redirection or UI changes needed after login here
//   // e.g. window.location.href = '/dashboard';
// }
