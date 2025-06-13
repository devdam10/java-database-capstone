// import { createDoctorCard } from './components/doctorCard.js';
// import { openModal } from './components/modals.js';
// import { filterDoctors, getDoctors } from './services/doctorServices.js';
// import { patientLogin, patientSignup } from './services/patientServices.js';
//
//
//
// document.addEventListener("DOMContentLoaded", () => {
//   loadDoctorCards();
// });
//
// document.addEventListener("DOMContentLoaded", () => {
//   const btn = document.getElementById("patientSignup");
//   if (btn) {
//     btn.addEventListener("click", () => openModal("patientSignup"));
//   }
// });
//
// document.addEventListener("DOMContentLoaded", ()=> {
//   const loginBtn = document.getElementById("patientLogin")
//   if(loginBtn){
//     loginBtn.addEventListener("click" , ()=> {
//       openModal("patientLogin")
//     })
//   }
// })
//
// function loadDoctorCards() {
//   getDoctors()
//     .then(doctors => {
//       const contentDiv = document.getElementById("content");
//       contentDiv.innerHTML = "";
//
//       doctors.forEach(doctor => {
//         const card = createDoctorCard(doctor);
//         contentDiv.appendChild(card);
//       });
//     })
//     .catch(error => {
//       console.error("Failed to load doctors:", error);
//     });
// }
// // Filter Input
// document.getElementById("searchBar").addEventListener("input", filterDoctorsOnChange);
// document.getElementById("filterTime").addEventListener("change", filterDoctorsOnChange);
// document.getElementById("filterSpecialty").addEventListener("change", filterDoctorsOnChange);
//
//
//
// function filterDoctorsOnChange() {
//   const searchBar = document.getElementById("searchBar").value.trim();
//   const filterTime = document.getElementById("filterTime").value;
//   const filterSpecialty = document.getElementById("filterSpecialty").value;
//
//
//   const name = searchBar.length > 0 ? searchBar : null;
//   const time = filterTime.length > 0 ? filterTime : null;
//   const specialty = filterSpecialty.length > 0 ? filterSpecialty : null;
//
//   filterDoctors(name , time ,specialty)
//     .then(response => {
//       const doctors = response.doctors;
//       const contentDiv = document.getElementById("content");
//       contentDiv.innerHTML = "";
//
//       if (doctors.length > 0) {
//         console.log(doctors);
//         doctors.forEach(doctor => {
//           const card = createDoctorCard(doctor);
//           contentDiv.appendChild(card);
//         });
//       } else {
//         contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
//         console.log("Nothing");
//       }
//     })
//     .catch(error => {
//       console.error("Failed to filter doctors:", error);
//       alert("❌ An error occurred while filtering doctors.");
//     });
// }
//
// export function renderDoctorCards(doctors) {
//   const contentDiv = document.getElementById("content");
//       contentDiv.innerHTML = "";
//
//       doctors.forEach(doctor => {
//         const card = createDoctorCard(doctor);
//         contentDiv.appendChild(card);
//       });
//
// }
//
// window.signupPatient = async function () {
//   try {
//     const name = document.getElementById("name").value;
//     const email = document.getElementById("email").value;
//     const password = document.getElementById("password").value;
//     const phone = document.getElementById("phone").value;
//     const address = document.getElementById("address").value;
//
//     const data = { name, email, password, phone, address };
//     const { success, message } = await patientSignup(data);
//     if(success){
//       alert(message);
//       document.getElementById("modal").style.display = "none";
//       window.location.reload();
//     }
//     else alert(message);
//   } catch (error) {
//     console.error("Signup failed:", error);
//     alert("❌ An error occurred while signing up.");
//   }
// };
//
// window.loginPatient = async function(){
//   try {
//     const email = document.getElementById("email").value;
//     const password = document.getElementById("password").value;
//
//     const data = {
//       email,
//       password
//     }
//     console.log("loginPatient :: ", data)
//     const response = await patientLogin(data);
//     console.log("Status Code:", response.status);
//     console.log("Response OK:", response.ok);
//     if (response.ok) {
//       const result = await response.json();
//       console.log(result);
//       selectRole('loggedPatient');
//       localStorage.setItem('token', result.token )
//       window.location.href = '/pages/loggedPatientDashboard.html';
//     } else {
//       alert('❌ Invalid credentials!');
//     }
//   }
//   catch(error) {
//     alert("❌ Failed to Login : ",error);
//     console.log("Error :: loginPatient :: " ,error)
//   }
//
//
// }

import { createDoctorCard } from './components/doctorCard.js';
import { openModal } from './components/modals.js';
import { filterDoctors, getDoctors } from './services/doctorServices.js';
import { patientLogin, patientSignup } from './services/patientServices.js';

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
    renderDoctorCards(doctors);
  } catch (error) {
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
export function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = "";
  doctors.forEach(doctor => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

// === Patient Signup Handler ===
window.signupPatient = async function () {
  try {
    const name = document.getElementById("name")?.value;
    const email = document.getElementById("email")?.value;
    const password = document.getElementById("password")?.value;
    const phone = document.getElementById("phone")?.value;
    const address = document.getElementById("address")?.value;

    const data = { name, email, password, phone, address };
    const { success, message } = await patientSignup(data);

    if (success) {
      alert(message);
      document.getElementById("modal").style.display = "none";
      window.location.reload();
    } else {
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
    const email = document.getElementById("email")?.value;
    const password = document.getElementById("password")?.value;

    const response = await patientLogin({ email, password });

    if (response.ok) {
      const result = await response.json();
      localStorage.setItem('token', result.token);
      // Optional: Handle user role before redirecting
      // selectRole('loggedPatient'); // Only if this function is defined
      window.location.href = '/pages/loggedPatientDashboard.html';
    } else {
      alert('❌ Invalid credentials!');
    }
  } catch (error) {
    console.error("Login failed:", error);
    alert("❌ Failed to login. Please try again.");
  }
};
