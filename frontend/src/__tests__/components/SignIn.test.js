import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import SignIn from '../../components/SignIn';

jest.mock('../../services/api', () => ({
  login: jest.fn()
}));

const { login } = require('../../services/api');

test('SignIn shows form with email/phone and password fields', () => {
  render(<SignIn onSuccess={jest.fn()} onSwitchMode={jest.fn()} />);
  expect(screen.getByLabelText(/email or phone/i)).toBeInTheDocument();
  expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
  expect(screen.getByRole('button', { name: /sign in/i })).toBeInTheDocument();
});

test('SignIn calls login and onSuccess when credentials are valid', async () => {
  const onSuccess = jest.fn();
  login.mockResolvedValueOnce({ id: 1, email: 'u@test.com', role: 'customer' });
  render(<SignIn onSuccess={onSuccess} onSwitchMode={jest.fn()} />);
  await userEvent.type(screen.getByLabelText(/email or phone/i), 'u@test.com');
  await userEvent.type(screen.getByLabelText(/password/i), 'pass');
  await userEvent.click(screen.getByRole('button', { name: /sign in/i }));
  expect(login).toHaveBeenCalledWith('u@test.com', 'pass');
  await screen.findByRole('button', { name: /sign in/i });
  expect(onSuccess).toHaveBeenCalledWith(expect.objectContaining({ id: 1, email: 'u@test.com', role: 'customer' }));
});

test('SignIn shows error when login fails', async () => {
  login.mockRejectedValueOnce(new Error('Invalid email or password'));
  render(<SignIn onSuccess={jest.fn()} onSwitchMode={jest.fn()} />);
  await userEvent.type(screen.getByLabelText(/email or phone/i), 'bad@test.com');
  await userEvent.type(screen.getByLabelText(/password/i), 'wrong');
  await userEvent.click(screen.getByRole('button', { name: /sign in/i }));
  expect(await screen.findByText(/Invalid email or password/i)).toBeInTheDocument();
});
