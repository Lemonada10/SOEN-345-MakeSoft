import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import AdminEvents from '../../pages/AdminEvents';

jest.mock('../../services/api', () => ({
  getEvents: jest.fn(),
  createEvent: jest.fn(),
  updateEvent: jest.fn(),
  deleteEvent: jest.fn(),
}));

const { getEvents } = require('../../services/api');

beforeEach(() => {
  getEvents.mockReset();
});

test('admin page shows title and Add event when events load', async () => {
  getEvents.mockResolvedValue([]);
  render(
    <MemoryRouter>
      <AdminEvents user={{ id: 1, role: 'admin' }} />
    </MemoryRouter>
  );
  expect(await screen.findByText(/Admin – Events/i)).toBeInTheDocument();
  expect(screen.getByRole('button', { name: /add event/i })).toBeInTheDocument();
});
