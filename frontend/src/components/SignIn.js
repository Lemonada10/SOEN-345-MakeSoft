import React, { useState } from 'react';
import { getUserByEmail } from '../services/api';

export default function SignIn({ onSuccess, onSwitchMode }) {
  const [identifier, setIdentifier] = useState(''); // email or phone
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  async function handleSubmit(e) {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      // Look up user by email via backend (password check not yet implemented on backend)
      const user = await getUserByEmail(identifier);
      if (onSuccess) onSuccess({ identifier: user.email, id: user.id, email: user.email, role: user.role });
    } catch (err) {
      setError(err.message || 'Failed to sign in');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="form">
      <div className="form-card">
        <form onSubmit={handleSubmit}>
          <div className="form-title">Sign in to your account</div>

          <div className="form-row">
            <label htmlFor="signin-id">Email or Phone</label>
            <input id="signin-id" value={identifier} onChange={e => setIdentifier(e.target.value)} placeholder="email or phone" />
          </div>

          <div className="form-row">
            <label htmlFor="signin-password">Password</label>
            <input id="signin-password" value={password} onChange={e => setPassword(e.target.value)} type="password" />
          </div>

          {error && <div style={{ color: 'red', textAlign: 'center', marginBottom: 8 }}>{error}</div>}

          <div className="form-actions">
            <button type="submit" className="btn btn-primary" disabled={loading}>{loading ? 'Signing...' : 'Sign In'}</button>
          </div>

          <div className="helper">Don't have an account? <button type="button" className="btn btn-ghost" onClick={() => onSwitchMode && onSwitchMode('signup')} style={{ marginLeft: 8 }}>Sign Up</button></div>
        </form>
      </div>
    </div>
  );
}
