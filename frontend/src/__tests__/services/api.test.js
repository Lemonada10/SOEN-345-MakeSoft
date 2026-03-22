import { getEvents, login } from '../../services/api';

describe('api client', () => {
  beforeEach(() => {
    global.fetch = jest.fn();
  });

  afterEach(() => {
    jest.restoreAllMocks();
    jest.useRealTimers();
  });

  test('getEvents returns parsed JSON on success', async () => {
    global.fetch.mockResolvedValueOnce({
      ok: true,
      text: async () => '',
      json: async () => [{ id: 1, name: 'Show' }],
    });
    const data = await getEvents({});
    expect(data).toEqual([{ id: 1, name: 'Show' }]);
    expect(global.fetch).toHaveBeenCalled();
  });

  test('getEvents retries after transient failure then succeeds', async () => {
    jest.spyOn(global, 'setTimeout').mockImplementation((fn) => {
      if (typeof fn === 'function') fn();
      return 0;
    });
    global.fetch
      .mockRejectedValueOnce(new TypeError('Failed to fetch'))
      .mockResolvedValueOnce({
        ok: true,
        text: async () => '',
        json: async () => [],
      });
    const data = await getEvents({});
    expect(data).toEqual([]);
    expect(global.fetch).toHaveBeenCalledTimes(2);
  });

  test('login returns user JSON on success', async () => {
    global.fetch.mockResolvedValueOnce({
      ok: true,
      text: async () => '',
      json: async () => ({ id: 42, email: 'u@test.com', role: 'customer' }),
    });
    const user = await login('u@test.com', 'secret');
    expect(user).toEqual({ id: 42, email: 'u@test.com', role: 'customer' });
  });
});
