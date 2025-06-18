import { PRESCRIPTIONS_API } from "../config/config.js";

/**
 * Save a new prescription to the backend.
 * @param {Object} prescription - The prescription data to be saved.
 * @param {string} token - Authentication token.
 * @returns {Promise<{success: boolean, message: string}>}
 */
export async function savePrescription(prescription, token) {
    try {
        const response = await fetch(`${PRESCRIPTIONS_API}/${token}`, {
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
        const response = await fetch(`${PRESCRIPTIONS_API}/${appointmentId}/${token}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || "Failed to fetch prescription");
        }

        const data = await response.json();
        return data.prescriptions;
    }
    catch (error) {
        console.error("getPrescription error:", error);
        throw error;
    }
}
