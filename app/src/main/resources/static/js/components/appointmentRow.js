/*
  Export a function named getAppointments that takes an appointment object as input
  Create a new table row element to represent one appointment
  Set the inner HTML of the row with appointment details:
      - Patient Name
      - Doctor Name
      - Appointment Date
      - Appointment Time
      - Action icon (edit) with appointment ID stored in data-id attribute
  Attach a click event listener to the edit icon (img with class 'prescription-btn')
  On click, redirect to 'addPrescription.html' passing appointment.id as a query parameter
    
  Return the constructed table row so it can be appended to the DOM elsewhere
    
*/

// export function getAppointments(appointment) {
//     // Create a new table row element
//     const row = document.createElement('tr');
//
//     console.log("appointment", appointment);
//
//     // Set the row's inner HTML with appointment details
//     row.innerHTML = `
//     <td>${appointment.patientName}</td>
//     <td>${appointment.doctorName}</td>
//     <td>${appointment.date}</td>
//     <td>${appointment.time}</td>
//     <td>
//       <img
//         src="/assets/icons/edit.svg"
//         alt="Edit Prescription"
//         class="prescription-btn"
//         data-id="${appointment.id}"
//         style="cursor:pointer; width:20px;"
//       />
//     </td>
//   `;
//
//     // Attach click event to the edit icon
//     const editBtn = row.querySelector('.prescription-btn');
//     if (editBtn) {
//         editBtn.addEventListener('click', () => {
//             const appointmentId = editBtn.getAttribute('data-id');
//             window.location.href = `addPrescription.html?id=${appointmentId}`;
//         });
//     }
//
//     // Return the constructed row
//     return row;
// }


export function createAppointmentRow(appointment) {
    // Create a new table row element
    const row = document.createElement('tr');

    console.log("appointment", appointment);

    // Set the row's inner HTML with appointment details
    row.innerHTML = `
    <td>${appointment.patientName}</td>
    <td>${appointment.doctorName}</td>
    <td>${appointment.date}</td>
    <td>${appointment.time}</td>
    <td>
      <img 
        src="/assets/icons/edit.svg" 
        alt="Edit Prescription" 
        class="prescription-btn" 
        data-id="${appointment.id}" 
        style="cursor:pointer; width:20px;"
      />
    </td>
  `;

    // Attach click event to the edit icon
    const editBtn = row.querySelector('.prescription-btn');
    if (editBtn) {
        editBtn.addEventListener('click', () => {
            const appointmentId = editBtn.getAttribute('data-id');
            window.location.href = `addPrescription.html?id=${appointmentId}`;
        });
    }

    // Return the constructed row
    return row;
}