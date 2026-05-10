import { useCallback, useEffect, useMemo, useState } from 'react';
import { login as loginRequest } from '../api/authApi';
import { clearAuth, getAuth, saveAuth } from '../utils/storage';
import type { AuthResponse, LoginRequest } from '../types/domain';

export function useAuth() {
  const [auth, setAuth] = useState<AuthResponse | null>(() => getAuth());

  useEffect(() => {
    const onStorage = () => setAuth(getAuth());
    window.addEventListener('storage', onStorage);
    window.addEventListener('auth:changed', onStorage);
    return () => {
      window.removeEventListener('storage', onStorage);
      window.removeEventListener('auth:changed', onStorage);
    };
  }, []);

  const login = useCallback(async (payload: LoginRequest) => {
    const response = await loginRequest(payload);
    saveAuth(response);
    setAuth(response);
    window.dispatchEvent(new Event('auth:changed'));
    return response;
  }, []);

  const logout = useCallback(() => {
    clearAuth();
    setAuth(null);
    window.dispatchEvent(new Event('auth:changed'));
  }, []);

  const helpers = useMemo(() => ({
    getToken: () => auth?.token ?? null,
    getRole: () => auth?.rol ?? null,
    getMedicoId: () => auth?.medicoId ?? null,
    isAdmin: () => auth?.rol === 'ADMIN',
    isDoctor: () => auth?.rol === 'DOCTOR',
    isUser: () => auth?.rol === 'USER'
  }), [auth]);

  return { auth, isAuthenticated: Boolean(auth?.token), login, logout, ...helpers };
}
