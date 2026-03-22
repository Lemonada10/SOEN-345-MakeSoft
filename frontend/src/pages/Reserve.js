import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { getEvent, createReservation } from '../services/api';

export default function Reserve({ user }) {
  const { id } = useParams();
  const navigate = useNavigate();
  const [event, setEvent] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [done, setDone] = useState(false);

  useEffect(() => {
    if (!id) return;
    getEvent(id)
      .then(setEvent)
      .catch((err) => setError(err.message || 'Failed to load event'));
  }, [id]);

  useEffect(() => {
    if (user && (user.role === 'admin' || user.role === 'instructor')) {
      navigate(id ? `/events/${id}` : '/events');
    }
  }, [user, id, navigate]);

  function handleSubmit(e) {
    e.preventDefault();
    if (!user || !user.id || !event) return;
    setLoading(true);
    setError(null);
    createReservation({ userId: user.id, eventId: event.id, quantity })
      .then(() => setDone(true))
      .catch((err) => setError(err.message || 'Reservation failed'))
      .finally(() => setLoading(false));
  }

  if (!user) {
    return (
      <div className="form"><div className="form-card">
        <div className="events-error">Please sign in to reserve tickets.</div>
        <Link to="/signin">Sign In</Link>
      </div></div>
    );
  }
  if (user.role === 'admin' || user.role === 'instructor') return null;
  if (error && !event) return <div className="form"><div className="form-card"><div className="events-error">{error}</div><Link to="/events">Back to events</Link></div></div>;
  if (done) {
    return (
      <div className="form"><div className="form-card">
        <div className="form-title">Reservation confirmed</div>
        <p className="helper">Your reservation has been created. You can view it under My reservations.</p>
        {/* <p className="helper">A confirmation has been sent to your email/phone.</p> */}
        <Link to="/reservations" className="btn btn-primary">My reservations</Link>
        <Link to="/events" className="btn btn-ghost" style={{ marginLeft: 8 }}>Back to events</Link>
      </div></div>
    );
  }
  if (!event) return <div className="form"><div className="form-card"><div className="helper">Loading…</div></div></div>;

  const isPassed = event.status === 'PASSED';
  if (isPassed) {
    return (
      <div className="form" style={{ maxWidth: 560 }}>
        <div className="form-card">
          <div className="form-title">Reserve: {event.name || 'Unnamed event'}</div>
          <div className="events-error">This event has already passed and cannot be reserved.</div>
          <div className="helper"><Link to={`/events/${id}`}>Back to event</Link></div>
        </div>
      </div>
    );
  }

  const maxQty = event.ticketRemaining != null && !isNaN(Number(event.ticketRemaining)) ? Math.max(0, Number(event.ticketRemaining)) : 99;
  const noTicketsLeft = maxQty === 0;

  if (noTicketsLeft) {
    return (
      <div className="form" style={{ maxWidth: 560 }}>
        <div className="form-card">
          <div className="form-title">Reserve: {event.name || 'Unnamed event'}</div>
          <p className="helper">Tickets available: {event.ticketRemaining ?? '—'}</p>
          <div className="events-error">There is not enough tickets for the quantity u chose.</div>
          <div className="helper"><Link to={`/events/${id}`}>Back to event</Link></div>
        </div>
      </div>
    );
  }

  return (
    <div className="form" style={{ maxWidth: 560 }}>
      <div className="form-card">
        <div className="form-title">Reserve: {event.name || 'Unnamed event'}</div>
        <p className="helper">Tickets available: {event.ticketRemaining ?? '—'}</p>
        <form onSubmit={handleSubmit}>
          <div className="form-row">
            <label>Quantity</label>
            <input
              type="number"
              min={1}
              max={maxQty}
              value={quantity}
              onChange={(e) => setQuantity(parseInt(e.target.value, 10) || 1)}
            />
          </div>
          {error && <div className="events-error">{error}</div>}
          <div className="form-actions">
            <button type="submit" className="btn btn-primary" disabled={loading}>{loading ? 'Reserving…' : 'Confirm reservation'}</button>
          </div>
        </form>
        <div className="helper"><Link to={`/events/${id}`}>Back to event</Link></div>
      </div>
    </div>
  );
}
