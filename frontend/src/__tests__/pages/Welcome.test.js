import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import Welcome from '../../pages/Welcome';

function renderWelcome(props = {}) {
  const defaultProps = {
    onChoose: jest.fn(),
    user: null,
    onLogout: jest.fn()
  };
  return render(
    <MemoryRouter>
      <Welcome {...defaultProps} {...props} />
    </MemoryRouter>
  );
}

test('Welcome shows title and Sign In / Sign Up when not logged in', () => {
  renderWelcome();
  expect(screen.getByText(/Welcome to MakeSoft Reservations/i)).toBeInTheDocument();
  expect(screen.getByRole('button', { name: /sign in/i })).toBeInTheDocument();
  expect(screen.getByRole('button', { name: /sign up/i })).toBeInTheDocument();
});

test('Welcome calls onChoose with signin when Sign In is clicked', async () => {
  const onChoose = jest.fn();
  renderWelcome({ onChoose });
  await userEvent.click(screen.getByRole('button', { name: /sign in/i }));
  expect(onChoose).toHaveBeenCalledWith('signin');
});

test('Welcome calls onChoose with signup when Sign Up is clicked', async () => {
  const onChoose = jest.fn();
  renderWelcome({ onChoose });
  await userEvent.click(screen.getByRole('button', { name: /sign up/i }));
  expect(onChoose).toHaveBeenCalledWith('signup');
});

test('Welcome shows Events and Sign out when user is logged in as customer', () => {
  renderWelcome({ user: { id: 1, role: 'customer' } });
  expect(screen.getByRole('button', { name: /events/i })).toBeInTheDocument();
  expect(screen.getByRole('button', { name: /sign out/i })).toBeInTheDocument();
});
