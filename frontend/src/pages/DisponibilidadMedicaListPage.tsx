import { Ban, Edit, Plus } from 'lucide-react';
import { useCallback, useState } from 'react';
import { Link } from 'react-router-dom';
import { disponibilidadMedicaApi } from '../api/clinicApi';
import { getErrorMessage } from '../api/http';
import { Alert } from '../components/Alert';
import { DataTable, type Column } from '../components/DataTable';
import { EmptyState } from '../components/EmptyState';
import { LoadingState } from '../components/LoadingState';
import { PageHeader } from '../components/PageHeader';
import { StatusBadge } from '../components/StatusBadge';
import { useAsync } from '../hooks/useAsync';
import { useAuth } from '../hooks/useAuth';
import type { DisponibilidadMedicaResponse } from '../types/domain';

export function DisponibilidadMedicaListPage() {
  const { isAdmin, isDoctor, getMedicoId } = useAuth();
  const [message, setMessage] = useState<string | null>(null);
  const [actionError, setActionError] = useState<string | null>(null);

  const loader = useCallback(() => {
    const medicoId = getMedicoId();
    if (isDoctor()) {
      if (!medicoId) throw new Error('Tu usuario doctor no tiene un medico asociado. Contacta al administrador.');
      return disponibilidadMedicaApi.listarDisponibilidadesPorMedico(medicoId);
    }
    return disponibilidadMedicaApi.listarDisponibilidades();
  }, [getMedicoId, isDoctor]);

  const { data, loading, error, execute } = useAsync(loader);

  const desactivar = async (id: number) => {
    if (!confirm('Confirmar desactivacion de la disponibilidad medica.')) return;
    setMessage(null);
    setActionError(null);
    try {
      const response = await disponibilidadMedicaApi.desactivarDisponibilidad(id);
      setMessage(response.message);
      await execute();
    } catch (err) {
      setActionError(getErrorMessage(err));
    }
  };

  const columns: Column<DisponibilidadMedicaResponse>[] = [
    { header: 'Medico', render: (item) => <strong>{item.medicoNombreCompleto ?? item.medicoNumeroDocumento}</strong> },
    { header: 'Documento', render: (item) => item.medicoNumeroDocumento },
    { header: 'Dia', render: (item) => formatDia(item.diaSemana) },
    { header: 'Hora inicio', render: (item) => formatHora(item.horaInicio) },
    { header: 'Hora fin', render: (item) => formatHora(item.horaFin) },
    { header: 'Estado', render: (item) => <StatusBadge active={item.activo} /> },
    {
      header: 'Acciones',
      className: 'actions-cell',
      render: (item) => isAdmin() ? (
        <div className="row-actions">
          <Link className="icon-button" to={`/disponibilidad-medica/${item.id}/editar`} title="Editar"><Edit size={16} /></Link>
          {item.activo && <button className="icon-button danger" type="button" onClick={() => desactivar(item.id)} title="Desactivar"><Ban size={16} /></button>}
        </div>
      ) : <span className="field-hint">Consulta</span>
    }
  ];

  return (
    <>
      <PageHeader
        title="Disponibilidad medica"
        subtitle={isDoctor() ? 'Consulta tus bloques semanales de atencion.' : 'Administra bloques semanales de atencion por medico.'}
        actions={isAdmin() ? <Link className="primary-button" to="/disponibilidad-medica/nueva"><Plus size={18} /> Nueva disponibilidad</Link> : undefined}
      />
      <Alert type="success" message={message} />
      <Alert type="error" message={actionError ?? error} />
      {loading && <LoadingState />}
      {data?.length ? (
        <DataTable columns={columns} data={data} keyExtractor={(item) => item.id} />
      ) : !loading && !error && (
        <EmptyState title="Sin disponibilidad" description="No hay bloques de disponibilidad registrados." />
      )}
    </>
  );
}

function formatHora(value: string) {
  return value?.slice(0, 5) || 'Sin hora';
}

function formatDia(value: string) {
  return value.charAt(0) + value.slice(1).toLowerCase();
}
