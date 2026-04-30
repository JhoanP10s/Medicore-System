import { Navigate, Route, Routes } from 'react-router-dom';
import { AppLayout } from '../layouts/AppLayout';
import { ProtectedRoute } from './ProtectedRoute';
import { LoginPage } from '../pages/LoginPage';
import { DashboardPage } from '../pages/DashboardPage';
import { PacienteListPage } from '../pages/PacienteListPage';
import { PacienteFormPage } from '../pages/PacienteFormPage';
import { MedicoListPage } from '../pages/MedicoListPage';
import { MedicoFormPage } from '../pages/MedicoFormPage';
import { CitaListPage } from '../pages/CitaListPage';
import { CitaFormPage } from '../pages/CitaFormPage';
import { DetailPage } from '../pages/DetailPage';
import { EspecialidadListPage } from '../pages/EspecialidadListPage';

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route element={<ProtectedRoute />}>
        <Route element={<AppLayout />}>
          <Route index element={<DashboardPage />} />
          <Route path="pacientes" element={<PacienteListPage />} />
          <Route path="pacientes/nuevo" element={<PacienteFormPage />} />
          <Route path="pacientes/:documento/editar" element={<PacienteFormPage />} />
          <Route path="medicos" element={<MedicoListPage />} />
          <Route path="medicos/nuevo" element={<MedicoFormPage />} />
          <Route path="medicos/:documento/editar" element={<MedicoFormPage />} />
          <Route path="citas" element={<CitaListPage />} />
          <Route path="citas/nueva" element={<CitaFormPage />} />
          <Route path="citas/:id/editar" element={<CitaFormPage />} />
          <Route path="especialidades" element={<EspecialidadListPage />} />
          <Route path="detalle/:tipo/:id" element={<DetailPage />} />
        </Route>
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
