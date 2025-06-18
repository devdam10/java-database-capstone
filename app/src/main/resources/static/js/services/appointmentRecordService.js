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
