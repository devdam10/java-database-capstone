import { createDoctorCard } from './components/doctorCard.js';
import { openModal } from './components/modals.js';
import { filterDoctors, getDoctors } from './services/doctorServices.js';
import { patientLogin, patientSignup } from './services/patientServices.js';
import { selectRole } from "./render.js";

// === Page Load ===
document.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();

  const signupBtn = document.getElementById("patientSignup");
  if (signupBtn) {
    signupBtn.addEventListener("click", () => openModal("patientSignup"));
  }

  const loginBtn = document.getElementById("patientLogin");
  if (loginBtn) {
    loginBtn.addEventListener("click", () => openModal("patientLogin"));
  }

  // Filter Input Listeners
  const searchBar = document.getElementById("searchBar");
  const filterTime = document.getElementById("filterTime");
  const filterSpecialty = document.getElementById("filterSpecialty");

  if (searchBar) searchBar.addEventListener("input", filterDoctorsOnChange);
  if (filterTime) filterTime.addEventListener("change", filterDoctorsOnChange);
  if (filterSpecialty) filterSpecialty.addEventListener("change", filterDoctorsOnChange);
});

// === Load All Doctors ===
async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();
    await renderDoctorCards(doctors);
  }
  catch (error) {
    console.error("Failed to load doctors:", error);
    document.getElementById("content").innerHTML = "<p>❌ Failed to load doctors. Please try again later.</p>";
  }
}

// === Filter Doctors Based on Input ===
async function filterDoctorsOnChange() {
  try {
    const name = document.getElementById("searchBar")?.value.trim() || null;
    const time = document.getElementById("filterTime")?.value || null;
    const specialty = document.getElementById("filterSpecialty")?.value || null;

    const { doctors } = await filterDoctors(name || null, time || null, specialty || null);
    const contentDiv = document.getElementById("content");
    contentDiv.innerHTML = "";

    if (doctors.length > 0) {
      renderDoctorCards(doctors);
    } else {
      contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
    }
  } catch (error) {
    console.error("Failed to filter doctors:", error);
    alert("❌ An error occurred while filtering doctors.");
  }
}

// === Reusable Render Function ===
export async function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = "";

  for (const doctor of doctors) {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(await card);
  }
}

// === Patient Signup Handler ===
window.signupPatient = async function () {
  try {
    const name = document.getElementById("name")?.value;
    const email = document.getElementById("email")?.value;
    const password = document.getElementById("password")?.value;
    const phone = document.getElementById("phone")?.value;
    const address = document.getElementById("address")?.value;

    const patient = { name, email, password, phone, address };
    const { success, message } = await patientSignup(patient);

    if (success) {
      alert(message);
      document.getElementById("modal").style.display = "none";
      window.location.reload();
    }
    else {
      alert(message);
    }
  } catch (error) {
    console.error("Signup failed:", error);
    alert("❌ An error occurred while signing up.");
  }
};

// === Patient Login Handler ===
window.loginPatient = async function () {
  try {
    const email = document.getElementById("patientEmail")?.value;
    const password = document.getElementById("patientPassword")?.value;

    const response = await patientLogin({ email, password });

    if (response.ok) {
      const result = await response.json();

      localStorage.setItem('token', result.token);
      localStorage.setItem('userRole', 'loggedPatient');

      selectRole('loggedPatient');
    }
    else {
      alert('❌ Invalid credentials!');
    }
  }
  catch (error) {
    console.error("Login failed:", error);
    alert("❌ Failed to login. Please try again.");
  }
};
