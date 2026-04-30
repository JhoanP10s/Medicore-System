import { Edit, Eye, Plus, Trash2 } from 'lucide-react';
import { useCallback, useState } from 'react';
import { Link } from 'react-router-dom';
import { getErrorMessage } from '../api/http';
import { pacientesApi } from '../api/clinicApi';
import { Alert } from '../components/Alert';
import { DataTable, type Column } from '../components/DataTable';
import { EmptyState } from '../components/EmptyState';
import { LoadingState } from '../components/LoadingState';
import { PageHeader } from '../components/PageHeader';
import { StatusBadge } from '../components/StatusBadge';
import { useAsync } from '../hooks/useAsync';
import type { Paciente } from '../types/domain';
import { fullName } from '../utils/format';

export function PacienteListPage() {
  const loader = useCallback(() => pacientesApi.list(), []);
  const { data, loading, error, execute } = useAsync(loader);
  const [success, setSuccess] = useState<string | null>(null);
  const [actionError, setActionError] = useState<string | null>(null);

  const handleDelete = async (documento: string) => {
    if (!confirm('Confirmar eliminacion o desactivacion del paciente.')) return;
    setActionError(null);
    setSuccess(null);
    try {
      const response = await pacientesApi.remove(documento);
      setSuccess(response.message);
      await execute();
    } catch (err) {
      setActionError(getErrorMessage(err));
    }
  };

  const columns: Column<Paciente>[] = [
    { header: 'Paciente', render: (p) => <strong>{fullName(p)}</strong> },
    { header: 'Documento', render: (p) => `${p.tipoDocumento} ${p.numeroDocumento}` },
    { header: 'Contacto', render: (p) => p.email || p.telefono || 'Sin contacto' },
    { header: 'Estado', render: (p) => <StatusBadge active={p.activo} /> },
    {
      header: 'Acciones',
      className: 'actions-cell',
      render: (p) => (
        <div className="row-actions">
          <Link className="icon-button" to={`/detalle/paciente/${p.numeroDocumento}`} title="Ver detalle"><Eye size={16} /></Link>
          <Link className="icon-button" to={`/pacientes/${p.numeroDocumento}/editar`} title="Editar"><Edit size={16} /></Link>
          <button className="icon-button danger" type="button" onClick={() => handleDelete(p.numeroDocumento)} title="Eliminar"><Trash2 size={16} /></button>
        </div>
      )
    }
  ];

  return (
    <>
      <PageHeader
        title="Pacientes"
        subtitle="Consulta y administra informacion clinica basica."
        actions={<Link className="primary-button" to="/pacientes/nuevo"><Plus size={18} /> Nuevo paciente</Link>}
      />
      <Alert type="success" message={success} />
      <Alert type="error" message={actionError ?? error} />
      {loading && <LoadingState />}
      {data?.length ? <DataTable columns={columns} data={data} keyExtractor={(p) => p.numeroDocumento} /> : !loading && <EmptyState title="Sin pacientes" description="Aun no hay pacientes registrados." />}
    </>
  );
}
