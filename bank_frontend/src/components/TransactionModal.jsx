import React, { useState } from 'react';
import { api } from '../services/api';
import { X } from 'lucide-react';

export default function TransactionModal({ type, isOpen, onClose, onSuccess, currentUserId }) {
  const [amount, setAmount] = useState('');
  const [toId, setToId] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      let parsedAmount = parseFloat(amount);
      if (type === 'withdraw' || type === 'transfer') {
        // Backend increases the balance, so we send a negative amount to decrease it
        parsedAmount = Math.abs(parsedAmount);
      }

      let payload = {
        amount: parsedAmount
      };

      if (type === 'deposit') {
        payload.to_id = currentUserId;
        payload.toId = currentUserId; // sending both snake_case and camelCase
        // payload.from_id = 0;
        // payload.fromId = 0;
        await api.deposit(payload);
      } else if (type === 'withdraw') {
        payload.from_id = currentUserId;
        // payload.fromId = currentUserId;
        // payload.to_id = 0;
        // payload.toId = 0;
        await api.withdraw(payload);
      } else if (type === 'transfer') {
        payload.from_id = currentUserId;
        // payload.fromId = currentUserId;
        payload.to_id = parseInt(toId, 10);
        payload.toId = parseInt(toId, 10);
        await api.transfer(payload);
      }
      
      setAmount('');
      setToId('');
      onSuccess();
    } catch (err) {
      setError(err.message || `Failed to process ${type}`);
    } finally {
      setLoading(false);
    }
  };

  const titleMap = {
    deposit: 'Make a Deposit',
    withdraw: 'Withdraw Funds',
    transfer: 'Transfer Money'
  };

  return (
    <div style={{
      position: 'fixed',
      top: 0, left: 0, right: 0, bottom: 0,
      background: 'rgba(0, 0, 0, 0.5)',
      backdropFilter: 'blur(4px)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      zIndex: 1000
    }}>
      <div className="glass-panel animate-fade-in" style={{ width: '100%', maxWidth: '400px', padding: '2rem', position: 'relative' }}>
        <button 
          onClick={onClose}
          style={{ position: 'absolute', top: '16px', right: '16px', background: 'transparent', border: 'none', color: 'var(--text-muted)', cursor: 'pointer' }}
        >
          <X size={24} />
        </button>

        <h3 style={{ marginBottom: '1.5rem', textAlign: 'center' }}>{titleMap[type]}</h3>

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Amount ($)</label>
            <input 
              type="number" 
              step="0.01"
              min="0.01"
              className="glass-input" 
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              required
            />
          </div>

          {type === 'transfer' && (
            <div className="form-group">
              <label className="form-label">Recipient User ID</label>
              <input 
                type="number" 
                className="glass-input" 
                value={toId}
                onChange={(e) => setToId(e.target.value)}
                required
              />
            </div>
          )}

          {error && <div className="error-message" style={{ marginBottom: '1rem' }}>{error}</div>}
          
          <button type="submit" className="glass-button" disabled={loading}>
            {loading ? 'Processing...' : 'Confirm'}
          </button>
        </form>
      </div>
    </div>
  );
}
