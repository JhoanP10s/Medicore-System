import { Plus, Trash2 } from 'lucide-react';
import { FormEvent, useCallback, useState } from 'react';
import { especialidadesApi } from '../api/clinicApi';
import { getErrorMessage } from '../api/http';
import { Alert } from '../components/Alert';
import { DataTable, type Column } from '../components/DataTable';
import { EmptyState } from '../components/EmptyState';
import { LoadingState } from '../components/LoadingState';
import { PageHeader } from '../components/PageHeader';
import { StatusBadge } from '../components/StatusBadge';
import { useAsync } from '../hooks/useAsync';
import type { Especialidad } from '../types/domain';

export function EspecialidadListPage() {
  const loader = useCallback(() => especialidadesApi.list(), []);
  const { data, loading, error, execute } = useAsync(loader);
  const [nombre, setNombre] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [message, setMessage] = useState<string | null>(null);
  const [actionError, setActionError] = useState<string | null>(null);

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setActionError(null);
    setMessage(null);
    try {
      await especialidadesApi.create({ nombre, descripcion, activo: true });
      setNombre('');
      setDescripcion('');
      setMessage('Especialidad creada correctamente');
      await execute();
    } catch (err) {
      setActionError(getErrorMessage(err));
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Confirmar eliminacion o desactivacion de la especialidad.')) return;
    try {
      const response = await especialidadesApi.remove(id);
      setMessage(response.message);
      await execute();
    } catch (err) {
      setActionError(getErrorMessage(err));
    }
  };

  const columns: Column<Especialidad>[] = [
    { header: 'Nombre', render: (e) => <strong>{e.nombre}</strong> },
    { header: 'Descripcion', render: (e) => e.descripcion || 'Sin descripcion' },
    { header: 'Estado', render: (e) => <StatusBadge active={e.activo} /> },
    { header: 'Acciones', className: 'actions-cell', render: (e) => <button className="icon-button danger" type="button" onClick={() => handleDelete(e.id)} title="Eliminar"><Trash2 size={16} /></button> }
  ];

  return (
    <>
      <PageHeader title="Especialidades" subtitle="Catalogo disponible para asociar medicos." />
      <Alert type="success" message={message} />
      <Alert type="error" message={actionError ?? error} />
      <form className="inline-form panel" onSubmit={handleSubmit}>
        <label>
          Nombre
          <input value={nombre} onChange={(event) => setNombre(event.target.value)} required />
        </label>
        <label>
          Descripcion
          <input value={descripcion} onChange={(event) => setDescripcion(event.target.value)} />
        </label>
        <button className="primary-button" type="submit"><Plus size={18} /> Crear</button>
      </form>
      {loading && <LoadingState />}
      {data?.length ? <DataTable columns={columns} data={data} keyExtractor={(e) => e.id} /> : !loading && <EmptyState title="Sin especialidades" description="Crea la primera especialidad para registrar medicos." />}
    </>
  );
}
