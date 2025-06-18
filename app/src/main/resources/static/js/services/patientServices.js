// patientServices.js
import { PATIENTS_API } from '../config/config.js';  // Adjust path if needed

// const PATIENT_API = `${BASE_API_URL}/patient`;

/**
 * Register a new patient.
 * @param {Object} patient - Patient signup details (name, email, password, etc.)
 * @returns {Promise<{success: boolean, message: string}>}
 */
export async function patientSignup(patient) {
    const token = localStorage.getItem('token'); // Example, adjust as needed

    try {
        const response = await fetch(PATIENTS_API, {
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}` // Include token if needed
             },
            body: JSON.stringify(patient),
        });

        const result = await response.json();

        if (!response.ok) {
            throw new Error(result.message || 'Signup failed');
        }

        return { success: true, message: result.message || 'Signup successful' };
    }
    catch (error) {
        console.error('Signup error:', error);
        return { success: false, message: error.message || 'Signup error' };
    }
}

/**
 * Authenticate patient login.
 * @param {Object} data - Login credentials {email, password}
 * @returns {Promise<Response>} Raw fetch response for caller to handle
 */
export async function patientLogin(data) {
    // Logging can be added here during development, but remove before production

    return fetch(`${PATIENTS_API}/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data),
    });
}

/**
 * Get logged-in patient details using token.
 * @param {string} token - Authentication token
 * @returns {Promise<Object|null>} Patient object or null if failed
 */
export async function getPatientData(token) {
    try {
        //const response = await fetch(`${PATIENTS_API}/${token}`);

        const response = await fetch(`${PATIENTS_API}/${token}`, {
            method: "GET",
            // headers: {
            //     Authorization: `Bearer ${token}`
            // }
        });

        if (!response.ok) {
            console.error('Failed to get patient data:', response.statusText);
            return null;
        }

        const data = await response.json();
        return data.patient || null;
    } catch (error) {
        console.error('Error fetching patient data:', error);
        return null;
    }
}

/**
 * Get patient appointments (for patient or doctor).
 * @param {string} patientId - patient
 * @param {string} token - Authentication token
 * @param {string} doctorId - doctor
 * @returns {Promise<Array|null>} Appointments array or null on failure
 */
export async function getPatientAppointments(patientId, doctorId, token) {
    try {
        // const url = `${PATIENTS_API}/${id}/${user}/${token}`;
        // const url = `${PATIENTS_API}/filter?patientId=${patientId}&doctorId=${doctorId}&token=${token}`;
        let url = null;

        if(doctorId && doctorId !== null) {
            url = `${PATIENTS_API}/filter?patientId=${patientId}&doctorId=${doctorId}&token=${token}`
        }
        else{
            url = `${PATIENTS_API}/appointments?id=${patientId}&token=${token}`;
        }

        //const response = await fetch(url);

        const response = await fetch(url, {
            method: "GET",
            // headers: {
            //     Authorization: `Bearer ${token}`
            // }
        });

        if (!response.ok) {
            console.error('Failed to get appointments:', response.statusText);
            return null;
        }

        const data = await response.json();
        return data.appointments || [];
    } catch (error) {
        console.error('Error fetching appointments:', error);
        return null;
    }
}

/**
 * Filter appointments by condition and patient name.
 * @param {string} condition - Appointment status filter ('pending', 'consulted', etc.)
 * @param {string} filter - Dropdown filter
 * @param {string} token - Authentication token
 * @returns {Promise<Array>} Filtered appointments or empty array on failure
 */
export async function filterAppointments(searchTerm, condition, token) {
    try {
        // const url = `${PATIENTS_API}/filter/${encodeURIComponent(condition)}/${encodeURIComponent(filter)}/${encodeURIComponent(token)}`;
        const url = `${PATIENTS_API}/filter/appointments?name=${searchTerm ? encodeURIComponent(searchTerm) : ''}&condition=${condition ? encodeURIComponent(condition): ''}&token=${encodeURIComponent(token)}`;

        const response = await fetch(url, {
            method: "GET",
            // headers: {
            //     Authorization: `Bearer ${token}`
            // }
        });

        if (!response.ok) {
            console.error('Failed to filter appointments:', response.statusText);
            return [];
        }

        const data = await response.json();

        return data.appointments || [];
    } 
    catch (error) {
        alert('Failed to filter appointments. Please try again.');
        console.error('Error filtering appointments:', error);
        return [];
    }
}
