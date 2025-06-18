/*
  Import the base API URL from the config file
  This helps keep URLs consistent and maintainable
import { API_BASE_URL } from "../config/config.js";

  Define a constant for the appointment endpoint by combining base URL and path
const APPOINTMENT_API = `${API_BASE_URL}/appointments`;


   Function: getAllAppointments
   Purpose: Fetch all appointments for a doctor based on a specific date and patient name

  Export an async function named getAllAppointments that takes 3 parameters:
    - date: the selected date to filter appointments
    - patientName: the name of the patient to search for
    - token: an authentication token used for secure API access

  Step 1: Use fetch() to send a GET request to the endpoint, including date, patientName, and token in the URL path
  Step 2: Check if the response is not OK; if so, throw an error
  Step 3: If successful, parse and return the response JSON



   Function: bookAppointment
   Purpose: Send a POST request to book a new appointment
 
  Export an async function named bookAppointment that takes:
    - appointment: an object containing appointment details (e.g., patient info, date/time)
    - token: authentication token for API access

  Step 1: Send a POST request using fetch()
    - Set method to "POST"
    - Include "Content-Type: application/json" in headers
    - Convert the appointment object to JSON and add it to the request body

  Step 2: Parse the response JSON
  Step 3: Return an object with:
    - success: true if response was OK, otherwise false
    - message: the message from the API or a fallback error

  Step 4: Add a try-catch block to handle network errors
    - In case of failure, log the error and return a default error message



   Function: updateAppointment
   Purpose: Update an existing appointment via a PUT request
 
  Export an async function named updateAppointment that takes:
    - appointment: the updated appointment data
    - token: authentication token

  Step 1: Use fetch() to send a PUT request to the same endpoint as booking
    - Set method to "PUT"
    - Include appropriate headers and stringify the appointment object

  Step 2: Parse the API response and return success status and message
  Step 3: Handle errors in a try-catch block, similar to the booking function
*/

import { APPOINTMENTS_API } from "../config/config.js";

// const APPOINTMENT_API = `${BASE_API_URL}/appointments`;

/**
 * Fetch all appointments for a doctor filtered by date and patient name.
 * @param {null} date - The selected appointment date.
 * @param {string} patientName - The name of the patient to filter by.
 * @param {string} token - The authentication token.
 * @returns {Promise<Object>} - The list of appointments or an error.
 */
export async function getAllAppointments(date, patientName, token) {
    try {
        // const url = `${APPOINTMENTS_API}?date=${encodeURIComponent(date)}&patientName=${encodeURIComponent(patientName)}`;
        // const response = await fetch(url, {
        //     method: "GET",
        //     headers: {
        //         Authorization: `Bearer ${token}`
        //     }
        // });

        // const url = `${APPOINTMENTS_API}/${encodeURIComponent(date)}/${encodeURIComponent(patientName)}/${token}`;
        let url = null;

        if(date == null){
            url = `${APPOINTMENTS_API}/${encodeURIComponent(patientName)}/${token}`;
        }
        else{
            url = `${APPOINTMENTS_API}/${encodeURIComponent(date)}/${encodeURIComponent(patientName)}/${token}`;
        }

        const response = await fetch(url, {
            method: "GET",
            // headers: {
            //     Authorization: `Bearer ${token}`
            // }
        });

        if (!response.ok) {
            throw new Error("Failed to fetch appointments.");
        }

        return await response.json();
    }
    catch (error) {
        console.error("getAllAppointments error:", error);
        throw error;
    }
}

/**
 * Book a new appointment.
 * @param {Object} appointment - Appointment data to send.
 * @param {string} token - The authentication token.
 * @returns {Promise<{success: boolean, message: string}>}
 */
export async function bookAppointment(appointment, token) {
    try {
        const response = await fetch(APPOINTMENTS_API, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`
            },
            
            body: JSON.stringify(appointment)
        });

        const data = await response.json();

        return {
            success: response.ok,
            message: data.message || "Failed to book appointment."
        };
    } 
    catch (error) {
        console.error("bookAppointment error:", error);
        return {
            success: false,
            message: "An error occurred while booking the appointment."
        };
    }
}

/**
 * Update an existing appointment.
 * @param {Object} appointment - Updated appointment data.
 * @param {string} token - The authentication token.
 * @returns {Promise<{success: boolean, message: string}>}
 */
export async function updateAppointment(appointment, token) {
    try {
        const response = await fetch(APPOINTMENTS_API + `/${token}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                // Authorization: `Bearer ${token}`
            },
            body: JSON.stringify(appointment)
        });

        const data = await response.json();

        return {
            success: response.ok,
            message: data.message || "Failed to update appointment."
        };
    } 
    catch (error) {
        console.error("updateAppointment error:", error);
        return {
            success: false,
            message: "An error occurred while updating the appointment."
        };
    }
}
