import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import MyReservations from '../../pages/MyReservations';

jest.mock('../../services/api', () => ({
  getReservations: jest.fn(),
  cancelReservation: jest.fn(),
}));

const { getReservations } = require('../../services/api');

beforeEach(() => {
  getReservations.mockReset();
});

test('shows user reservations when API returns data', async () => {
  getReservations.mockResolvedValue([
    {
      reservation_id: 100,
      quantity: 2,
      status: 'CONFIRMED',
      reservationDateTime: '2026-01-15T18:00:00.000Z',
      user: { id: 5 },
      event: { name: 'Jazz Night' },
    },
  ]);
  render(
    <MemoryRouter>
      <MyReservations user={{ id: 5, role: 'customer' }} />
    </MemoryRouter>
  );
  expect(await screen.findByText(/Jazz Night/i)).toBeInTheDocument();
  expect(screen.getByText(/Qty: 2/i)).toBeInTheDocument();
});

test('shows empty helper when user has no matching reservations', async () => {
  getReservations.mockResolvedValue([
    {
      reservation_id: 1,
      quantity: 1,
      status: 'CONFIRMED',
      reservationDateTime: '2026-01-01T12:00:00.000Z',
      user: { id: 99 },
      event: { name: 'Other user booking' },
    },
  ]);
  render(
    <MemoryRouter>
      <MyReservations user={{ id: 5, role: 'customer' }} />
    </MemoryRouter>
  );
  expect(await screen.findByText(/No reservations yet/i)).toBeInTheDocument();
});
