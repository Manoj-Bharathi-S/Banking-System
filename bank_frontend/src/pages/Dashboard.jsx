import React, { useEffect, useState, useCallback } from 'react';
import { useAuth } from '../context/AuthContext';
import { api } from '../services/api';
import { LogOut, ArrowUpRight, ArrowDownRight, RefreshCcw, User as UserIcon } from 'lucide-react';
import TransactionModal from '../components/TransactionModal';
import bankLogo from '../assets/bank_log.png';

export default function Dashboard() {
  const { user, logout } = useAuth();
  const [balance, setBalance] = useState(null);
  const [loading, setLoading] = useState(true);
  const [modalConfig, setModalConfig] = useState({ isOpen: false, type: null });

  const fetchBalance = useCallback(async () => {
    try {
      setLoading(true);
      const bal = await api.getBalance();
      // Assume bal is the number if it's returning a double, or an object { balance: number }
      setBalance(typeof bal === 'object' ? bal.balance : bal);
    } catch (error) {
      console.error("Failed to fetch balance", error);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchBalance();
  }, [fetchBalance]);

  const handleTransactionSuccess = () => {
    setModalConfig({ isOpen: false, type: null });
    fetchBalance(); // refresh balance after transaction
  };

  if (!user) {
    return (
      <div className="app-container" style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', color: 'white' }}>
        <div style={{ textAlign: 'center' }}>
          <h2>Loading Dashboard...</h2>
          <p style={{ color: 'var(--text-muted)' }}>Fetching profile details</p>
        </div>
      </div>
    );
  }

  return (
    <div className="app-container">
      <nav className="navbar">
        <div className="navbar-brand" style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <img src={bankLogo} alt="MBI Logo" style={{ height: '32px', width: 'auto', objectFit: 'contain' }} />
          <span style={{ fontSize: '1.5rem', fontWeight: 'bold', color: 'white', letterSpacing: '1px' }}>MBI</span>
        </div>
        <div className="navbar-links">
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginRight: '16px' }}>
            <UserIcon size={18} color="var(--text-muted)" />
            <span style={{ fontWeight: 500 }}>{user.username || 'User'}</span>
          </div>
          <button onClick={logout} className="glass-button secondary" style={{ padding: '8px 16px', width: 'auto' }}>
            <LogOut size={18} />
            Logout
          </button>
        </div>
      </nav>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem' }}>
        <div className="glass-panel animate-fade-in" style={{ padding: '2rem', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
          <div>
            <h3 style={{ color: 'var(--text-muted)', marginBottom: '0.5rem', fontWeight: 500 }}>Available Balance</h3>
            <div style={{ fontSize: '3.5rem', fontWeight: 700, fontFamily: 'var(--font-heading)', color: 'white' }}>
              {loading && balance === null ? '...' : `$${Number(balance || 0).toFixed(2)}`}
            </div>
          </div>
          
          <div style={{ display: 'flex', gap: '1rem', marginTop: '2rem' }}>
            <button className="glass-button" onClick={() => setModalConfig({ isOpen: true, type: 'deposit' })}>
              <ArrowDownRight size={18} />
              Deposit
            </button>
            <button className="glass-button secondary" onClick={() => setModalConfig({ isOpen: true, type: 'withdraw' })}>
              <ArrowUpRight size={18} />
              Withdraw
            </button>
            <button className="glass-button secondary" onClick={() => setModalConfig({ isOpen: true, type: 'transfer' })}>
              <RefreshCcw size={18} />
              Transfer
            </button>
          </div>
        </div>

        <div className="glass-panel animate-fade-in" style={{ padding: '2rem', animationDelay: '0.1s' }}>
          <h3 style={{ marginBottom: '1.5rem', borderBottom: '1px solid var(--border-color)', paddingBottom: '1rem' }}>
            Account Details
          </h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
            <div>
              <div style={{ fontSize: '0.9rem', color: 'var(--text-muted)', marginBottom: '4px' }}>User ID</div>
              <div style={{ fontSize: '1.1rem', fontWeight: 500 }}>{user.id != null ? user.id : 'N/A'}</div>
            </div>
            <div>
              <div style={{ fontSize: '0.9rem', color: 'var(--text-muted)', marginBottom: '4px' }}>Username</div>
              <div style={{ fontSize: '1.1rem', fontWeight: 500 }}>{user.username || 'N/A'}</div>
            </div>
            <div>
              <div style={{ fontSize: '0.9rem', color: 'var(--text-muted)', marginBottom: '4px' }}>Email Address</div>
              <div style={{ fontSize: '1.1rem', fontWeight: 500 }}>{user.email || 'N/A'}</div>
            </div>
            <div>
              <div style={{ fontSize: '0.9rem', color: 'var(--text-muted)', marginBottom: '4px' }}>Account Number</div>
              <div style={{ fontSize: '1.1rem', fontWeight: 500 }}>{user.ac != null ? user.ac : 'N/A'}</div>
            </div>
          </div>
        </div>
      </div>

      <TransactionModal 
        isOpen={modalConfig.isOpen} 
        type={modalConfig.type} 
        onClose={() => setModalConfig({ isOpen: false, type: null })}
        onSuccess={handleTransactionSuccess}
        currentUserId={user.id}
      />
    </div>
  );
}
