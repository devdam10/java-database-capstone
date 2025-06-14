// Base API URL for backend calls (adjust as needed per your environment)
export const BASE_API_URL = "http://localhost:8080";

// Endpoints derived from the base URL
export const PRESCRIPTION_API = `${BASE_API_URL}/prescription`;
export const APPOINTMENTS_API = `${BASE_API_URL}/appointments`;
export const DOCTORS_API = `${BASE_API_URL}/doctors`;
export const PATIENTS_API = `${BASE_API_URL}/patients`;
export const ADMIN_API = `${BASE_API_URL}/admin`;

// Any other global config constants can be added here
// e.g., pagination defaults, token keys, etc.

export const TOKEN_KEY = "token";      // Key name for localStorage token
export const USER_ROLE_KEY = "userRole";  // Key name for storing user role
