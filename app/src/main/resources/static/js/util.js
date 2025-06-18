// Store the user's role in localStorage
export function setRole(role) {
    localStorage.setItem("userRole", role);
}

// Retrieve the user's role from localStorage
export function getRole() {
    return localStorage.getItem("userRole");
}

// Remove the user's role from localStorage
export function clearRole() {
    localStorage.removeItem("userRole");
}

// Convert date and time to a readable format
export function convertDate(inputDate = null, inputTime = null) {
  if(inputDate){
    const date = new Date(inputDate);

    const options = { year: 'numeric', month: 'long', day: 'numeric' };
    return date.toLocaleDateString('en-US', options);
  }

  if(inputTime){
    const time = new Date(inputTime);
    return time.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }
  
  return '';
}
