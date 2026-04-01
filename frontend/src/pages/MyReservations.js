import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getReservations, cancelReservation } from '../services/api';

function formatDate(iso) {
  if (!iso) return '—';
  try {
    return new Date(iso).toLocaleString(undefined, { dateStyle: 'medium', timeStyle: 'short' });
  } catch (_) {
    return iso;
  }
}

function isEventCancelled(reservation) {
  return reservation.event && reservation.event.status === 'DELETED';
}

export default function MyReservations({ user }) {
  const navigate = useNavigate();
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [cancelMessage, setCancelMessage] = useState(null);

  function load() {
    setLoading(true);
    setError(null);
    getReservations()
      .then(setReservations)
      .catch((err) => setError(err.message || 'Failed to load reservations'))
      .finally(() => setLoading(false));
  }

  useEffect(() => {
    if (!user) {
      navigate('/signin');
      return;
    }
    if (user.role === 'admin' || user.role === 'instructor') {
      navigate('/');
      return;
    }
    load();
  }, [user, navigate]);

  function handleCancel(reservationId) {
    if (!window.confirm('Cancel this reservation?')) return;
    setCancelMessage(null);
    cancelReservation(reservationId)
      .then(() => {
        load();
        setCancelMessage('Reservation has been cancelled.');
      })
      .catch((err) => setError(err.message || 'Failed to cancel'));
  }

  if (!user) return null;
  if (user.role === 'admin' || user.role === 'instructor') return null;
  const myReservations = reservations.filter((r) => r.user && String(r.user.id) === String(user.id));
  const hasCancelledEventReservation = myReservations.some(isEventCancelled);

  return (
    <div className="form" style={{ maxWidth: 720 }}>
      <div className="form-card">
        <div className="form-title">My reservations</div>
        <p className="helper">View and cancel your reservations.</p>
        {hasCancelledEventReservation && (
          <div className="events-error" role="alert" style={{ marginBottom: 12 }}>
            One or more of your reservations are for events that were cancelled by the organizer. See details on each reservation below.
          </div>
        )}
        {error && <div className="events-error">{error}</div>}
        {cancelMessage && <div className="helper" style={{ color: 'green' }}>{cancelMessage}</div>}
        {loading && <div className="helper">Loading…</div>}
        {!loading && (
          <ul className="events-list">
            {myReservations.length === 0 ? (
              <li className="helper">No reservations yet. <Link to="/events">Browse events</Link></li>
            ) : (
              myReservations.map((r) => (
                <li key={r.reservation_id} className="event-item">
                  <div className="event-item-header">
                    <span>{r.event ? r.event.name : 'Event'} · Qty: {r.quantity}</span>
                  </div>
                  {isEventCancelled(r) && (
                    <div
                      className="events-error"
                      role="status"
                      style={{ margin: '8px 0', padding: '10px 12px', borderRadius: 8 }}
                    >
                      This event was cancelled by the organizer. This reservation is no longer valid for attending the event.
                    </div>
                  )}
                  <div className="event-item-footer">
                    <span>Reserved: {formatDate(r.reservationDateTime)}</span>
                    {isEventCancelled(r) && <span>Event: DELETED</span>}
                    <span>Reservation status: {r.status || '—'}</span>
                    <button type="button" className="btn btn-ghost btn-sm" onClick={() => handleCancel(r.reservation_id)}>Cancel reservation</button>
                  </div>
                </li>
              ))
            )}
          </ul>
        )}
        <div className="form-actions" style={{ marginTop: 16 }}>
          <Link to="/events" className="btn btn-primary">Browse events</Link>
        </div>
      </div>
    </div>
  );
}
