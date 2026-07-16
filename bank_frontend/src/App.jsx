import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';

const ProtectedRoute = ({ children }) => {
  const { token, loading } = useAuth();
  
  if (loading) return <div style={{display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', color: 'white'}}>Loading...</div>;
  if (!token) return <Navigate to="/login" />;
  
  return children;
};

const PublicRoute = ({ children }) => {
  const { token, loading } = useAuth();
  
  if (loading) return <div style={{display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', color: 'white'}}>Loading...</div>;
  if (token) return <Navigate to="/dashboard" />;
  
  return children;
};

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={
            <PublicRoute>
              <Login />
            </PublicRoute>
          } />
          <Route path="/register" element={
            <PublicRoute>
              <Register />
            </PublicRoute>
          } />
          <Route path="/dashboard" element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
          } />
          <Route path="*" element={<Navigate to="/dashboard" />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
