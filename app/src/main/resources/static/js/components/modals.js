// modals.js
import { saveDoctor } from "../services/doctorServices.js";
import { refreshDoctorList } from "../adminDashboard.js";

export function openModal(type) {
  let modalContent = '';

  if (type === 'addDoctor') {
    modalContent = `
        <form id="addDoctorForm">
             <h2>Add Doctor</h2>
             <input type="text" id="doctorName" placeholder="Doctor Name" class="input-field">
             
             <select id="specialization" class="input-field select-dropdown">
                  <option value="">Specialization</option>
                  <option value="cardiology">Cardiology</option>
                  <option value="dentistry">Dentistry</option>
                  <option value="dermatology">Dermatology</option>
                  <option value="ent">ENT Specialist</option>
                  <option value="gastroenterology">Gastroenterology</option>
                  <option value="general">General Physician</option>
                  <option value="gynecology">Gynecology</option>
                  <option value="neurology">Neurology</option>
                  <option value="oncology">Oncology</option>
                  <option value="ophthalmology">Ophthalmology</option>
                  <option value="orthopedics">Orthopedics</option>
                  <option value="pediatrics">Pediatrics</option>
                  <option value="psychiatry">Psychiatry</option>
                  <option value="urology">Urology</option>
            </select>
            
            <input type="email" id="doctorEmail" placeholder="Email" class="input-field">
            <input type="password" id="doctorPassword" placeholder="Password" class="input-field">
            <input type="text" id="doctorPhone" placeholder="Mobile No." class="input-field">
            <div class="availability-container">
                <span class="availabilityLabel" style="color: #222222">Select Availability:</span>
                <div class="checkbox-group">
                  <label><input type="checkbox" name="availability" value="09:00-10:00"> 9:00 AM - 10:00 AM</label>
                  <label><input type="checkbox" name="availability" value="10:00-11:00"> 10:00 AM - 11:00 AM</label>
                  <label><input type="checkbox" name="availability" value="11:00-12:00"> 11:00 AM - 12:00 PM</label>
                  <label><input type="checkbox" name="availability" value="12:00-13:00"> 12:00 PM - 1:00 PM</label>
                  <label><input type="checkbox" name="availability" value="13:00-14:00"> 1:00 PM - 2:00 PM</label>
                  <label><input type="checkbox" name="availability" value="14:00-15:00"> 2:00 PM - 3:00 PM</label>
                  <label><input type="checkbox" name="availability" value="15:00-16:00"> 3:00 PM - 4:00 PM</label>
                  <label><input type="checkbox" name="availability" value="16:00-17:00"> 4:00 PM - 5:00 PM</label>
              </div>
            </div>
            
            <button class="dashboard-btn" type="submit" id="saveDoctorBtn">Save</button>
        </form>
      `;
  }
  else if (type === 'patientLogin') {
    modalContent = `
        <h2>Patient Login</h2>
        <input type="text" id="patientEmail" placeholder="Email" class="input-field">
        <input type="password" id="patientPassword" placeholder="Password" class="input-field">
        <button class="dashboard-btn patientBtn" id="loginBtn">Login</button>
      `;
  }
  else if (type === "patientSignup") {
    modalContent = `
      <h2>Patient Signup</h2>
      <input type="text" id="name" placeholder="Name" class="input-field">
      <input type="email" id="email" placeholder="Email" class="input-field">
      <input type="password" id="password" placeholder="Password" class="input-field">
      <input type="text" id="phone" placeholder="Phone" class="input-field">
      <input type="text" id="address" placeholder="Address" class="input-field">
      <button class="dashboard-btn patientBtn" id="signupBtn">Signup</button>
    `;
  }
  else if (type === 'adminLogin') {
    modalContent = `
        <h2>Admin Login</h2>
        <input type="text" id="adminUsername" name="username" placeholder="Username" class="input-field">
        <input type="password" id="adminPassword" name="password" placeholder="Password" class="input-field">
        <button class="dashboard-btn" id="adminLoginBtn" >Login</button>
      `;
  }
  else if (type === 'doctorLogin') {
    modalContent = `
        <h2>Doctor Login</h2>
        <input type="text" id="doctorEmail" placeholder="Email" class="input-field">
        <input type="password" id="doctorPassword" placeholder="Password" class="input-field">
        <button class="dashboard-btn" id="doctorLoginBtn" >Login</button>
      `;
  }

  document.getElementById('modal-body').innerHTML = modalContent;
  document.getElementById('modal').style.display = 'block';

  document.getElementById('close-modal').onclick = () => {
    document.getElementById('modal').style.display = 'none';
  };

  if (type === "patientSignup") {
    document.getElementById("signupBtn").addEventListener("click", signupPatient);
  }

  if(type === "patientLogin"){
    document.getElementById("loginBtn").addEventListener("click" , loginPatient);
    //document.getElementById("loginBtn").addEventListener("click" , patientLoginHandler);
  }

  if(type === 'addDoctor'){
    document.getElementById('saveDoctorBtn').addEventListener('click' , adminAddDoctor);
  }

  if (type === 'adminLogin') {
    document.getElementById('adminLoginBtn').addEventListener('click', adminLoginHandler);
  }

  if (type === 'doctorLogin') {
    document.getElementById('doctorLoginBtn').addEventListener('click', doctorLoginHandler);
  }
}


async function adminAddDoctor(event) {
  event.preventDefault(); // Prevent default form submission

  // Get values from form inputs
  const name = document.getElementById('doctorName').value.trim();
  const email = document.getElementById('doctorEmail').value.trim();
  const password = document.getElementById('doctorPassword').value;
  const phone = document.getElementById('doctorPhone').value.trim();
  const specialty = document.getElementById('specialization').value;

  // Collect availability values
  const availableTimes = Array.from(document.querySelectorAll('input[name="availability"]:checked')).map(cb => cb.value);

  // You can retrieve token from local storage or session
  const token = localStorage.getItem('token'); // Example

  // Validation (basic)
  if (!name || !email || !password || !phone || !specialty || availableTimes.length === 0) {
    alert("Please fill out all required fields.");
    return;
  }

  // Create doctor object
  const doctor = {
    name,
    email,
    password,
    phone,
    specialty,
    availableTimes
  };

  // Call saveDoctor API
  const result = await saveDoctor(doctor, token);

  if (result.success) {
    alert("Doctor added successfully!");
    document.getElementById('modal').style.display = 'none'; // Close modal
    // Optionally refresh doctor list

    await refreshDoctorList();
  }
  else {
    alert("Failed to add doctor: " + result.message);
  }
}

