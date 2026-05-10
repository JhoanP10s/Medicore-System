import { Navigate, Route, Routes } from 'react-router-dom';
import { AppLayout } from '../layouts/AppLayout';
import { ProtectedRoute } from './ProtectedRoute';
import { LoginPage } from '../pages/LoginPage';
import { RegisterPage } from '../pages/RegisterPage';
import { DashboardPage } from '../pages/DashboardPage';
import { PacienteListPage } from '../pages/PacienteListPage';
import { PacienteFormPage } from '../pages/PacienteFormPage';
import { MedicoListPage } from '../pages/MedicoListPage';
import { MedicoFormPage } from '../pages/MedicoFormPage';
import { CitaListPage } from '../pages/CitaListPage';
import { CitaFormPage } from '../pages/CitaFormPage';
import { DetailPage } from '../pages/DetailPage';
import { EspecialidadListPage } from '../pages/EspecialidadListPage';
import { AgendaPage } from '../pages/AgendaPage';
import { AccessDeniedPage } from '../pages/AccessDeniedPage';
import { HistoriaClinicaListPage } from '../pages/HistoriaClinicaListPage';
import { HistoriaClinicaDetailPage } from '../pages/HistoriaClinicaDetailPage';
import { HistoriaClinicaFormPage } from '../pages/HistoriaClinicaFormPage';
import { DisponibilidadMedicaListPage } from '../pages/DisponibilidadMedicaListPage';
import { DisponibilidadMedicaFormPage } from '../pages/DisponibilidadMedicaFormPage';
import { BloqueoAgendaListPage } from '../pages/BloqueoAgendaListPage';
import { BloqueoAgendaFormPage } from '../pages/BloqueoAgendaFormPage';

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route element={<ProtectedRoute />}>
        <Route element={<AppLayout />}>
          <Route index element={<DashboardPage />} />
          <Route path="acceso-denegado" element={<AccessDeniedPage />} />

          <Route element={<ProtectedRoute allowedRoles={['ADMIN', 'USER']} />}>
            <Route path="pacientes" element={<PacienteListPage />} />
            <Route path="pacientes/nuevo" element={<PacienteFormPage />} />
            <Route path="pacientes/:documento/editar" element={<PacienteFormPage />} />
          </Route>

          <Route element={<ProtectedRoute allowedRoles={['ADMIN']} />}>
            <Route path="medicos" element={<MedicoListPage />} />
            <Route path="medicos/nuevo" element={<MedicoFormPage />} />
            <Route path="medicos/:documento/editar" element={<MedicoFormPage />} />
            <Route path="especialidades" element={<EspecialidadListPage />} />
            <Route path="disponibilidad-medica/nueva" element={<DisponibilidadMedicaFormPage />} />
            <Route path="disponibilidad-medica/:id/editar" element={<DisponibilidadMedicaFormPage />} />
            <Route path="bloqueo-agenda/nuevo" element={<BloqueoAgendaFormPage />} />
            <Route path="bloqueo-agenda/:id/editar" element={<BloqueoAgendaFormPage />} />
          </Route>

          <Route element={<ProtectedRoute allowedRoles={['ADMIN', 'DOCTOR']} />}>
            <Route path="citas" element={<CitaListPage />} />
            <Route path="citas/nueva" element={<CitaFormPage />} />
            <Route path="citas/:id/editar" element={<CitaFormPage />} />
            <Route path="citas/:citaId/historia-clinica/nueva" element={<HistoriaClinicaFormPage />} />
            <Route path="agenda" element={<AgendaPage />} />
            <Route path="disponibilidad-medica" element={<DisponibilidadMedicaListPage />} />
            <Route path="bloqueo-agenda" element={<BloqueoAgendaListPage />} />
            <Route path="historias-clinicas" element={<HistoriaClinicaListPage />} />
            <Route path="historias-clinicas/nueva" element={<HistoriaClinicaFormPage />} />
            <Route path="historias-clinicas/:id" element={<HistoriaClinicaDetailPage />} />
            <Route path="historias-clinicas/:id/editar" element={<HistoriaClinicaFormPage />} />
          </Route>

          <Route path="detalle/:tipo/:id" element={<DetailPage />} />
        </Route>
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
