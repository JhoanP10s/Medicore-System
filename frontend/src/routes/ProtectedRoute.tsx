import { Navigate, Outlet } from 'react-router-dom';
import { getAuth } from '../utils/storage';

export function ProtectedRoute() {
  return getAuth()?.token ? <Outlet /> : <Navigate to="/login" replace />;
}
