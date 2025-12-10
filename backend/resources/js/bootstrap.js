import axios from 'axios';
window.axios = axios;

window.axios.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
window.axios.defaults.withCredentials = true; // Send cookies with requests
window.axios.defaults.withXSRFToken = true; // Include CSRF token in headers
