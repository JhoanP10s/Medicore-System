import { Edit, Eye, Plus, Trash2 } from 'lucide-react';
import { useCallback, useState } from 'react';
import { Link } from 'react-router-dom';
import { citasApi } from '../api/clinicApi';
import { getErrorMessage } from '../api/http';
import { Alert } from '../components/Alert';
import { DataTable, type Column } from '../components/DataTable';
import { EmptyState } from '../components/EmptyState';
import { LoadingState } from '../components/LoadingState';
import { PageHeader } from '../components/PageHeader';
import { useAsync } from '../hooks/useAsync';
import type { Cita } from '../types/domain';
import { formatDateTime } from '../utils/format';

export function CitaListPage() {
  const loader = useCallback(() => citasApi.list(), []);
  const { data, loading, error, execute } = useAsync(loader);
  const [message, setMessage] = useState<string | null>(null);
  const [actionError, setActionError] = useState<string | null>(null);

  const handleDelete = async (id: number) => {
    if (!confirm('Confirmar eliminacion de la cita.')) return;
    try {
      const response = await citasApi.remove(id);
      setMessage(response.message);
      await execute();
    } catch (err) {
      setActionError(getErrorMessage(err));
    }
  };

  const columns: Column<Cita>[] = [
    { header: 'Fecha', render: (c) => formatDateTime(c.fechaHora) },
    { header: 'Paciente', render: (c) => <strong>{c.pacienteNombreCompleto}</strong> },
    { header: 'Medico', render: (c) => c.medicoNombreCompleto },
    { header: 'Motivo', render: (c) => c.motivo },
    {
      header: 'Acciones',
      className: 'actions-cell',
      render: (c) => (
        <div className="row-actions">
          <Link className="icon-button" to={`/detalle/cita/${c.id}`} title="Ver detalle"><Eye size={16} /></Link>
          <Link className="icon-button" to={`/citas/${c.id}/editar`} title="Editar"><Edit size={16} /></Link>
          <button className="icon-button danger" type="button" onClick={() => handleDelete(c.id)} title="Eliminar"><Trash2 size={16} /></button>
        </div>
      )
    }
  ];

  return (
    <>
      <PageHeader title="Citas" subtitle="Agenda y seguimiento de consultas medicas." actions={<Link className="primary-button" to="/citas/nueva"><Plus size={18} /> Nueva cita</Link>} />
      <Alert type="success" message={message} />
      <Alert type="error" message={actionError ?? error} />
      {loading && <LoadingState />}
      {data?.length ? <DataTable columns={columns} data={data} keyExtractor={(c) => c.id} /> : !loading && <EmptyState title="Sin citas" description="Aun no hay citas registradas." />}
    </>
  );
}
