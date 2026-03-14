import React from 'react';

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
      <svg className="logo-mark" viewBox="0 0 64 64" aria-hidden="true" focusable="false">
        <defs>
          <linearGradient id="logoGrad" x1="0%" y1="0%" x2="100%" y2="100%">
            <stop offset="0%" stopColor="#4a6cf7" />
            <stop offset="100%" stopColor="#6c9cff" />
          </linearGradient>
        </defs>
        <rect x="8" y="12" width="48" height="40" rx="6" ry="6" fill="url(#logoGrad)" />
        <path d="M32 12 L32 52 M8 28 L56 28" stroke="white" strokeWidth="2.5" strokeLinecap="round" fill="none" opacity="0.9" />
        <circle cx="32" cy="20" r="3" fill="white" opacity="0.95" />
      </svg>
      <div className="logo-text"> MakeSoft Reservations</div>
    </div>
  );
}
