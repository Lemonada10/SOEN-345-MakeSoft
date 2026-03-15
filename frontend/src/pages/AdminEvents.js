import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getEvents, createEvent, updateEvent, deleteEvent } from '../services/api';

function formatDate(iso) {
  if (!iso) return '—';
  try {
    return new Date(iso).toLocaleString(undefined, { dateStyle: 'medium', timeStyle: 'short' });
  } catch (_) {
    return iso;
  }
}

const emptyEvent = () => ({
  name: '',
  description: '',
  location: '',
  startDateTime: '',
  status: 'AVAILABLE',
  ticketRemaining: '',
  category: ''
});

export default function AdminEvents({ user }) {
  const navigate = useNavigate();
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState(emptyEvent());
  const [editingId, setEditingId] = useState(null);
  const [editForm, setEditForm] = useState(null);
  const [submitLoading, setSubmitLoading] = useState(false);

  useEffect(() => {
    if (!user) {
      navigate('/signin');
      return;
    }
    if (user.role !== 'admin' && user.role !== 'instructor') {
      navigate('/');
      return;
    }
  }, [user, navigate]);

  function load() {
    setLoading(true);
    setError(null);
    getEvents()
      .then(setEvents)
      .catch((err) => setError(err.message || 'Failed to load events'))
      .finally(() => setLoading(false));
  }

  useEffect(() => {
    if (user && (user.role === 'admin' || user.role === 'instructor')) {
      load();
    }
  }, [user]);

  const isAdmin = user && (user.role === 'admin' || user.role === 'instructor');
  if (!isAdmin) return null;

  function handleCreateSubmit(e) {
    e.preventDefault();
    setSubmitLoading(true);
    setError(null);
    const payload = {
      ...form,
      startDateTime: form.startDateTime ? new Date(form.startDateTime).toISOString() : null
    };
    createEvent(payload)
      .then(() => {
        setForm(emptyEvent());
        setShowForm(false);
        load();
      })
      .catch((err) => setError(err.message || 'Failed to create event'))
      .finally(() => setSubmitLoading(false));
  }

  function handleEdit(ev) {
    setEditingId(ev.id);
    setEditForm({
      name: ev.name || '',
      description: ev.description || '',
      location: ev.location || '',
      startDateTime: ev.startDateTime ? ev.startDateTime.slice(0, 16) : '',
      status: ev.status || 'AVAILABLE',
      ticketRemaining: ev.ticketRemaining != null ? String(ev.ticketRemaining) : '',
      category: ev.category || ''
    });
  }

  function handleEditSubmit(e) {
    e.preventDefault();
    if (!editingId || !editForm) return;
    setSubmitLoading(true);
    setError(null);
    const payload = {
      ...editForm,
      startDateTime: editForm.startDateTime ? new Date(editForm.startDateTime).toISOString() : null
    };
    updateEvent(editingId, payload)
      .then(() => {
        setEditingId(null);
        setEditForm(null);
        load();
      })
      .catch((err) => setError(err.message || 'Failed to update event'))
      .finally(() => setSubmitLoading(false));
  }

  function handleCancelEvent(id) {
    if (!window.confirm('Cancel (delete) this event? This cannot be undone.')) return;
    setError(null);
    deleteEvent(id)
      .then(load)
      .catch((err) => setError(err.message || 'Failed to cancel event'));
  }

  return (
    <div className="form" style={{ maxWidth: 720 }}>
      <div className="form-card">
        <div className="form-title">Admin – Events</div>
        <p className="helper">Add, edit, or cancel events. (Instructor/organizer only.)</p>
        {error && <div className="events-error">{error}</div>}
        <div className="form-actions" style={{ marginBottom: 16 }}>
          <button type="button" className="btn btn-primary" onClick={() => { setShowForm(true); setError(null); }}>Add event</button>
          <Link to="/" className="btn btn-ghost">Back to home</Link>
        </div>

        {showForm && (
          <form onSubmit={handleCreateSubmit} className="admin-event-form">
            <div className="form-title">New event</div>
            <div className="form-row"><label>Name</label><input value={form.name} onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))} required /></div>
            <div className="form-row"><label>Description</label><input value={form.description} onChange={(e) => setForm((f) => ({ ...f, description: e.target.value }))} /></div>
            <div className="form-row"><label>Location</label><input value={form.location} onChange={(e) => setForm((f) => ({ ...f, location: e.target.value }))} /></div>
            <div className="form-row"><label>Start (date-time)</label><input type="datetime-local" value={form.startDateTime} onChange={(e) => setForm((f) => ({ ...f, startDateTime: e.target.value }))} /></div>
            <div className="form-row"><label>Status</label><input value={form.status} onChange={(e) => setForm((f) => ({ ...f, status: e.target.value }))} placeholder="AVAILABLE" /></div>
            <div className="form-row"><label>Tickets remaining</label><input type="number" min="0" value={form.ticketRemaining} onChange={(e) => setForm((f) => ({ ...f, ticketRemaining: e.target.value }))} /></div>
            <div className="form-row"><label>Category</label><input value={form.category} onChange={(e) => setForm((f) => ({ ...f, category: e.target.value }))} /></div>
            <div className="form-actions">
              <button type="submit" className="btn btn-primary" disabled={submitLoading}>{submitLoading ? 'Creating…' : 'Create'}</button>
              <button type="button" className="btn btn-ghost" onClick={() => { setShowForm(false); setForm(emptyEvent()); }}>Cancel</button>
            </div>
          </form>
        )}

        {loading && <div className="helper">Loading events…</div>}
        {!loading && (
          <ul className="events-list">
            {events.length === 0 ? (
              <li className="helper">You have no events yet. Use &quot;Add event&quot; to create one.</li>
            ) : (
              events.map((ev) => (
                <li key={ev.id} className="event-item">
                  {editingId === ev.id && editForm ? (
                    <form onSubmit={handleEditSubmit} className="admin-event-form">
                      <div className="form-row"><label>Name</label><input value={editForm.name} onChange={(e) => setEditForm((f) => ({ ...f, name: e.target.value }))} required /></div>
                      <div className="form-row"><label>Description</label><input value={editForm.description} onChange={(e) => setEditForm((f) => ({ ...f, description: e.target.value }))} /></div>
                      <div className="form-row"><label>Location</label><input value={editForm.location} onChange={(e) => setEditForm((f) => ({ ...f, location: e.target.value }))} /></div>
                      <div className="form-row"><label>Start</label><input type="datetime-local" value={editForm.startDateTime} onChange={(e) => setEditForm((f) => ({ ...f, startDateTime: e.target.value }))} /></div>
                      <div className="form-row"><label>Status</label><input value={editForm.status} onChange={(e) => setEditForm((f) => ({ ...f, status: e.target.value }))} /></div>
                      <div className="form-row"><label>Tickets remaining</label><input type="number" min="0" value={editForm.ticketRemaining} onChange={(e) => setEditForm((f) => ({ ...f, ticketRemaining: e.target.value }))} /></div>
                      <div className="form-row"><label>Category</label><input value={editForm.category} onChange={(e) => setEditForm((f) => ({ ...f, category: e.target.value }))} /></div>
                      <div className="form-actions">
                        <button type="submit" className="btn btn-primary" disabled={submitLoading}>Save</button>
                        <button type="button" className="btn btn-ghost" onClick={() => { setEditingId(null); setEditForm(null); }}>Cancel</button>
                      </div>
                    </form>
                  ) : (
                    <>
                      <div className="event-item-header">
                        <span>{ev.name || 'Unnamed'}</span>
                        <span className="event-meta">{ev.category} · {ev.location}</span>
                      </div>
                      <div className="event-item-desc">{ev.description || '—'}</div>
                      <div className="event-item-footer">
                        <span>{formatDate(ev.startDateTime)}</span>
                        <span>Status: {(ev.ticketRemaining != null && String(ev.ticketRemaining).trim() === '0') ? 'FILLED' : ev.status}</span>
                        <span>Tickets: {ev.ticketRemaining ?? '—'}</span>
                        <button type="button" className="btn btn-ghost btn-sm" onClick={() => handleEdit(ev)}>Edit</button>
                        <button type="button" className="btn btn-ghost btn-sm" onClick={() => handleCancelEvent(ev.id)}>Cancel event</button>
                      </div>
                    </>
                  )}
                </li>
              ))
            )}
          </ul>
        )}
      </div>
    </div>
  );
}
