import { ArrowLeft, Edit } from 'lucide-react';
import { useCallback } from 'react';
import { Link, useParams } from 'react-router-dom';
import { historiaClinicaApi } from '../api/clinicApi';
import { Alert } from '../components/Alert';
import { LoadingState } from '../components/LoadingState';
import { PageHeader } from '../components/PageHeader';
import { useAsync } from '../hooks/useAsync';
import type { HistoriaClinicaResponse } from '../types/domain';
import { formatDateTime } from '../utils/format';

export function HistoriaClinicaDetailPage() {
  const { id } = useParams();

  const loader = useCallback(() => {
    if (!id) throw new Error('Identificador invalido.');
    return historiaClinicaApi.obtenerHistoriaClinica(Number(id));
  }, [id]);

  const { data, loading, error } = useAsync(loader);

  return (
    <>
      <PageHeader
        title="Detalle de historia clinica"
        subtitle="Informacion clinica registrada para la cita asociada."
        actions={(
          <div className="page-actions">
            <Link className="secondary-button" to="/historias-clinicas"><ArrowLeft size={18} /> Volver</Link>
            {data && <Link className="primary-button" to={`/historias-clinicas/${data.id}/editar`}><Edit size={18} /> Editar</Link>}
          </div>
        )}
      />
      <Alert type="error" message={error} />
      {loading && <LoadingState />}
      {data && <HistoriaClinicaDetail historia={data} />}
    </>
  );
}

function HistoriaClinicaDetail({ historia }: { historia: HistoriaClinicaResponse }) {
  return (
    <section className="detail-panel clinical-record-panel">
      <h2>{historia.pacienteNombreCompleto}</h2>
      <DetailGrid items={[
        ['Paciente', `${historia.pacienteNombreCompleto} (${historia.pacienteNumeroDocumento})`],
        ['Medico', `${historia.medicoNombreCompleto} (${historia.medicoNumeroDocumento})`],
        ['Fecha registro', formatDateTime(historia.fechaRegistro)],
        ['Cita asociada', `#${historia.citaId} - ${formatDateTime(historia.citaFechaHora)}`],
        ['Estado cita', historia.citaEstado]
      ]} />
      <ClinicalBlock title="Sintomas" value={historia.sintomas} />
      <ClinicalBlock title="Diagnostico" value={historia.diagnostico} />
      <ClinicalBlock title="Tratamiento" value={historia.tratamiento} />
      <ClinicalBlock title="Observaciones" value={historia.observaciones || 'Sin observaciones'} />
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

function ClinicalBlock({ title, value }: { title: string; value: string }) {
  return (
    <article className="clinical-block">
      <h3>{title}</h3>
      <p>{value}</p>
    </article>
  );
}
