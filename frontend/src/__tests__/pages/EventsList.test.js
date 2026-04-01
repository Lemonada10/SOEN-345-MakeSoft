import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import EventsList from '../../pages/EventsList';

jest.mock('../../services/api', () => ({
  getEvents: jest.fn(),
}));

const { getEvents } = require('../../services/api');

beforeEach(() => {
  getEvents.mockReset();
});

test('customer sees Reserve only for non-PASSED non-FILLED events', async () => {
  getEvents.mockResolvedValue([
    {
      id: 1,
      name: 'Past Event',
      category: 'music',
      location: 'A',
      description: 'd',
      startDateTime: '2020-01-01T12:00:00.000Z',
      status: 'PASSED',
      ticketRemaining: '5',
    },
    {
      id: 2,
      name: 'Open Event',
      category: 'music',
      location: 'B',
      description: 'd',
      startDateTime: '2030-06-01T12:00:00.000Z',
      status: 'AVAILABLE',
      ticketRemaining: '3',
    },
  ]);
  render(
    <MemoryRouter>
      <EventsList user={{ id: 10, role: 'customer' }} />
    </MemoryRouter>
  );
  expect(await screen.findByText('Past Event')).toBeInTheDocument();
  expect(screen.getByText('Open Event')).toBeInTheDocument();
  const reserveLinks = screen.getAllByRole('link', { name: /^Reserve$/i });
  expect(reserveLinks).toHaveLength(1);
  expect(reserveLinks[0]).toHaveAttribute('href', '/events/2/reserve');
});

test('customer does not see Reserve for DELETED event', async () => {
  getEvents.mockResolvedValue([
    {
      id: 1,
      name: 'Cancelled',
      category: 'music',
      location: 'A',
      description: 'd',
      startDateTime: '2030-06-01T12:00:00.000Z',
      status: 'DELETED',
      ticketRemaining: '10',
    },
  ]);
  render(
    <MemoryRouter>
      <EventsList user={{ id: 10, role: 'customer' }} />
    </MemoryRouter>
  );
  expect(await screen.findByText('Cancelled')).toBeInTheDocument();
  expect(screen.queryByRole('link', { name: /^Reserve$/i })).not.toBeInTheDocument();
});
