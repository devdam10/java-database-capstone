
import { convertDate } from "../util.js";

export function createPatientRecordRow(patient) {
    // Create a new table row
    const row = document.createElement('tr');

    // Set the inner HTML with table data cells
    row.innerHTML = `
      <td>${patient.appointmentDate}</td>
      <td>${patient.id}</td>
      <td>${patient.patientId}</td>
      <td>
        <img 
          src="/assets/images/edit/edit.png"  
          alt="View Prescription" 
          class="prescription-btn" 
          data-id="${patient.id}" 
          style="cursor: pointer; width: 20px;"
        />
      </td>
    `;

    // Select the image and bind click event
    const btn = row.querySelector('.prescription-btn');
    if (btn) {
        btn.addEventListener('click', () => {
            const appointmentId = btn.getAttribute('data-id');
            window.location.href = `addPrescription.html?mode=view&appointmentId=${appointmentId}`;
        });
    }

    // Return the constructed row
    return row;
}

export function createPatientRecordRow2(appointment) {
    // Create a new table row
    const row = document.createElement('tr');

    // Set the inner HTML with table data cells
    row.innerHTML = `
      <td>${appointment.patient.id}</td>
      <td>${appointment.id}</td>
      <td>${convertDate(appointment.appointmentDate)}</td>
      <td>
        <img 
          src="/assets/images/edit/edit.png"  
          alt="View Prescription" 
          class="prescription-btn" 
          data-id="${appointment.patient.id}" 
          style="cursor: pointer; width: 20px;"
        />
      </td>
    `;

    // Select the image and bind click event
    const btn = row.querySelector('.prescription-btn');
    if (btn) {
        btn.addEventListener('click', () => {
            const appointmentId = btn.getAttribute('data-id');
            window.location.href = `../../pages/addPrescription.html?mode=view&appointmentId=${appointmentId}`;
        });
    }

    // Return the constructed row
    return row;
}
