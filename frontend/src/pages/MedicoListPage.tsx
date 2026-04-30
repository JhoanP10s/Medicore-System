import { Edit, Eye, Plus, Trash2 } from 'lucide-react';
import { useCallback, useState } from 'react';
import { Link } from 'react-router-dom';
import { medicosApi } from '../api/clinicApi';
import { getErrorMessage } from '../api/http';
import { Alert } from '../components/Alert';
import { DataTable, type Column } from '../components/DataTable';
import { EmptyState } from '../components/EmptyState';
import { LoadingState } from '../components/LoadingState';
import { PageHeader } from '../components/PageHeader';
import { StatusBadge } from '../components/StatusBadge';
import { useAsync } from '../hooks/useAsync';
import type { Medico } from '../types/domain';
import { fullName } from '../utils/format';

export function MedicoListPage() {
  const loader = useCallback(() => medicosApi.list(), []);
  const { data, loading, error, execute } = useAsync(loader);
  const [message, setMessage] = useState<string | null>(null);
  const [actionError, setActionError] = useState<string | null>(null);

  const handleDelete = async (documento: string) => {
    if (!confirm('Confirmar eliminacion o desactivacion del medico.')) return;
    try {
      const response = await medicosApi.remove(documento);
      setMessage(response.message);
      await execute();
    } catch (err) {
      setActionError(getErrorMessage(err));
    }
  };

  const columns: Column<Medico>[] = [
    { header: 'Medico', render: (m) => <strong>{fullName(m)}</strong> },
    { header: 'Documento', render: (m) => `${m.tipoDocumento} ${m.numeroDocumento}` },
    { header: 'Especialidad', render: (m) => m.especialidadNombre ?? 'Sin especialidad' },
    { header: 'Estado', render: (m) => <StatusBadge active={m.activo} /> },
    {
      header: 'Acciones',
      className: 'actions-cell',
      render: (m) => (
        <div className="row-actions">
          <Link className="icon-button" to={`/detalle/medico/${m.numeroDocumento}`} title="Ver detalle"><Eye size={16} /></Link>
          <Link className="icon-button" to={`/medicos/${m.numeroDocumento}/editar`} title="Editar"><Edit size={16} /></Link>
          <button className="icon-button danger" type="button" onClick={() => handleDelete(m.numeroDocumento)} title="Eliminar"><Trash2 size={16} /></button>
        </div>
      )
    }
  ];

  return (
    <>
      <PageHeader title="Medicos" subtitle="Gestion del equipo medico y sus especialidades." actions={<Link className="primary-button" to="/medicos/nuevo"><Plus size={18} /> Nuevo medico</Link>} />
      <Alert type="success" message={message} />
      <Alert type="error" message={actionError ?? error} />
      {loading && <LoadingState />}
      {data?.length ? <DataTable columns={columns} data={data} keyExtractor={(m) => m.numeroDocumento} /> : !loading && <EmptyState title="Sin medicos" description="Aun no hay medicos registrados." />}
    </>
  );
}
