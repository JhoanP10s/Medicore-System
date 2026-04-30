import { http } from './http';
import type { AuthResponse, LoginRequest } from '../types/domain';

export async function login(payload: LoginRequest) {
  const { data } = await http.post<AuthResponse>('/auth/login', payload);
  return data;
}
