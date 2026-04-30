import axios, { AxiosError } from 'axios';
import { getAuth } from '../utils/storage';
import type { ErrorResponse } from '../types/domain';

export const http = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? '',
  headers: {
    'Content-Type': 'application/json'
  }
});

http.interceptors.request.use((config) => {
  const auth = getAuth();
  if (auth?.token) {
    config.headers.Authorization = `${auth.tokenType ?? 'Bearer'} ${auth.token}`;
  }
  return config;
});

export function getErrorMessage(error: unknown) {
  if (error instanceof AxiosError) {
    const data = error.response?.data as ErrorResponse | undefined;
    return data?.message ?? error.message;
  }

  if (error instanceof Error) {
    return error.message;
  }

  return 'Ocurrio un error inesperado.';
}
