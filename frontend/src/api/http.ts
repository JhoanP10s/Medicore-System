import axios, { AxiosError } from 'axios';
import { clearAuth, getAuth } from '../utils/storage';
import type { ErrorResponse } from '../types/domain';

export const http = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? '',
  headers: {
    'Content-Type': 'application/json'
  }
});

http.interceptors.request.use((config) => {
  const auth = getAuth();
  const url = config.url ?? '';

  if (auth?.token && !url.startsWith('/auth/')) {
    config.headers.Authorization = `${auth.tokenType ?? 'Bearer'} ${auth.token}`;
  }

  return config;
});

export function getErrorMessage(error: unknown) {
  if (error instanceof AxiosError) {
    const data = error.response?.data as ErrorResponse | undefined;

    if (error.response?.status === 401) {
      clearAuth();
      window.dispatchEvent(new Event('auth:changed'));
      if (!window.location.pathname.startsWith('/login')) {
        window.location.assign('/login');
      }
      return data?.message ?? 'Sesion expirada o credenciales incorrectas.';
    }

    if (error.response?.status === 403) {
      return data?.message ?? 'No tienes permisos para realizar esta accion.';
    }

    return data?.message ?? error.message;
  }

  if (error instanceof Error) {
    return error.message;
  }

  return 'Ocurrio un error inesperado.';
}
