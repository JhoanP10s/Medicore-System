import { http } from './http';
import type { AuthResponse, LoginRequest, RegisterRequest } from '../types/domain';

export async function register(payload: RegisterRequest) {
  const { data } = await http.post<AuthResponse>('/auth/register', payload);
  return data;
}

export async function login(payload: LoginRequest) {
  const { data } = await http.post<AuthResponse>('/auth/login', payload);
  return data;
}
