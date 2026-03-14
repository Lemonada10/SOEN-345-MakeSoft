import React from 'react';
import { useNavigate } from 'react-router-dom';

export default function Welcome({ onChoose, user, onLogout }) {
  const navigate = useNavigate();

  return (
    <div className="form">
      <div className="form-card">
        <div className="form-title">Welcome to MakeSoft Reservations</div>
        <p className="helper">Browse and reserve tickets for events such as concerts, sports, and shows. Register using your email or phone number, make reservations, and receive confirmations via email or SMS. Administrators can add, edit and cancel events.</p>
        <div className="welcome-actions">
          {!user ? (
            <>
              <button className="btn btn-primary" onClick={() => onChoose && onChoose('signin')}>Sign In</button>
              <button className="btn btn-ghost" onClick={() => onChoose && onChoose('signup')}>Sign Up</button>
            </>
          ) : (
            <>
              <button className="btn btn-primary" onClick={() => navigate('/events')}>Events</button>
              {(user.role === 'admin' || user.role === 'instructor') ? (
                <button className="btn btn-ghost" onClick={() => navigate('/admin/events')}>Admin – Events</button>
              ) : (
                <button className="btn btn-ghost" onClick={() => navigate('/reservations')}>My reservations</button>
              )}
              <button className="btn btn-ghost" onClick={onLogout}>Sign out</button>
            </>
          )}
        </div>
      </div>
    </div>
  );
}
