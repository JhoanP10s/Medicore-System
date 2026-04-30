import type { AuthResponse } from '../types/domain';

const AUTH_KEY = 'medicore_system_auth';

export function saveAuth(auth: AuthResponse) {
  localStorage.setItem(AUTH_KEY, JSON.stringify(auth));
}

export function getAuth(): AuthResponse | null {
  const raw = localStorage.getItem(AUTH_KEY);
  if (!raw) return null;

  try {
    return JSON.parse(raw) as AuthResponse;
  } catch {
    localStorage.removeItem(AUTH_KEY);
    return null;
  }
}

export function clearAuth() {
  localStorage.removeItem(AUTH_KEY);
}
