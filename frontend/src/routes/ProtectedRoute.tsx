import { Navigate, Outlet } from 'react-router-dom';
import { getAuth } from '../utils/storage';
import type { Role } from '../types/domain';

interface ProtectedRouteProps {
  allowedRoles?: Role[];
}

export function ProtectedRoute({ allowedRoles }: ProtectedRouteProps) {
  const auth = getAuth();

  if (!auth?.token) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles?.length && !allowedRoles.includes(auth.rol)) {
    return <Navigate to="/acceso-denegado" replace />;
  }

  return <Outlet />;
}
