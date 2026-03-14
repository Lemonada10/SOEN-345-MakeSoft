import React, { useState } from 'react';
import { registerUser } from '../services/api';

export default function SignUp({ onSuccess, onSwitchMode }) {
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('customer');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  async function handleSubmit(e) {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      // call backend register endpoint
      const name = email ? email.split('@')[0] : '';
      const payload = { name, email, phoneNumber: phone, password, role };
      const created = await registerUser(payload);
      // call onSuccess with created user info
      if (onSuccess) onSuccess({ email: created.email, id: created.id, role: created.role });
    } catch (err) {
      setError(err.message || 'Failed to sign up');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="form">
      <div className="form-card">
        <form onSubmit={handleSubmit}>
          <div className="form-title">Create an account</div>

          <div className="form-row">
            <label htmlFor="signup-email">Email</label>
            <input id="signup-email" value={email} onChange={e => setEmail(e.target.value)} type="email" placeholder="you@example.com" />
          </div>

          <div className="form-row">
            <label htmlFor="signup-phone">Phone number</label>
            <input id="signup-phone" value={phone} onChange={e => setPhone(e.target.value)} type="tel" placeholder="123-456-7890" />
          </div>

          <div className="form-row">
            <label htmlFor="signup-password">Password</label>
            <input id="signup-password" value={password} onChange={e => setPassword(e.target.value)} type="password" placeholder="••••••" />
          </div>

          <div className="form-row">
            <label className="account-type">Account type</label>
            <div className="radio-group">
              <label><input type="radio" name="role" value="customer" checked={role==='customer'} onChange={() => setRole('customer')} /> Customer</label>
              <label><input type="radio" name="role" value="admin" checked={role==='admin'} onChange={() => setRole('admin')} /> Admin</label>
            </div>
          </div>

          {error && <div style={{ color: 'red', textAlign: 'center', marginBottom: 8 }}>{error}</div>}

          <div className="form-actions">
            <button type="submit" className="btn btn-primary" disabled={loading}>{loading ? 'Creating...' : 'Sign Up'}</button>
          </div>

          <div className="helper">Already have an account? <button type="button" className="btn btn-ghost" onClick={() => onSwitchMode && onSwitchMode('signin')} style={{ marginLeft: 8 }}>Sign In</button></div>
        </form>
      </div>
    </div>
  );
}
