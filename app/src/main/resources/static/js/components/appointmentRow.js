export function createAppointmentRow(appointment) {
    // Create a new table row element
    const row = document.createElement('tr');

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
            window.location.href = `../../pages/addPrescription.html?id=${appointmentId}`;
        });
    }

    // Return the constructed row
    return row;
}