import { Edit, Eye, FilePlus2 } from 'lucide-react';
import { useCallback } from 'react';
import { Link } from 'react-router-dom';
import { historiaClinicaApi } from '../api/clinicApi';
import { Alert } from '../components/Alert';
import { DataTable, type Column } from '../components/DataTable';
import { EmptyState } from '../components/EmptyState';
import { LoadingState } from '../components/LoadingState';
import { PageHeader } from '../components/PageHeader';
import { useAsync } from '../hooks/useAsync';
import { useAuth } from '../hooks/useAuth';
import type { HistoriaClinicaResponse } from '../types/domain';
import { formatDateTime } from '../utils/format';

export function HistoriaClinicaListPage() {
  const { isDoctor, getMedicoId } = useAuth();

  const loader = useCallback(() => {
    const medicoId = getMedicoId();
    if (isDoctor()) {
      if (!medicoId) throw new Error('Tu usuario doctor no tiene un medico asociado. Contacta al administrador.');
      return historiaClinicaApi.listarHistoriasPorMedico(medicoId);
    }
    return historiaClinicaApi.listarHistoriasClinicas();
  }, [getMedicoId, isDoctor]);

  const { data, loading, error } = useAsync(loader);
  const doctorWithoutMedico = isDoctor() && !getMedicoId();

  const rows = Array.isArray(data) ? data : [];

  const columns: Column<HistoriaClinicaResponse>[] = [
    { header: 'Paciente', render: (historia) => <strong>{historia.pacienteNombreCompleto}</strong> },
    { header: 'Medico', render: (historia) => historia.medicoNombreCompleto },
    { header: 'Fecha registro', render: (historia) => formatDateTime(historia.fechaRegistro) },
    { header: 'Cita', render: (historia) => `#${historia.citaId} - ${formatDateTime(historia.citaFechaHora)}` },
    { header: 'Estado cita', render: (historia) => <span className="badge badge-info">{historia.citaEstado}</span> },
    { header: 'Diagnostico', render: (historia) => resumen(historia.diagnostico) },
    {
      header: 'Acciones',
      className: 'actions-cell',
      render: (historia) => (
        <div className="row-actions">
          <Link className="icon-button" to={`/historias-clinicas/${historia.id}`} title="Ver detalle"><Eye size={16} /></Link>
          <Link className="icon-button" to={`/historias-clinicas/${historia.id}/editar`} title="Editar"><Edit size={16} /></Link>
        </div>
      )
    }
  ];

  return (
    <>
      <PageHeader
        title="Historias clinicas"
        subtitle="Consulta y seguimiento clinico asociado a citas medicas."
        actions={!doctorWithoutMedico ? <Link className="primary-button" to="/historias-clinicas/nueva"><FilePlus2 size={18} /> Nueva historia</Link> : undefined}
      />
      <Alert type="error" message={error} />
      {loading && <LoadingState />}
      {rows.length ? (
        <DataTable columns={columns} data={rows} keyExtractor={(historia) => historia.id} />
      ) : !loading && (
        <EmptyState title="Sin historias clinicas" description="Aun no hay historias clinicas registradas." />
      )}
    </>
  );
}

function resumen(texto?: string | null) {
  if (!texto) return 'Sin diagnostico';
  return texto.length > 80 ? texto.slice(0, 80) + '...' : texto;
}
