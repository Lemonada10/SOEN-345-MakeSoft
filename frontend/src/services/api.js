export const API_BASE = process.env.REACT_APP_API_BASE || 'http://localhost:8080/api';

async function fetchWithRetry(url, options = {}, retries = 2) {
  for (let attempt = 0; attempt <= retries; attempt++) {
    try {
      const res = await fetch(url, options);
      return res;
    } catch (err) {
      if (attempt < retries) {
        await new Promise(r => setTimeout(r, 3000));
      } else {
        throw err;
      }
    }
  }
}

/** Sends a fire-and-forget ping to wake the backend on Render free tier. */
export function wakeBackend() {
  fetchWithRetry(`${API_BASE}/events`, {}, 0).catch(() => {});
}

/** @param {{ date?: string, location?: string, category?: string, status?: string }} params */
export async function getEvents(params = {}) {
  const sp = new URLSearchParams();
  if (params.date) {
    let d = params.date.trim();
    if (/^\d{4}-\d{2}-\d{2}$/.test(d)) d += 'T00:00:00';
    sp.set('date', d);
  }
  if (params.location) sp.set('location', params.location);
  if (params.category) sp.set('category', params.category);
  if (params.status) sp.set('status', params.status);
  const qs = sp.toString();
  const url = `${API_BASE}/events${qs ? '?' + qs : ''}`;
  const res = await fetchWithRetry(url);
  if (!res.ok) throw new Error(await res.text());
  return res.json();
}

export async function getEvent(id) {
  const res = await fetchWithRetry(`${API_BASE}/events/${id}`);
  if (!res.ok) throw new Error(await res.text());
  return res.json();
}

export async function createEvent(event, token) {
  const res = await fetchWithRetry(`${API_BASE}/events`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', ...(token ? { Authorization: `Bearer ${token}` } : {}) },
    body: JSON.stringify(event)
  });
  if (!res.ok) throw new Error(await res.text());
  return res.json();
}

export async function updateEvent(id, event, token) {
  const res = await fetchWithRetry(`${API_BASE}/events/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json', ...(token ? { Authorization: `Bearer ${token}` } : {}) },
    body: JSON.stringify(event)
  });
  if (!res.ok) throw new Error(await res.text());
  return res.json();
}

export async function deleteEvent(id, token) {
  const res = await fetchWithRetry(`${API_BASE}/events/${id}`, {
    method: 'DELETE',
    headers: token ? { Authorization: `Bearer ${token}` } : {}
  });
  if (!res.ok) throw new Error(await res.text());
}

export async function createReservation({ userId, eventId, quantity }, token) {
  const res = await fetchWithRetry(`${API_BASE}/reservations`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', ...(token ? { Authorization: `Bearer ${token}` } : {}) },
    body: JSON.stringify({ userId, eventId, quantity })
  });
  if (!res.ok) throw new Error(await res.text());
  return res.text();
}

export async function getReservations() {
  const res = await fetchWithRetry(`${API_BASE}/reservations`);
  if (!res.ok) throw new Error(await res.text());
  return res.json();
}

export async function cancelReservation(id, token) {
  const res = await fetchWithRetry(`${API_BASE}/reservations/${id}`, {
    method: 'DELETE',
    headers: token ? { Authorization: `Bearer ${token}` } : {}
  });
  if (!res.ok) throw new Error(await res.text());
}

export async function login(identifier, password) {
  const res = await fetchWithRetry(`${API_BASE}/users/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ identifier, password })
  });
  if (!res.ok) {
    const body = await res.text();
    throw new Error(body || 'Failed to sign in');
  }
  return res.json();
}

export async function getUserByEmail(email) {
  const res = await fetchWithRetry(`${API_BASE}/users/by-email?email=${encodeURIComponent(email)}`);
  if (!res.ok) throw new Error(await res.text());
  return res.json();
}

export async function registerUser(payload) {
  const res = await fetchWithRetry(`${API_BASE}/users/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });
  if (!res.ok) throw new Error(await res.text());
  return res.json();
}