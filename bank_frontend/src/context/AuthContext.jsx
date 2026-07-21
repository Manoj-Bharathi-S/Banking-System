import React, { createContext, useContext, useState, useEffect } from 'react';
import { api, setToken as setApiToken, getToken } from '../services/api';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [token, setTokenState] = useState(getToken());
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const initAuth = async () => {
      if (token) {
        setApiToken(token);
        try {
          const profileData = await api.getProfile();
          setUser(profileData);
        } catch (error) {
          console.error("Token invalid or expired", error);
          logout();
        }
      }
      setLoading(false);
    };
    initAuth();
  }, [token]);

  const login = async (credentials) => {
    const response = await api.login(credentials);
    const newToken = response.token || response; // fallback if it's just a string
    setApiToken(newToken);
    setTokenState(newToken);
    try {
      const profileData = await api.getProfile();
      setUser(profileData);
    } catch (err) {
      console.error("Failed to fetch profile after login", err);
    }
  };

  const register = async (userData) => {
    await api.register(userData);
    // After registration, depending on API, might need to login manually or it auto-logs in.
    // Assuming manual login required, we just return.
  };

  const logout = () => {
    setTokenState(null);
    setApiToken(null);
    setUser(null);
  };

  const updateProfile = (profileData) => {
    setUser(profileData);
  }

  return (
    <AuthContext.Provider value={{ token, user, loading, login, register, logout, updateProfile }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
