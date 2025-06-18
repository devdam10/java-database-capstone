import { openModal } from './components/modals.js';
import { getDoctors, filterDoctors, saveDoctor } from './services/doctorServices.js';
import { createDoctorCard } from './components/doctorCard.js';


/**
 * Fetch all doctors and display them as cards
 */
async function loadDoctorCards() {
    try {
        const doctors = await getDoctors();

        const contentDiv = document.getElementById('content');

        if (!contentDiv) {
            //console.log('Element with id="content" not found in DOM.');
            return;
        }

        contentDiv.innerHTML = '';

        for (const doctor of doctors) {
            const card = await createDoctorCard(doctor);
            contentDiv.appendChild(card);
        }

    } catch (error) {
        console.error('Error loading doctors:', error);
    }
}

/**
 * Filter doctors based on search input and dropdown selections
 */
async function filterDoctorsOnChange() {
    try {
        const name = document.getElementById('searchBar').value.trim() || null;
        const time = document.getElementById('timeFilter')?.value || document.getElementById('filterTime')?.value || null;
        const specialty = document.getElementById('specialtyFilter')?.value || document.getElementById('filterSpecialty')?.value || null;

        const filteredDoctors = await filterDoctors(name, time, specialty);

        const contentDiv = document.getElementById('content');
        contentDiv.innerHTML = '';

        if (filteredDoctors && filteredDoctors.doctors.length > 0) {
            for (const doctor of filteredDoctors.doctors) {
                const card = await createDoctorCard(doctor); // ✅ await the async function
                contentDiv.appendChild(card);
            }
        }
        else {
            contentDiv.innerHTML = '<p>No doctors found with the given filters.</p>';
        }
    }
    catch (error) {
        alert('Failed to filter doctors. Please try again.');
        console.error('Filter error:', error);
    }
}

/**
 * Render an array of doctors as cards (utility)
 * @param {Array} doctors - Array of doctor objects
 */
async function renderDoctorCards(doctors) {
    const contentDiv = document.getElementById('content');
    contentDiv.innerHTML = '';

    // doctors.forEach(doctor => {
    //     const card = createDoctorCard(doctor);
    //     contentDiv.appendChild(card);
    // });

    for (const doctor of doctors) {
        const card = await createDoctorCard(doctor); // ✅ await the async function
        contentDiv.appendChild(card);
    }
}

/**
 * Collect modal form data and add a new doctor via API
 * Called on form submission (bind this in your modal form)
 */
async function adminAddDoctor(event) {
    event.preventDefault();

    // Collect input values
    const name = document.getElementById('docName').value.trim();
    const email = document.getElementById('docEmail').value.trim();
    const phone = document.getElementById('docPhone').value.trim();
    const password = document.getElementById('docPassword').value.trim();
    const specialty = document.getElementById('docSpecialty').value.trim();

    // Collect availability times (checkboxes example)
    const availabilityCheckboxes = document.querySelectorAll('input[name="docAvailability"]:checked');
    const availability = Array.from(availabilityCheckboxes).map(cb => cb.value);

    // Validate token
    const token = localStorage.getItem('token');
    if (!token) {
        alert('You must be logged in as admin to perform this action.');
        return;
    }

    // Build doctor object
    const doctor = {
        name,
        email,
        phone,
        password,
        specialty,
        availability
    };

    try {
        const result = await saveDoctor(doctor, token);

        if (result.success) {
            alert('Doctor added successfully!');
            // Close modal (assuming openModal has a close function or implement accordingly)
            openModal('addDoctor', 'close');
            // Reload doctor list
            await loadDoctorCards();
        } else {
            alert(`Failed to add doctor: ${result.message}`);
        }
    } catch (error) {
        alert('An error occurred while adding the doctor. Please try again.');
        console.error('Save doctor error:', error);
    }
}


async function refreshDoctorList(){
    await filterDoctorsOnChange();
}

// Open "Add Doctor" modal on button click
// Load all doctors on page load
// Attach listeners for search and filters
document.addEventListener('DOMContentLoaded', () => {
    const addBtn = document.getElementById('addDocBtn');
    const searchInput = document.getElementById('searchBar');
    const timeFilter = document.getElementById('timeFilter');
    const specialtyFilter = document.getElementById('specialtyFilter');

    if (addBtn) addBtn.addEventListener('click', () => openModal('addDoctor'));
    if (searchInput) searchInput.addEventListener('input', filterDoctorsOnChange);
    if (timeFilter) timeFilter.addEventListener('change', filterDoctorsOnChange);
    if (specialtyFilter) specialtyFilter.addEventListener('change', filterDoctorsOnChange);

    loadDoctorCards(); // Only call after DOM is ready
});



// Expose adminAddDoctor if you need to bind it from outside this file, e.g.:
//document.getElementById('addDoctorForm').addEventListener('submit', adminAddDoctor);
export { adminAddDoctor, refreshDoctorList };
