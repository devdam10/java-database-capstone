/*
  Import the base API URL from the config file
  Define a constant PRESCRIPTION_API by appending '/prescription' to the base URL


  Function: savePrescription
  Purpose: Save a new prescription to the backend

  Step 1: Use fetch() to send a POST request to `${PRESCRIPTION_API}/${token}`
    - Include headers with 'Content-Type' set to 'application/json'
    - Convert the prescription object to JSON and pass it in the body

  Step 2: Parse the JSON response from the server
  Step 3: Return an object with:
    - success: true if response is OK
    - message: message from the backend (e.g., "Prescription saved")

  Step 4: Wrap in a try-catch block to handle errors
    - Log the error and return a failure response with a message


  Function: getPrescription
  Purpose: Retrieve the prescription for a specific appointment

  Step 1: Use fetch() to send a GET request to:
    - `${PRESCRIPTION_API}/${appointmentId}/${token}`
    - Include the 'Content-Type' header as 'application/json'

  Step 2: Check if the response is not OK
    - If not, parse the error JSON and throw a descriptive error

  Step 3: If successful, parse the response JSON
    - This will contain the prescription object

  Step 4: Return the prescription data

  Step 5: Catch and log any errors, and rethrow them so the caller can handle them
*/

import { API_BASE_URL } from "../config/config.js";

const PRESCRIPTION_API = `${API_BASE_URL}/prescription`;

/**
 * Save a new prescription to the backend.
 * @param {Object} prescription - The prescription data to be saved.
 * @param {string} token - Authentication token.
 * @returns {Promise<{success: boolean, message: string}>}
 */
export async function savePrescription(prescription, token) {
    try {
        const response = await fetch(`${PRESCRIPTION_API}/${token}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(prescription)
        });

        const data = await response.json();

        return {
            success: response.ok,
            message: data.message || "Prescription saved"
        };
    } catch (error) {
        console.error("savePrescription error:", error);
        return {
            success: false,
            message: "Failed to save prescription"
        };
    }
}

/**
 * Retrieve the prescription for a specific appointment.
 * @param {string} appointmentId - ID of the appointment.
 * @param {string} token - Authentication token.
 * @returns {Promise<Object>} - The prescription data.
 */
export async function getPrescription(appointmentId, token) {
    try {
        const response = await fetch(`${PRESCRIPTION_API}/${appointmentId}/${token}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || "Failed to fetch prescription");
        }

        const prescription = await response.json();
        return prescription;
    } catch (error) {
        console.error("getPrescription error:", error);
        throw error;
    }
}
