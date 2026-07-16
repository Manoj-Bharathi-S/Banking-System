const API_URL = '';

export const setToken = (token) => {
  if (token) {
    localStorage.setItem('bank_token', token);
  } else {
    localStorage.removeItem('bank_token');
  }
};

export const getToken = () => {
  return localStorage.getItem('bank_token');
};

const fetchWithAuth = async (endpoint, options = {}) => {
  const token = getToken();
  const headers = {
    'Content-Type': 'application/json',
    ...options.headers,
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const response = await fetch(`${API_URL}${endpoint}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    let errorMsg = 'An error occurred';
    try {
      const errorData = await response.json();
      errorMsg = errorData.message || errorData.error || errorMsg;
    } catch (e) {
      errorMsg = await response.text() || errorMsg;
    }
    throw new Error(errorMsg);
  }

  const text = await response.text();
  try {
    return text ? JSON.parse(text) : {};
  } catch (e) {
    return text;
  }
};

export const api = {
  login: (credentials) => fetchWithAuth('/auth/login', {
    method: 'POST',
    body: JSON.stringify(credentials)
  }),
  register: (userData) => fetchWithAuth('/auth/register', {
    method: 'POST',
    body: JSON.stringify(userData)
  }),
  getProfile: () => fetchWithAuth('/profile', {
    method: 'GET'
  }),
  getBalance: () => fetchWithAuth('/mybalance', {
    method: 'GET'
  }),
  deposit: (paymentData) => fetchWithAuth('/deposit', {
    method: 'POST',
    body: JSON.stringify(paymentData)
  }),
  withdraw: (paymentData) => fetchWithAuth('/withdraw', {
    method: 'POST',
    body: JSON.stringify(paymentData)
  }),
  transfer: (paymentData) => fetchWithAuth('/transfer', {
    method: 'POST',
    body: JSON.stringify(paymentData)
  })
};
