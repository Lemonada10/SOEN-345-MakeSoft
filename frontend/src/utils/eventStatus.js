export function isEventPassed(ev) {
  return ev && ev.status === 'PASSED';
}

export function isEventSoldOut(ev) {
  if (!ev) return true;
  const rem = ev.ticketRemaining;
  return rem != null && String(rem).trim() === '0';
}

export function displayEventStatus(ev) {
  if (isEventPassed(ev)) return 'PASSED';
  if (isEventSoldOut(ev)) return 'FILLED';
  if (ev && ev.status === 'DELETED') return 'DELETED';
  return 'AVAILABLE';
}

export function canCustomerReserve(ev) {
  if (!ev || ev.status === 'DELETED') return false;
  return !isEventPassed(ev) && !isEventSoldOut(ev);
}
