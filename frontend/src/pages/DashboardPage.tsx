import { AlertTriangle, BadgePlus, CalendarDays, FileText, Stethoscope, Users } from 'lucide-react';
import type { ReactNode } from 'react';
import { useCallback } from 'react';
import { dashboardApi } from '../api/clinicApi';
import { Alert } from '../components/Alert';
import { EmptyState } from '../components/EmptyState';
import { LoadingState } from '../components/LoadingState';
import { PageHeader } from '../components/PageHeader';
import { useAsync } from '../hooks/useAsync';
import type {
  AdminDashboardResponse,
  AlertaDashboardResponse,
  CitaResumenResponse,
  DoctorDashboardResponse,
  EstadoCitaCountResponse,
  HistoriaClinicaResumenResponse,
  UserDashboardResponse
} from '../types/domain';
import { formatDateTime } from '../utils/format';

export function DashboardPage() {
  const loader = useCallback(() => dashboardApi.obtenerResumen(), []);
  const { data, loading, error } = useAsync(loader);

  return (
    <>
      <PageHeader title="Dashboard" subtitle="Resumen clinico y operativo segun tu rol." />
      <Alert type="error" message={error} />
      {loading && <LoadingState />}
      {data?.admin && <AdminDashboard admin={data.admin} />}
      {data?.doctor && <DoctorDashboard doctor={data.doctor} />}
      {data?.user && <UserDashboard user={data.user} />}
    </>
  );
}

function AdminDashboard({ admin }: { admin: AdminDashboardResponse }) {
  return (
    <>
      <div className="metrics-grid">
        <Metric icon={<Users />} label="Pacientes activos" value={admin.totalPacientesActivos} />
        <Metric icon={<Stethoscope />} label="Medicos activos" value={admin.totalMedicosActivos} />
        <Metric icon={<BadgePlus />} label="Especialidades" value={admin.totalEspecialidadesActivas} />
        <Metric icon={<CalendarDays />} label="Citas hoy" value={admin.citasHoy} />
        <Metric icon={<FileText />} label="Historias clinicas" value={admin.historiasClinicasRegistradas} />
      </div>
      <DashboardGrid>
        <EstadoPanel title="Citas por estado" estados={admin.citasPorEstado} />
        <CitasPanel title="Proximas citas" citas={admin.proximasCitas} />
        <AlertasPanel alertas={admin.alertas} />
      </DashboardGrid>
    </>
  );
}

function DoctorDashboard({ doctor }: { doctor: DoctorDashboardResponse }) {
  return (
    <>
      <div className="metrics-grid">
        <Metric icon={<CalendarDays />} label="Mis citas hoy" value={doctor.citasHoy} />
        <Metric icon={<FileText />} label="Pendientes historia" value={doctor.citasPendientesHistoria} />
      </div>
      <DashboardGrid>
        <EstadoPanel title="Mis citas por estado" estados={doctor.citasPorEstado} />
        <CitasPanel title="Mis proximas citas" citas={doctor.proximasCitas} />
        <HistoriasPanel historias={doctor.historiasRecientes} />
        <AlertasPanel alertas={doctor.alertas} />
      </DashboardGrid>
    </>
  );
}

function UserDashboard({ user }: { user: UserDashboardResponse }) {
  return (
    <section className="detail-panel">
      <h2>{user.mensaje}</h2>
      {user.accesosDisponibles.length ? (
        <ul className="dashboard-list">
          {user.accesosDisponibles.map((acceso) => <li key={acceso}>{acceso}</li>)}
        </ul>
      ) : <EmptyState title="Sin accesos disponibles" description="Tu perfil aun no tiene modulos clinicos habilitados." />}
    </section>
  );
}

function DashboardGrid({ children }: { children: ReactNode }) {
  return <div className="dashboard-grid">{children}</div>;
}

function EstadoPanel({ title, estados }: { title: string; estados: EstadoCitaCountResponse[] }) {
  return (
    <section className="panel dashboard-panel">
      <div className="section-title"><h2>{title}</h2></div>
      {estados.length ? (
        <div className="dashboard-list">
          {estados.map((estado) => (
            <div className="dashboard-row" key={estado.estado}>
              <span className="badge badge-info">{estado.estado}</span>
              <strong>{estado.total}</strong>
            </div>
          ))}
        </div>
      ) : <EmptyState title="Sin estados" description="No hay citas para resumir." />}
    </section>
  );
}

function CitasPanel({ title, citas }: { title: string; citas: CitaResumenResponse[] }) {
  return (
    <section className="panel dashboard-panel">
      <div className="section-title"><h2>{title}</h2></div>
      {citas.length ? (
        <div className="appointment-list">
          {citas.map((cita) => (
            <article className="appointment-item" key={cita.id}>
              <div>
                <strong>{cita.motivo}</strong>
                <span>{cita.pacienteNombreCompleto} con {cita.medicoNombreCompleto}</span>
                <span>{cita.estado} - {cita.duracionMinutos} min</span>
              </div>
              <time>{formatDateTime(cita.fechaHora)}</time>
            </article>
          ))}
        </div>
      ) : <EmptyState title="Sin proximas citas" description="No hay citas proximas para mostrar." />}
    </section>
  );
}

function HistoriasPanel({ historias }: { historias: HistoriaClinicaResumenResponse[] }) {
  return (
    <section className="panel dashboard-panel">
      <div className="section-title"><h2>Historias recientes</h2></div>
      {historias.length ? (
        <div className="dashboard-list">
          {historias.map((historia) => (
            <div className="dashboard-row" key={historia.id}>
              <div>
                <strong>{historia.pacienteNombreCompleto}</strong>
                <span>{historia.diagnostico}</span>
              </div>
              <time>{formatDateTime(historia.fechaRegistro)}</time>
            </div>
          ))}
        </div>
      ) : <EmptyState title="Sin historias recientes" description="Aun no hay historias clinicas recientes." />}
    </section>
  );
}

function AlertasPanel({ alertas }: { alertas: AlertaDashboardResponse[] }) {
  return (
    <section className="panel dashboard-panel">
      <div className="section-title"><h2>Alertas</h2></div>
      {alertas.length ? (
        <div className="dashboard-list">
          {alertas.map((alerta) => (
            <div className="dashboard-row" key={`${alerta.tipo}-${alerta.mensaje}`}>
              <span className={`badge ${alerta.severidad === 'WARNING' ? 'badge-muted' : 'badge-info'}`}><AlertTriangle size={13} /> {alerta.tipo}</span>
              <strong>{alerta.mensaje}</strong>
            </div>
          ))}
        </div>
      ) : <EmptyState title="Sin alertas" description="No hay alertas operativas por ahora." />}
    </section>
  );
}

function Metric({ icon, label, value }: { icon: ReactNode; label: string; value: number }) {
  return (
    <article className="metric-card">
      <div className="metric-icon">{icon}</div>
      <span>{label}</span>
      <strong>{value}</strong>
    </article>
  );
}
