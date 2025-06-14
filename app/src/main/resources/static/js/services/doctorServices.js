/*
  Import the base API URL from the config file
  Define a constant DOCTOR_API to hold the full endpoint for doctor-related actions


  Function: getDoctors
  Purpose: Fetch the list of all doctors from the API

   Use fetch() to send a GET request to the DOCTOR_API endpoint
   Convert the response to JSON
   Return the 'doctors' array from the response
   If there's an error (e.g., network issue), log it and return an empty array


  Function: deleteDoctor
  Purpose: Delete a specific doctor using their ID and an authentication token

   Use fetch() with the DELETE method
    - The URL includes the doctor ID and token as path parameters
   Convert the response to JSON
   Return an object with:
    - success: true if deletion was successful
    - message: message from the server
   If an error occurs, log it and return a default failure response


  Function: saveDoctor
  Purpose: Save (create) a new doctor using a POST request

   Use fetch() with the POST method
    - URL includes the token in the path
    - Set headers to specify JSON content type
    - Convert the doctor object to JSON in the request body

   Parse the JSON response and return:
    - success: whether the request succeeded
    - message: from the server

   Catch and log errors
    - Return a failure response if an error occurs


  Function: filterDoctors
  Purpose: Fetch doctors based on filtering criteria (name, time, and specialty)

   Use fetch() with the GET method
    - Include the name, time, and specialty as URL path parameters
   Check if the response is OK
    - If yes, parse and return the doctor data
    - If no, log the error and return an object with an empty 'doctors' array

   Catch any other errors, alert the user, and return a default empty result
*/

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

/**
 * Delete a doctor by ID with authentication token.
 * @param {string} id - Doctor's unique ID.
 * @param {string} token - Authentication token.
 * @returns {Promise<{success: boolean, message: string}>}
 */
export async function deleteDoctor(id, token) {
    try {
        const url = `${DOCTORS_API}/delete/${id}/${token}`;
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
        const url = `${DOCTORS_API}/save/${token}`;
        const response = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
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
    } catch (error) {
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

        const url = `${DOCTORS_API}/filter/${encodeURIComponent(n)}/${encodeURIComponent(t)}/${encodeURIComponent(s)}`;

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
