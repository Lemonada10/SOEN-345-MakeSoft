import { render, screen } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import EventDetail from '../../pages/EventDetail';

jest.mock('../../services/api', () => ({
  getEvent: jest.fn(),
}));

const { getEvent } = require('../../services/api');

beforeEach(() => {
  getEvent.mockReset();
});

function renderAt(path, user) {
  return render(
    <MemoryRouter initialEntries={[path]}>
      <Routes>
        <Route path="/events/:id" element={<EventDetail user={user} />} />
      </Routes>
    </MemoryRouter>
  );
}

test('PASSED event hides Reserve tickets link', async () => {
  getEvent.mockResolvedValue({
    id: 1,
    name: 'Done Show',
    description: 'x',
    location: 'L',
    category: 'c',
    startDateTime: '2020-01-01T00:00:00.000Z',
    status: 'PASSED',
    ticketRemaining: '2',
  });
  renderAt('/events/1', { id: 9, role: 'customer' });
  expect(await screen.findByText(/Title: Done Show/i)).toBeInTheDocument();
  expect(screen.queryByRole('link', { name: /reserve tickets/i })).not.toBeInTheDocument();
});

test('AVAILABLE event shows Reserve tickets for customer', async () => {
  getEvent.mockResolvedValue({
    id: 2,
    name: 'Live Show',
    description: 'x',
    location: 'L',
    category: 'c',
    startDateTime: '2030-01-01T00:00:00.000Z',
    status: 'AVAILABLE',
    ticketRemaining: '10',
  });
  renderAt('/events/2', { id: 9, role: 'customer' });
  expect(await screen.findByText(/Title: Live Show/i)).toBeInTheDocument();
  expect(screen.getByRole('link', { name: /reserve tickets/i })).toHaveAttribute('href', '/events/2/reserve');
});
