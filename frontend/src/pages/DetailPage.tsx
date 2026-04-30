import { ArrowLeft } from 'lucide-react';
import { useCallback } from 'react';
import { Link, useParams } from 'react-router-dom';
import { citasApi, medicosApi, pacientesApi } from '../api/clinicApi';
import { Alert } from '../components/Alert';
import { LoadingState } from '../components/LoadingState';
import { PageHeader } from '../components/PageHeader';
import { StatusBadge } from '../components/StatusBadge';
import { useAsync } from '../hooks/useAsync';
import type { Cita, Medico, Paciente } from '../types/domain';
import { formatDateTime, fullName } from '../utils/format';

type DetailType = 'paciente' | 'medico' | 'cita';

export function DetailPage() {
  const { tipo, id } = useParams();
  const detailType = tipo as DetailType;

  const loader = useCallback(async () => {
    if (!id) throw new Error('Identificador invalido.');
    if (detailType === 'paciente') return { type: detailType, item: await pacientesApi.get(id) };
    if (detailType === 'medico') return { type: detailType, item: await medicosApi.get(id) };
    if (detailType === 'cita') return { type: detailType, item: await citasApi.get(Number(id)) };
    throw new Error('Tipo de detalle no soportado.');
  }, [detailType, id]);

  const { data, loading, error } = useAsync(loader);

  return (
    <>
      <PageHeader
        title="Detalle"
        subtitle="Informacion completa del registro seleccionado."
        actions={<Link className="secondary-button" to={backRoute(detailType)}><ArrowLeft size={18} /> Volver</Link>}
      />
      <Alert type="error" message={error} />
      {loading && <LoadingState />}
      {data?.type === 'paciente' && <PersonDetail person={data.item as Paciente} />}
      {data?.type === 'medico' && <DoctorDetail medico={data.item as Medico} />}
      {data?.type === 'cita' && <AppointmentDetail cita={data.item as Cita} />}
    </>
  );
}

function PersonDetail({ person }: { person: Paciente }) {
  return (
    <section className="detail-panel">
      <h2>{fullName(person)}</h2>
      <StatusBadge active={person.activo} />
      <DetailGrid items={[
        ['Documento', `${person.tipoDocumento} ${person.numeroDocumento}`],
        ['Email', person.email || 'Sin email'],
        ['Telefono', person.telefono || 'Sin telefono'],
        ['Fecha expedicion', person.fechaExpedicionDoc || 'Sin fecha']
      ]} />
    </section>
  );
}

function DoctorDetail({ medico }: { medico: Medico }) {
  return (
    <section className="detail-panel">
      <h2>{fullName(medico)}</h2>
      <StatusBadge active={medico.activo} />
      <DetailGrid items={[
        ['Documento', `${medico.tipoDocumento} ${medico.numeroDocumento}`],
        ['Especialidad', medico.especialidadNombre || 'Sin especialidad'],
        ['Email', medico.email || 'Sin email'],
        ['Telefono', medico.telefono || 'Sin telefono']
      ]} />
    </section>
  );
}

function AppointmentDetail({ cita }: { cita: Cita }) {
  return (
    <section className="detail-panel">
      <h2>{cita.motivo}</h2>
      <DetailGrid items={[
        ['Fecha y hora', formatDateTime(cita.fechaHora)],
        ['Paciente', `${cita.pacienteNombreCompleto} (${cita.pacienteNumeroDocumento})`],
        ['Medico', `${cita.medicoNombreCompleto} (${cita.medicoNumeroDocumento})`],
        ['Especialidad', cita.especialidadNombre || 'Sin especialidad'],
        ['Observaciones', cita.observaciones || 'Sin observaciones']
      ]} />
    </section>
  );
}

function DetailGrid({ items }: { items: Array<[string, string]> }) {
  return (
    <dl className="detail-grid">
      {items.map(([label, value]) => (
        <div key={label}>
          <dt>{label}</dt>
          <dd>{value}</dd>
        </div>
      ))}
    </dl>
  );
}

function backRoute(type: DetailType) {
  if (type === 'paciente') return '/pacientes';
  if (type === 'medico') return '/medicos';
  return '/citas';
}
