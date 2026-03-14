import { render, screen } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import Reserve from '../../pages/Reserve';

jest.mock('../../services/api', () => ({
  getEvent: jest.fn(),
  createReservation: jest.fn()
}));

const { getEvent } = require('../../services/api');

beforeEach(() => {
  getEvent.mockReset();
});

test('Reserve shows sign-in prompt when user is not logged in', () => {
  getEvent.mockResolvedValue(null);
  render(
    <MemoryRouter initialEntries={['/events/1/reserve']}>
      <Routes>
        <Route path="/events/:id/reserve" element={<Reserve user={null} />} />
      </Routes>
    </MemoryRouter>
  );
  expect(screen.getByText(/Please sign in to reserve tickets/i)).toBeInTheDocument();
});

test('Reserve shows zero-tickets message when event has 0 tickets', async () => {
  getEvent.mockResolvedValueOnce({
    id: 1,
    name: 'Volleyball',
    ticketRemaining: '0'
  });
  render(
    <MemoryRouter initialEntries={['/events/1/reserve']}>
      <Routes>
        <Route path="/events/:id/reserve" element={<Reserve user={{ id: 1, role: 'customer' }} />} />
      </Routes>
    </MemoryRouter>
  );
  expect(await screen.findByText(/Reserve: Volleyball/i)).toBeInTheDocument();
  expect(screen.getByText(/Tickets available: 0/)).toBeInTheDocument();
  expect(screen.getByText(/There is not enough tickets for the quantity u chose/i)).toBeInTheDocument();
});
