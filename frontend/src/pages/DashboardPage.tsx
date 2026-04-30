import { CalendarDays, Stethoscope, Users, BadgePlus } from 'lucide-react';
import type { ReactNode } from 'react';
import { useCallback } from 'react';
import { citasApi, especialidadesApi, medicosApi, pacientesApi } from '../api/clinicApi';
import { Alert } from '../components/Alert';
import { LoadingState } from '../components/LoadingState';
import { PageHeader } from '../components/PageHeader';
import { useAsync } from '../hooks/useAsync';
import { formatDateTime } from '../utils/format';

export function DashboardPage() {
  const loader = useCallback(async () => {
    const [pacientes, medicos, citas, especialidades] = await Promise.all([
      pacientesApi.list(),
      medicosApi.list(),
      citasApi.list(),
      especialidadesApi.list()
    ]);
    return { pacientes, medicos, citas, especialidades };
  }, []);

  const { data, loading, error } = useAsync(loader);
  const proximasCitas = [...(data?.citas ?? [])]
    .sort((a, b) => new Date(a.fechaHora).getTime() - new Date(b.fechaHora).getTime())
    .slice(0, 5);

  return (
    <>
      <PageHeader title="Dashboard" subtitle="Resumen operativo del sistema medico." />
      <Alert type="error" message={error} />
      {loading && <LoadingState />}
      {data && (
        <>
          <div className="metrics-grid">
            <Metric icon={<Users />} label="Pacientes" value={data.pacientes.length} />
            <Metric icon={<Stethoscope />} label="Medicos" value={data.medicos.length} />
            <Metric icon={<CalendarDays />} label="Citas" value={data.citas.length} />
            <Metric icon={<BadgePlus />} label="Especialidades" value={data.especialidades.length} />
          </div>
          <section className="panel">
            <div className="section-title">
              <h2>Proximas citas</h2>
            </div>
            <div className="appointment-list">
              {proximasCitas.map((cita) => (
                <article className="appointment-item" key={cita.id}>
                  <div>
                    <strong>{cita.motivo}</strong>
                    <span>{cita.pacienteNombreCompleto} con {cita.medicoNombreCompleto}</span>
                  </div>
                  <time>{formatDateTime(cita.fechaHora)}</time>
                </article>
              ))}
            </div>
          </section>
        </>
      )}
    </>
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
