import { render, screen } from '@testing-library/react';
import App from '../App';

test('renders welcome and MakeSoft branding', () => {
  render(<App />);
  expect(screen.getByText(/Welcome to MakeSoft Reservations/i)).toBeInTheDocument();
  expect(screen.getByText(/Sign In/i)).toBeInTheDocument();
  expect(screen.getByText(/Sign Up/i)).toBeInTheDocument();
});
