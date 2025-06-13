// Base API URL for backend calls (adjust as needed per your environment)
export const BASE_API_URL = "https://your-api-domain.com/api";

// Endpoints derived from the base URL
export const PRESCRIPTION_API = `${BASE_API_URL}/prescription`;
export const APPOINTMENTS_API = `${BASE_API_URL}/appointments`;
export const DOCTORS_API = `${BASE_API_URL}/doctors`;
export const PATIENTS_API = `${BASE_API_URL}/patients`;

// Any other global config constants can be added here
// e.g., pagination defaults, token keys, etc.

export const TOKEN_KEY = "token";      // Key name for localStorage token
export const USER_ROLE_KEY = "userRole";  // Key name for storing user role
