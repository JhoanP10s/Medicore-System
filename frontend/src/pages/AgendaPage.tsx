import { FormEvent, useCallback, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { agendaApi, medicosApi } from '../api/clinicApi';
import { getErrorMessage } from '../api/http';
import { Alert } from '../components/Alert';
import { EmptyState } from '../components/EmptyState';
import { LoadingState } from '../components/LoadingState';
import { PageHeader } from '../components/PageHeader';
import { useAuth } from '../hooks/useAuth';
import { useAsync } from '../hooks/useAsync';
import type { HorarioDisponibleResponse } from '../types/domain';
import { formatDateTime, fullName } from '../utils/format';

export function AgendaPage() {
  const { auth, isAdmin, isDoctor, getMedicoId } = useAuth();
  const medicoIdSesion = getMedicoId();
  const [medicoId, setMedicoId] = useState(medicoIdSesion ?? '');
  const [fecha, setFecha] = useState(() => new Date().toISOString().slice(0, 10));
  const [duracionMinutos, setDuracionMinutos] = useState(30);
  const [slots, setSlots] = useState<HorarioDisponibleResponse[]>([]);
  const [consultando, setConsultando] = useState(false);
  const [hasConsulted, setHasConsulted] = useState(false);
  const [emptyMessage, setEmptyMessage] = useState('Aun no has consultado horarios.');
  const [error, setError] = useState<string | null>(null);

  const cargarMedicos = useCallback(() => isAdmin() ? medicosApi.list() : Promise.resolve([]), [isAdmin]);
  const { data: medicos, loading: cargandoMedicos, error: errorMedicos } = useAsync(cargarMedicos, isAdmin());

  const medicoOperativo = useMemo(() => isDoctor() ? medicoIdSesion : medicoId, [isDoctor, medicoIdSesion, medicoId]);

  const consultar = async (event: FormEvent) => {
    event.preventDefault();
    setError(null);
    setSlots([]);
    setHasConsulted(false);

    if (!medicoOperativo) {
      setError(auth?.rol === 'DOCTOR' ? 'Tu usuario doctor no tiene un medico asociado. Contacta al administrador.' : 'Debes seleccionar medico, fecha y duracion.');
      return;
    }

    if (duracionMinutos < 15 || duracionMinutos > 240) {
      setError('La duracion debe estar entre 15 y 240 minutos.');
      return;
    }

    setConsultando(true);
    try {
      const response = await agendaApi.obtenerHorariosDisponibles(medicoOperativo, fecha, duracionMinutos);
      setSlots(response);
      setHasConsulted(true);
      setEmptyMessage(response.length ? '' : 'No hay horarios disponibles para esta fecha y duracion.');
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setConsultando(false);
    }
  };

  return (
    <>
      <PageHeader
        title="Agenda"
        subtitle="Consulta horarios disponibles segun disponibilidad, citas y bloqueos."
        actions={
          <>
            <Link className="secondary-button" to="/disponibilidad-medica">
              {isAdmin() ? 'Gestionar disponibilidad medica' : 'Ver mi disponibilidad'}
            </Link>
            <Link className="secondary-button" to="/bloqueo-agenda">
              {isAdmin() ? 'Gestionar bloqueos de agenda' : 'Ver mis bloqueos'}
            </Link>
          </>
        }
      />
      <Alert type="error" message={error ?? errorMedicos} />
      <form className="panel form-grid" onSubmit={consultar}>
        {isAdmin() && (
          <label>
            Medico
            <select value={medicoId} onChange={(event) => setMedicoId(event.target.value)} required>
              <option value="">Seleccionar</option>
              {medicos?.map((medico) => (
                <option key={medico.numeroDocumento} value={medico.numeroDocumento}>{fullName(medico)} - {medico.especialidadNombre}</option>
              ))}
            </select>
          </label>
        )}
        {isDoctor() && (
          <label>
            Medico asociado
            <input value={medicoIdSesion ?? 'Sin medico asociado'} disabled />
            {!medicoIdSesion && <span className="field-error">Tu usuario doctor no tiene un medico asociado. Contacta al administrador.</span>}
          </label>
        )}
        <label>
          Fecha
          <input type="date" value={fecha} onChange={(event) => setFecha(event.target.value)} required />
        </label>
        <label>
          Duracion
          <input type="number" min={15} max={240} value={duracionMinutos} onChange={(event) => setDuracionMinutos(Number(event.target.value))} required />
        </label>
        <div className="form-actions">
          <button className="primary-button" type="submit" disabled={consultando || cargandoMedicos || (isDoctor() && !medicoIdSesion)}>
            {consultando ? 'Consultando...' : 'Consultar horarios'}
          </button>
        </div>
      </form>

      {consultando && <LoadingState />}
      {!consultando && slots.length > 0 && (
        <section className="panel slot-list">
          {slots.map((slot) => (
            <article className="slot-item" key={slot.inicio}>
              <strong>{formatDateTime(slot.inicio)}</strong>
              <span>Fin: {formatDateTime(slot.fin)}</span>
            </article>
          ))}
        </section>
      )}
      {!consultando && !error && slots.length === 0 && (
        <EmptyState
          title={hasConsulted ? 'Sin horarios disponibles' : 'Sin horarios consultados'}
          description={emptyMessage}
        />
      )}
    </>
  );
}
