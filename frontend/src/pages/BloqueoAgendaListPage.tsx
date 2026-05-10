import { Ban, Edit, Plus } from 'lucide-react';
import { useCallback, useState } from 'react';
import { Link } from 'react-router-dom';
import { bloqueoAgendaApi } from '../api/clinicApi';
import { getErrorMessage } from '../api/http';
import { Alert } from '../components/Alert';
import { DataTable, type Column } from '../components/DataTable';
import { EmptyState } from '../components/EmptyState';
import { LoadingState } from '../components/LoadingState';
import { PageHeader } from '../components/PageHeader';
import { StatusBadge } from '../components/StatusBadge';
import { useAsync } from '../hooks/useAsync';
import { useAuth } from '../hooks/useAuth';
import type { BloqueoAgendaResponse } from '../types/domain';
import { formatDateTime } from '../utils/format';

export function BloqueoAgendaListPage() {
  const { isAdmin, isDoctor, getMedicoId } = useAuth();
  const [message, setMessage] = useState<string | null>(null);
  const [actionError, setActionError] = useState<string | null>(null);

  const loader = useCallback(() => {
    const medicoId = getMedicoId();
    if (isDoctor()) {
      if (!medicoId) throw new Error('Tu usuario doctor no tiene un medico asociado. Contacta al administrador.');
      return bloqueoAgendaApi.listarBloqueosPorMedico(medicoId);
    }
    return bloqueoAgendaApi.listarBloqueos();
  }, [getMedicoId, isDoctor]);

  const { data, loading, error, execute } = useAsync(loader);

  const desactivar = async (id: number) => {
    if (!confirm('Confirmar desactivacion del bloqueo de agenda.')) return;
    setMessage(null);
    setActionError(null);
    try {
      const response = await bloqueoAgendaApi.desactivarBloqueo(id);
      setMessage(response.message);
      await execute();
    } catch (err) {
      setActionError(getErrorMessage(err));
    }
  };

  const columns: Column<BloqueoAgendaResponse>[] = [
    { header: 'Medico', render: (item) => <strong>{item.medicoNombreCompleto ?? item.medicoNumeroDocumento}</strong> },
    { header: 'Inicio', render: (item) => formatDateTime(item.fechaInicio) },
    { header: 'Fin', render: (item) => formatDateTime(item.fechaFin) },
    { header: 'Motivo', render: (item) => item.motivo },
    { header: 'Estado', render: (item) => <StatusBadge active={item.activo} /> },
    {
      header: 'Acciones',
      className: 'actions-cell',
      render: (item) => isAdmin() ? (
        <div className="row-actions">
          <Link className="icon-button" to={`/bloqueo-agenda/${item.id}/editar`} title="Editar"><Edit size={16} /></Link>
          {item.activo && <button className="icon-button danger" type="button" onClick={() => desactivar(item.id)} title="Desactivar"><Ban size={16} /></button>}
        </div>
      ) : <span className="field-hint">Consulta</span>
    }
  ];

  return (
    <>
      <PageHeader
        title="Bloqueos de agenda"
        subtitle={isDoctor() ? 'Consulta los bloqueos asociados a tu agenda.' : 'Administra rangos en los que un medico no puede recibir citas.'}
        actions={isAdmin() ? <Link className="primary-button" to="/bloqueo-agenda/nuevo"><Plus size={18} /> Nuevo bloqueo</Link> : undefined}
      />
      <Alert type="success" message={message} />
      <Alert type="error" message={actionError ?? error} />
      {loading && <LoadingState />}
      {data?.length ? (
        <DataTable columns={columns} data={data} keyExtractor={(item) => item.id} />
      ) : !loading && !error && (
        <EmptyState title="Sin bloqueos" description="No hay bloqueos de agenda registrados." />
      )}
    </>
  );
}
