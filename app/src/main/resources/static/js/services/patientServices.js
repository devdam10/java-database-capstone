/*
  Import the base API URL from the config file
  Create a constant named PATIENT_API by appending '/patient' to the base URL


  Function  patientSignup
  Purpose  Register a new patient in the system

     Send a POST request to PATIENT_API with 
    - Headers  Content-Type set to 'application/json'
    - Body  JSON.stringify(data) where data includes patient details

    Convert the response to JSON and check for success
    - If response is not OK, throw an error with the message from the server

    Return an object with 
    - success  true or false
    - message  feedback from the server

    Use try-catch to handle network or API errors
    - Log errors and return a failure response with the error message


  Function  patientLogin
  Purpose  Authenticate a patient with email and password

     Send a POST request to `${PATIENT_API}/login`
    - Include appropriate headers and the login data in JSON format

    Return the raw fetch response to be handled where the function is called
    - The caller will check the response status and process the token or error


  Function  getPatientData
  Purpose  Fetch basic patient information using a token

     Send a GET request to `${PATIENT_API}/${token}`
    Parse the response and return the 'patient' object if response is OK
    If there's an error or the response is not OK, return null
    Catch and log any network or server errors


  Function  getPatientAppointments
  Purpose  Retrieve appointment data for a specific user (doctor or patient)

     Send a GET request to `${PATIENT_API}/${id}/${user}/${token}`
    - 'id' is the user’s ID, 'user' is either 'doctor' or 'patient', and 'token' is for auth

    Parse the response and return the 'appointments' array if successful
    If the response fails or an error occurs, return null
    Log any errors for debugging


  Function  filterAppointments
  Purpose  Retrieve filtered appointments based on condition and patient name

   Send a GET request to `${PATIENT_API}/filter/${condition}/${name}/${token}`
    - This allows filtering based on status or search criteria

   Parse the response if it's OK and return the data
   If the response fails, return an empty appointments array
   Use a try-catch to handle errors gracefully and notify the user
*/

// patientServices.js
import { PATIENTS_API } from '../config/config.js';  // Adjust path if needed

// const PATIENT_API = `${BASE_API_URL}/patient`;

/**
 * Register a new patient.
 * @param {Object} data - Patient signup details (name, email, password, etc.)
 * @returns {Promise<{success: boolean, message: string}>}
 */
export async function patientSignup(data) {
    try {
        const response = await fetch(PATIENTS_API, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data),
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
    // console.log('Login data:', data);

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
        const response = await fetch(`${PATIENTS_API}/${token}`);
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
        const url = `${PATIENTS_API}/filter?patientId=${patientId}&doctorId=${doctorId}&token=${token}`;
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
 * @param {string} name - Patient name filter
 * @param {string} token - Authentication token
 * @returns {Promise<Array>} Filtered appointments or empty array on failure
 */
export async function filterAppointments(condition, name, token) {
    try {
        const url = `${PATIENTS_API}/filter/${encodeURIComponent(condition)}/${encodeURIComponent(name)}/${encodeURIComponent(token)}`;
        const response = await fetch(url);
        if (!response.ok) {
            console.error('Failed to filter appointments:', response.statusText);
            return [];
        }

        const data = await response.json();
        return data.appointments || [];
    } catch (error) {
        alert('Failed to filter appointments. Please try again.');
        console.error('Error filtering appointments:', error);
        return [];
    }
}
