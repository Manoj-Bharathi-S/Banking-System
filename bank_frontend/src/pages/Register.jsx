import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { UserPlus } from 'lucide-react';

export default function Register() {
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    id: 0,
    ac: 0,
    balance: 0,
    version: 0
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);
  const { register } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);
    try {
      // Process form data appropriately
      const payload = {
        ...formData,
        id: Number(formData.id) || 0,
        ac: Number(formData.ac) || 0,
        balance: Number(formData.balance) || 0,
        version: Number(formData.version) || 0
      };
      await register(payload);
      setSuccess('Registration successful! Redirecting to login...');
      setTimeout(() => {
        navigate('/login');
      }, 2000);
    } catch (err) {
      setError(err.message || 'Failed to register');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="app-container" style={{ alignItems: 'center', justifyContent: 'center' }}>
      <div className="glass-panel" style={{ width: '100%', maxWidth: '400px', padding: '2rem' }}>
        <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
          <div style={{ display: 'flex', justifyContent: 'center', marginBottom: '1rem' }}>
            <div style={{ background: 'var(--primary-color)', padding: '12px', borderRadius: '50%' }}>
              <UserPlus color="white" size={24} />
            </div>
          </div>
          <h2>Create Account</h2>
          <p style={{ color: 'var(--text-muted)' }}>Join us today</p>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Username</label>
            <input 
              type="text" 
              name="username"
              className="glass-input" 
              value={formData.username}
              onChange={handleChange}
              required
            />
          </div>
          <div className="form-group">
            <label className="form-label">Email</label>
            <input 
              type="email" 
              name="email"
              className="glass-input" 
              value={formData.email}
              onChange={handleChange}
              required
            />
          </div>
          <div className="form-group">
            <label className="form-label">Password</label>
            <input 
              type="password" 
              name="password"
              className="glass-input" 
              value={formData.password}
              onChange={handleChange}
              required
            />
          </div>
          <div className="form-group" style={{ display: 'none' }}>
            {/* Hidden fields for required API structure but not needed from user usually. */}
            {/* Setting defaults in payload */}
          </div>
          
          {error && <div className="error-message" style={{ marginBottom: '1rem' }}>{error}</div>}
          {success && <div className="success-message" style={{ marginBottom: '1rem' }}>{success}</div>}
          
          <button type="submit" className="glass-button" disabled={loading}>
            {loading ? 'Creating Account...' : 'Register'}
          </button>
        </form>

        <div style={{ marginTop: '1.5rem', textAlign: 'center', fontSize: '0.9rem' }}>
          <span style={{ color: 'var(--text-muted)' }}>Already have an account? </span>
          <Link to="/login" style={{ color: 'var(--primary-color)', textDecoration: 'none', fontWeight: '500' }}>
            Sign in
          </Link>
        </div>
      </div>
    </div>
  );
}
