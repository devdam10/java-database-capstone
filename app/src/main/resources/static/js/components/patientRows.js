export function createPatientRow(patient, appointmentId, doctorId) {
    // Create the table row
    const row = document.createElement('tr');

    // Set the row's inner HTML with the specified columns
    row.innerHTML = `
        <td class="patient-id" style="cursor: pointer; color: blue;" title="View Patient's Record">${patient.id}</td>
        <td>${patient.name}</td>
        <td>${patient.phone}</td>
        <td>${patient.email}</td>
        <td>
          <img 
            src="/assets/images/addPrescriptionIcon/addPrescription.png" 
            alt="Add Prescription" 
            class="prescription-btn" 
            data-id="${patient.id}" 
            data-mode="add"
            style="cursor: pointer; width: 20px;"
          />
          
          <img 
            src="/assets/images/edit/edit.png" 
            alt="Edit Prescription" 
            class="prescription-btn" 
            data-id="${patient.id}" 
            data-mode="view"
            style="cursor: pointer; width: 20px;"
          />
        </td>
     `;

    // Add click event to patient-id cell to redirect to patientRecord.html
    const patientIdCell = row.querySelector('.patient-id');
    if (patientIdCell) {
        patientIdCell.addEventListener('click', () => {
            //window.location.href = `patientRecord.html?patientId=${patient.id}&doctorId=${doctorId}`;
            window.location.href = `../../pages/patientRecord.html?patientId=${patient.id}&doctorId=${doctorId}`;
        });
    }

    // Add click event to prescription icon to redirect to addPrescription.html
    // const prescriptionBtn = row.querySelector('.prescription-btn');
    // if (prescriptionBtn) {
    //     prescriptionBtn.addEventListener('click', () => {
    //         const mode =  prescriptionBtn.getAttribute("data-mode");
    //         window.location.href = `../../pages/addPrescription.html?appointmentId=${appointmentId}&patientName=${encodeURIComponent(patient.name)}&mode=${mode}`;
    //     });
    // }
    const prescriptionBtns = row.querySelectorAll('.prescription-btn');
    prescriptionBtns.forEach((btn) => {
        btn.addEventListener('click', () => {
            const mode = btn.getAttribute("data-mode");
            window.location.href = `../../pages/addPrescription.html?appointmentId=${appointmentId}&patientName=${encodeURIComponent(patient.name)}&mode=${mode}`;
        });
    });


    // Return the constructed row
    return row;
}
