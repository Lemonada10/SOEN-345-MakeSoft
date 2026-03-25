import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import SignUp from '../../components/SignUp';

jest.mock('../../services/api', () => ({
  registerUser: jest.fn(),
}));

const { registerUser } = require('../../services/api');

test('SignUp shows email, phone, password and role options', () => {
  render(<SignUp onSuccess={jest.fn()} onSwitchMode={jest.fn()} />);
  expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
  expect(screen.getByLabelText(/phone number/i)).toBeInTheDocument();
  expect(screen.getByLabelText(/^password$/i)).toBeInTheDocument();
  expect(screen.getByRole('button', { name: /sign up/i })).toBeInTheDocument();
});

test('SignUp calls registerUser and onSuccess with created user', async () => {
  const onSuccess = jest.fn();
  registerUser.mockResolvedValueOnce({ id: 9, email: 'new@example.com', role: 'customer' });
  render(<SignUp onSuccess={onSuccess} onSwitchMode={jest.fn()} />);
  await userEvent.type(screen.getByLabelText(/email/i), 'new@example.com');
  await userEvent.type(screen.getByLabelText(/phone number/i), '555-0000');
  await userEvent.type(screen.getByLabelText(/^password$/i), 'pw123');
  await userEvent.click(screen.getByRole('button', { name: /sign up/i }));
  await waitFor(() => {
    expect(registerUser).toHaveBeenCalledWith(
      expect.objectContaining({
        email: 'new@example.com',
        phoneNumber: '555-0000',
        password: 'pw123',
        role: 'customer',
        name: 'new',
      })
    );
    expect(onSuccess).toHaveBeenCalledWith(
      expect.objectContaining({ id: 9, email: 'new@example.com', role: 'customer' })
    );
  });
});

test('SignUp shows friendly message when account already exists', async () => {
  registerUser.mockRejectedValueOnce(new Error('user with that email already exists'));
  render(<SignUp onSuccess={jest.fn()} onSwitchMode={jest.fn()} />);
  await userEvent.type(screen.getByLabelText(/email/i), 'dup@example.com');
  await userEvent.type(screen.getByLabelText(/^password$/i), 'pw123');
  await userEvent.click(screen.getByRole('button', { name: /sign up/i }));
  expect(await screen.findByText(/User account already exists/i)).toBeInTheDocument();
});
