import { useCallback, useEffect, useState } from 'react';
import { login as loginRequest } from '../api/authApi';
import { clearAuth, getAuth, saveAuth } from '../utils/storage';
import type { AuthResponse, LoginRequest } from '../types/domain';

export function useAuth() {
  const [auth, setAuth] = useState<AuthResponse | null>(() => getAuth());

  useEffect(() => {
    const onStorage = () => setAuth(getAuth());
    window.addEventListener('storage', onStorage);
    return () => window.removeEventListener('storage', onStorage);
  }, []);

  const login = useCallback(async (payload: LoginRequest) => {
    const response = await loginRequest(payload);
    saveAuth(response);
    setAuth(response);
    return response;
  }, []);

  const logout = useCallback(() => {
    clearAuth();
    setAuth(null);
  }, []);

  return { auth, isAuthenticated: Boolean(auth?.token), login, logout };
}
