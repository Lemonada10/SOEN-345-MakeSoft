import React from 'react';
import logoIcon from '../assets/airplane-ticket.png';

export default function Logo({ onClick }) {
  return (
    <div
      className="app-logo"
      onClick={onClick}
      role="button"
      tabIndex={0}
      onKeyPress={(e) => { if (e.key === 'Enter') onClick && onClick(); }}
      aria-label="Go to welcome page"
    >
      <img src={logoIcon} alt="" className="logo-mark logo-mark-img" />
      <div className="logo-text"> MakeSoft Reservations</div>
    </div>
  );
}
