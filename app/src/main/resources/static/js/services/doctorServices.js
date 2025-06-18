// doctorServices.js
import { DOCTORS_API } from '../config/config.js';  // Adjust path as needed

// const DOCTOR_API = `${BASE_API_URL}/doctor`;

/**
 * Fetch all doctors from the API.
 * @returns {Promise<Array>} Array of doctor objects or [] if error.
 */
export async function getDoctors() {
    try {
        const response = await fetch(DOCTORS_API);

        if (!response.ok) {
            console.error('Failed to fetch doctors: ', response.statusText);
            return [];
        }

        const data = await response.json();
        return data.doctors || [];
    }
    catch (error) {
        console.error('Error fetching doctors: ', error);
        return [];
    }
}

export async function getDoctor(id) {
    try {
        const response = await fetch(DOCTORS_API + `/${id}`, {
            method: 'GET',
        });

        if (!response.ok) {
            console.error('Failed to fetch doctor: ', response.statusText);
            return [];
        }

        const data = await response.json();
        return data.doctor;
    }
    catch (error) {
        console.error('Error fetching doctors: ', error);
        return [];
    }
}

/**
 * Delete a doctor by ID with authentication token.
 * @param {string} id - Doctor's unique ID.
 * @param {string} token - Authentication token.
 * @returns {Promise<{success: boolean, message: string}>}
 */
export async function deleteDoctor(id, token) {
    try {
        //const url = `${DOCTORS_API}/delete/${id}/${token}`;
        const url = `${DOCTORS_API}/${id}/${token}`;
        const response = await fetch(url, { method: 'DELETE' });

        if (!response.ok) {
            const errorText = await response.text();
            console.error('Failed to delete doctor:', errorText);
            return { success: false, message: errorText || 'Delete failed' };
        }

        const data = await response.json();
        return {
            success: data.success || true,
            message: data.message || 'Doctor deleted successfully',
        };
    } catch (error) {
        console.error('Error deleting doctor:', error);
        return { success: false, message: 'Error deleting doctor' };
    }
}

/**
 * Save a new doctor with authentication token.
 * @param {Object} doctor - Doctor data object.
 * @param {string} token - Authentication token.
 * @returns {Promise<{success: boolean, message: string}>}
 */
export async function saveDoctor(doctor, token) {
    try {
        // const url = `${DOCTORS_API}/save/${token}`;
        const url = `${DOCTORS_API}`;
        const response = await fetch(url, {
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`  // Include token in headers
            },
            body: JSON.stringify(doctor),
        });

        if (!response.ok) {
            const errorText = await response.text();
            console.error('Failed to save doctor:', errorText);
            return { success: false, message: errorText || 'Save failed' };
        }

        const data = await response.json();
        return {
            success: data.success || true,
            message: data.message || 'Doctor saved successfully',
        };
    }
    catch (error) {
        console.error('Error saving doctor:', error);
        return { success: false, message: 'Error saving doctor' };
    }
}

/**
 * Filter doctors by name, time, and specialty.
 * @param {string|null} name - Doctor's name filter (nullable).
 * @param {string|null} time - Available time filter (nullable).
 * @param {string|null} specialty - Specialty filter (nullable).
 * @returns {Promise<{doctors: Array}>} Filtered list or empty list on error.
 */
export async function filterDoctors(name, time, specialty) {
    try {
        // Use 'null' string or empty if parameters are falsy to avoid breaking URLs
        const n = name || 'null';
        const t = time || 'null';
        const s = specialty || 'null';

        //const url = `${DOCTORS_API}/filter/${encodeURIComponent(n)}/${encodeURIComponent(t)}/${encodeURIComponent(s)}`;
        const url = `${DOCTORS_API}/filter?name=${encodeURIComponent(n)}&time=${encodeURIComponent(t)}&specialty=${encodeURIComponent(s)}`;

        const response = await fetch(url);
        if (!response.ok) {
            console.error('Failed to filter doctors:', response.statusText);
            return { doctors: [] };
        }

        const data = await response.json();
        return { doctors: data || [] };
    }
    catch (error) {
        alert('Failed to filter doctors. Please try again.');
        console.error('Error filtering doctors:', error);
        return { doctors: [] };
    }
}
