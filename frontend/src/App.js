import './App.css';
import React, { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, useNavigate, useLocation } from 'react-router-dom';
import faviconIcon from './assets/airplane-ticket.png';
import { wakeBackend } from './services/api';
import Background from './components/Background';
import Welcome from './pages/Welcome';
import SignIn from './components/SignIn';
import SignUp from './components/SignUp';
import Logo from './components/Logo';
import EventsList from './pages/EventsList';
import EventDetail from './pages/EventDetail';
import Reserve from './pages/Reserve';
import MyReservations from './pages/MyReservations';
import AdminEvents from './pages/AdminEvents';

function App() {
  const [user, setUser] = useState(() => {
    try {
      const s = sessionStorage.getItem('makesoft_user');
      return s ? JSON.parse(s) : null;
    } catch (_) {
      return null;
    }
  });
  const navigate = useNavigate();
  const location = useLocation();
  const showBack = location.pathname !== '/';

  useEffect(() => {
    wakeBackend();
    const link = document.querySelector('link[rel="icon"]');
    if (link) {
      link.href = faviconIcon;
      link.type = 'image/png';
    }
  }, []);

  function handleAuthSuccess(authUser) {
    setUser(authUser);
    try {
      sessionStorage.setItem('makesoft_user', JSON.stringify(authUser));
    } catch (_) {}
    if (authUser.role === 'admin' || authUser.role === 'instructor') {
      navigate('/admin/events');
    } else {
      navigate('/events');
    }
  }

  function handleLogout() {
    setUser(null);
    try {
      sessionStorage.removeItem('makesoft_user');
    } catch (_) {}
    navigate('/');
  }

  return (
    <div className="App">
      {showBack && (
        <button type="button" className="app-back" onClick={() => navigate('/')} aria-label="Back to welcome">
          ← Back
        </button>
      )}
      <Logo onClick={() => navigate('/')} />
      <Background />
      <Routes>
        <Route path="/" element={<Welcome onChoose={(mode) => { if (mode === 'signin') navigate('/signin'); else if (mode === 'signup') navigate('/signup'); else navigate('/'); }} user={user} onLogout={handleLogout} />} />
        <Route path="/signin" element={<SignIn onSuccess={handleAuthSuccess} onSwitchMode={(m) => navigate(m === 'signup' ? '/signup' : '/')} />} />
        <Route path="/signup" element={<SignUp onSuccess={handleAuthSuccess} onSwitchMode={(m) => navigate(m === 'signin' ? '/signin' : '/')} />} />
        <Route path="/events" element={<EventsList user={user} />} />
        <Route path="/events/:id" element={<EventDetail user={user} />} />
        <Route path="/events/:id/reserve" element={<Reserve user={user} />} />
        <Route path="/reservations" element={<MyReservations user={user} />} />
        <Route path="/admin/events" element={<AdminEvents user={user} />} />
      </Routes>
    </div>
  );
}

function AppWithRouter() {
  return (
    <BrowserRouter>
      <App />
    </BrowserRouter>
  );
}

export default AppWithRouter;
