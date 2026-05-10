import { CheckCircle2, Edit, Eye, Plus, XCircle, CircleSlash, ClipboardCheck, FilePlus2, FileText } from 'lucide-react';
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
import { useAuth } from '../hooks/useAuth';
import type { Cita, EstadoCita } from '../types/domain';
import { formatDateTime } from '../utils/format';

export function CitaListPage() {
  const { isDoctor, getMedicoId } = useAuth();
  const loader = useCallback(() => {
    const medicoId = getMedicoId();
    if (isDoctor()) {
      if (!medicoId) throw new Error('Tu usuario doctor no tiene un medico asociado. Contacta al administrador.');
      return citasApi.listarPorMedico(medicoId);
    }
    return citasApi.list();
  }, [getMedicoId, isDoctor]);
  const { data, loading, error, execute } = useAsync(loader);
  const [message, setMessage] = useState<string | null>(null);
  const [actionError, setActionError] = useState<string | null>(null);

  const cancelar = async (id: number) => {
    if (!confirm('Confirmar cancelacion logica de la cita.')) return;
    setActionError(null);
    setMessage(null);
    try {
      const response = await citasApi.cancelar(id);
      setMessage(response.message);
      await execute();
    } catch (err) {
      setActionError(getErrorMessage(err));
    }
  };

  const cambiarEstado = async (id: number, estado: EstadoCita) => {
    setActionError(null);
    setMessage(null);
    try {
      await citasApi.cambiarEstado(id, estado);
      setMessage(`Cita actualizada a ${estado}.`);
      await execute();
    } catch (err) {
      setActionError(getErrorMessage(err));
    }
  };

  const columns: Column<Cita>[] = [
    { header: 'Fecha', render: (c) => formatDateTime(c.fechaHora) },
    { header: 'Paciente', render: (c) => <strong>{c.pacienteNombreCompleto}</strong> },
    { header: 'Medico', render: (c) => c.medicoNombreCompleto },
    { header: 'Estado', render: (c) => <AppointmentStatus estado={c.estado} /> },
    { header: 'Duracion', render: (c) => `${c.duracionMinutos} min` },
    { header: 'Motivo', render: (c) => c.motivo },
    {
      header: 'Acciones',
      className: 'actions-cell',
      render: (c) => (
        <div className="row-actions">
          <Link className="icon-button" to={`/detalle/cita/${c.id}`} title="Ver detalle"><Eye size={16} /></Link>
          {puedeEditar(c.estado) && <Link className="icon-button" to={`/citas/${c.id}/editar`} title="Editar"><Edit size={16} /></Link>}
          {c.estado === 'PROGRAMADA' && <button className="icon-button" type="button" onClick={() => cambiarEstado(c.id, 'CONFIRMADA')} title="Confirmar"><CheckCircle2 size={16} /></button>}
          {c.estado === 'CONFIRMADA' && <button className="icon-button" type="button" onClick={() => cambiarEstado(c.id, 'COMPLETADA')} title="Completar"><ClipboardCheck size={16} /></button>}
          {puedeCrearHistoria(c) && <Link className="icon-button" to={`/citas/${c.id}/historia-clinica/nueva`} title="Crear historia clinica"><FilePlus2 size={16} /></Link>}
          {c.estado === 'COMPLETADA' && c.historiaClinicaId && <Link className="icon-button" to={`/historias-clinicas/${c.historiaClinicaId}`} title="Ver historia clinica"><FileText size={16} /></Link>}
          {puedeCancelar(c.estado) && <button className="icon-button danger" type="button" onClick={() => cancelar(c.id)} title="Cancelar"><XCircle size={16} /></button>}
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

function puedeEditar(estado: EstadoCita) {
  return estado === 'PROGRAMADA' || estado === 'CONFIRMADA';
}

function puedeCancelar(estado: EstadoCita) {
  return estado === 'PROGRAMADA' || estado === 'CONFIRMADA';
}

function puedeCrearHistoria(cita: Cita) {
  return cita.estado === 'CONFIRMADA' || (cita.estado === 'COMPLETADA' && !cita.historiaClinicaId);
}

function AppointmentStatus({ estado }: { estado: EstadoCita }) {
  const className = estado === 'CANCELADA' ? 'badge-muted' : estado === 'COMPLETADA' ? 'badge-success' : 'badge-info';
  return <span className={`badge ${className}`}><CircleSlash size={13} /> {estado}</span>;
}
