import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getEvent } from '../services/api';

function formatDate(iso) {
  if (!iso) return '—';
  try {
    return new Date(iso).toLocaleString(undefined, { dateStyle: 'medium', timeStyle: 'short' });
  } catch (_) {
    return iso;
  }
}

export default function EventDetail({ user }) {
  const { id } = useParams();
  const [event, setEvent] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    getEvent(id)
      .then(setEvent)
      .catch((err) => setError(err.message || 'Failed to load event'))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <div className="form"><div className="form-card"><div className="helper">Loading…</div></div></div>;
  if (error) return <div className="form"><div className="form-card"><div className="events-error">{error}</div><Link to="/events">Back to events</Link></div></div>;
  if (!event) return null;

  const isFilled = event.ticketRemaining != null && String(event.ticketRemaining).trim() === '0';
  const displayStatus = isFilled ? 'FILLED' : (event.status || '—');

  return (
    <div className="form" style={{ maxWidth: 560 }}>
      <div className="form-card">
        <div className="form-title">Title: {event.name || 'Unnamed event'}</div>
        <p className="helper">Description: {event.description || '—'}</p>
        <div className="event-detail-meta">
          <span>Location: {event.location || '—'}</span>
          <span>Category: {event.category || '—'}</span>
          <span>Date & time: {formatDate(event.startDateTime)}</span>
          <span>Status: {displayStatus}</span>
          <span>Tickets remaining: {event.ticketRemaining ?? '—'}</span>
        </div>
        <div className="form-actions" style={{ marginTop: 16 }}>
          <Link to="/events" className="btn btn-ghost">Back to events</Link>
          {user && user.role !== 'admin' && user.role !== 'instructor' && (
            <Link to={`/events/${id}/reserve`} className="btn btn-primary">Reserve tickets</Link>
          )}
        </div>
      </div>
    </div>
  );
}
