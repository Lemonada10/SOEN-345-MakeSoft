import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getEvents } from '../services/api';
import { displayEventStatus, canCustomerReserve } from '../utils/eventStatus';

export default function EventsList({ user }) {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filters, setFilters] = useState({ date: '', location: '', category: '', status: '' });

  function loadEvents() {
    setLoading(true);
    setError(null);
    const params = {};
    if (filters.date) params.date = filters.date;
    if (filters.location) params.location = filters.location;
    if (filters.category) params.category = filters.category;
    if (filters.status) params.status = filters.status;
    getEvents(params)
      .then(setEvents)
      .catch((err) => setError(err.message || 'Failed to load events'))
      .finally(() => setLoading(false));
  }

  useEffect(() => {
    setLoading(true);
    setError(null);
    getEvents({})
      .then(setEvents)
      .catch((err) => setError(err.message || 'Failed to load events'))
      .finally(() => setLoading(false));
  }, []);

  function handleFilterSubmit(e) {
    e.preventDefault();
    loadEvents();
  }

  function formatDate(iso) {
    if (!iso) return '—';
    try {
      const d = new Date(iso);
      return d.toLocaleString(undefined, { dateStyle: 'medium', timeStyle: 'short' });
    } catch (_) {
      return iso;
    }
  }

  return (
    <div className="form" style={{ maxWidth: 720 }}>
      <div className="form-card">
        <div className="form-title">Events</div>
        <p className="helper">Search and filter by date, location, or category. Sign in to reserve tickets.</p>

        <form onSubmit={handleFilterSubmit} className="events-filters">
          <div className="events-filters-grid">
            <div className="events-filter-field">
              <label>Date</label>
              <input
                type="text"
                placeholder="e.g. 2025-12-01"
                value={filters.date}
                onChange={(e) => setFilters((f) => ({ ...f, date: e.target.value }))}
              />
            </div>
            <div className="events-filter-field">
              <label>Location</label>
              <input
                type="text"
                placeholder="Location"
                value={filters.location}
                onChange={(e) => setFilters((f) => ({ ...f, location: e.target.value }))}
              />
            </div>
            <div className="events-filter-field">
              <label>Category</label>
              <input
                type="text"
                placeholder="e.g. concert, sports"
                value={filters.category}
                onChange={(e) => setFilters((f) => ({ ...f, category: e.target.value }))}
              />
            </div>
            <div className="events-filter-field">
              <label>Status</label>
              <input
                type="text"
                placeholder="e.g. AVAILABLE"
                value={filters.status}
                onChange={(e) => setFilters((f) => ({ ...f, status: e.target.value }))}
              />
            </div>
          </div>
          <div className="events-filters-actions">
            <button type="submit" className="btn btn-primary btn-sm">Apply filters</button>
          </div>
        </form>

        {error && <div className="events-error">{error}</div>}
        {loading && <div className="helper">Loading events…</div>}
        {!loading && !error && (
          <ul className="events-list">
            {events.length === 0 ? (
              <li className="helper">No events found.</li>
            ) : (
              events.map((ev) => {
                const displayStatus = displayEventStatus(ev);
                const canReserve = canCustomerReserve(ev);
                return (
                <li key={ev.id} className="event-item">
                  <div className="event-item-header">
                    <Link to={`/events/${ev.id}`}>{ev.name || 'Unnamed event'}</Link>
                    <span className="event-meta">Category: {ev.category || '—'} · Location: {ev.location || '—'}</span>
                  </div>
                  <div className="event-item-desc">Description: {ev.description || '—'}</div>
                  <div className="event-item-footer">
                    <span>{formatDate(ev.startDateTime)}</span>
                    <span>Status: {displayStatus}</span>
                    <span>Tickets: {ev.ticketRemaining ?? '—'}</span>
                    {user && user.role !== 'admin' && user.role !== 'instructor' && canReserve && (
                      <Link to={`/events/${ev.id}/reserve`} className="btn btn-primary btn-sm">Reserve</Link>
                    )}
                  </div>
                </li>
                );
              })
            )}
          </ul>
        )}
      </div>
    </div>
  );
}
