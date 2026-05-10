import { FormEvent, useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { agendaApi, citasApi, medicosApi, pacientesApi } from '../api/clinicApi';
import { getErrorMessage } from '../api/http';
import { Alert } from '../components/Alert';
import { EmptyState } from '../components/EmptyState';
import { LoadingState } from '../components/LoadingState';
import { PageHeader } from '../components/PageHeader';
import { useAsync } from '../hooks/useAsync';
import { useAuth } from '../hooks/useAuth';
import type { CitaRequest, HorarioDisponibleResponse } from '../types/domain';
import { formatDateTime, fullName, toDateTimeLocal } from '../utils/format';

const initialForm: CitaRequest = {
  numeroDocumentoPaciente: '',
  numeroDocumentoMedico: '',
  fechaHora: '',
  motivo: '',
  observaciones: '',
  duracionMinutos: 30
};

export function CitaFormPage() {
  const { id } = useParams();
  const isEditing = Boolean(id);
  const navigate = useNavigate();
  const { isDoctor, getMedicoId } = useAuth();
  const medicoIdSesion = getMedicoId();
  const [form, setForm] = useState<CitaRequest>(() => ({ ...initialForm, numeroDocumentoMedico: medicoIdSesion ?? '' }));
  const [fechaConsulta, setFechaConsulta] = useState(() => new Date().toISOString().slice(0, 10));
  const [slots, setSlots] = useState<HorarioDisponibleResponse[]>([]);
  const [consultandoSlots, setConsultandoSlots] = useState(false);
  const [slotMessage, setSlotMessage] = useState<string | null>(null);
  const [manualWarning, setManualWarning] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const loader = useCallback(async () => {
    const [pacientes, medicos, cita] = await Promise.all([
      pacientesApi.list(),
      isDoctor() ? Promise.resolve([]) : medicosApi.list(),
      isEditing ? citasApi.get(Number(id)) : Promise.resolve(null)
    ]);
    return { pacientes, medicos, cita };
  }, [id, isEditing, isDoctor]);

  const { data, loading, error: loadError } = useAsync(loader);

  useEffect(() => {
    if (data?.cita) {
      const fechaLocal = toDateTimeLocal(data.cita.fechaHora);
      setForm({
        numeroDocumentoPaciente: data.cita.pacienteNumeroDocumento,
        numeroDocumentoMedico: data.cita.medicoNumeroDocumento,
        fechaHora: fechaLocal,
        motivo: data.cita.motivo,
        observaciones: data.cita.observaciones ?? '',
        duracionMinutos: data.cita.duracionMinutos ?? 30
      });
      setFechaConsulta(fechaLocal.slice(0, 10));
    } else if (isDoctor() && medicoIdSesion) {
      setForm((current) => ({ ...current, numeroDocumentoMedico: medicoIdSesion }));
    }
  }, [data, isDoctor, medicoIdSesion]);

  const medicoSeleccionado = useMemo(() => isDoctor() ? medicoIdSesion : form.numeroDocumentoMedico, [isDoctor, medicoIdSesion, form.numeroDocumentoMedico]);

  const updateField = (field: keyof CitaRequest, value: string | number) => {
    setForm((current) => ({ ...current, [field]: value }));
  };

  const consultarHorarios = async () => {
    setError(null);
    setSlotMessage(null);
    setManualWarning(null);
    setSlots([]);

    if (!medicoSeleccionado) {
      setError(isDoctor() ? 'Tu usuario doctor no tiene un medico asociado. Contacta al administrador.' : 'Selecciona un medico para consultar horarios.');
      return;
    }

    if (form.duracionMinutos < 15 || form.duracionMinutos > 240) {
      setError('La duracion debe estar entre 15 y 240 minutos.');
      return;
    }

    setConsultandoSlots(true);
    try {
      const response = await agendaApi.obtenerHorariosDisponibles(medicoSeleccionado, fechaConsulta, form.duracionMinutos);
      setSlots(response);
      if (!response.length) setSlotMessage('No hay horarios disponibles para los parametros seleccionados.');
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setConsultandoSlots(false);
    }
  };

  const seleccionarSlot = (slot: HorarioDisponibleResponse) => {
    updateField('fechaHora', toDateTimeLocal(slot.inicio));
    setManualWarning(null);
    setSlotMessage(`Horario seleccionado: ${formatDateTime(slot.inicio)}`);
  };

  const actualizarFechaManual = (value: string) => {
    updateField('fechaHora', value);
    setSlotMessage(null);
    setManualWarning('El backend validara disponibilidad, bloqueos y solapamientos. El flujo recomendado es seleccionar un horario disponible.');
  };

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setSaving(true);
    setError(null);
    try {
      if (isDoctor() && !medicoIdSesion) {
        throw new Error('Tu usuario doctor no tiene un medico asociado. Contacta al administrador.');
      }
      if (form.duracionMinutos < 15 || form.duracionMinutos > 240) {
        throw new Error('La duracion debe estar entre 15 y 240 minutos.');
      }
      const payload = {
        ...form,
        numeroDocumentoMedico: medicoSeleccionado ?? form.numeroDocumentoMedico,
        fechaHora: form.fechaHora.length === 16 ? `${form.fechaHora}:00` : form.fechaHora
      };
      if (isEditing && id) {
        await citasApi.update(Number(id), payload);
      } else {
        await citasApi.create(payload);
      }
      navigate('/citas');
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setSaving(false);
    }
  };

  return (
    <>
      <PageHeader title={isEditing ? 'Editar cita' : 'Nueva cita'} subtitle="Programa una consulta entre paciente y medico usando disponibilidad de agenda." />
      <Alert type="error" message={error ?? loadError} />
      <Alert type="success" message={slotMessage} />
      <Alert type="info" message={manualWarning} />
      {loading ? <LoadingState /> : (
        <form className="panel form-grid" onSubmit={handleSubmit}>
          <label>
            Paciente
            <select value={form.numeroDocumentoPaciente} onChange={(event) => updateField('numeroDocumentoPaciente', event.target.value)} required>
              <option value="">Seleccionar</option>
              {data?.pacientes.map((paciente) => (
                <option key={paciente.numeroDocumento} value={paciente.numeroDocumento}>{fullName(paciente)}</option>
              ))}
            </select>
          </label>
          {isDoctor() ? (
            <label>
              Medico asociado
              <input value={medicoIdSesion ?? 'Sin medico asociado'} disabled />
              {!medicoIdSesion && <span className="field-error">Tu usuario doctor no tiene un medico asociado. Contacta al administrador.</span>}
            </label>
          ) : (
            <label>
              Medico
              <select value={form.numeroDocumentoMedico} onChange={(event) => updateField('numeroDocumentoMedico', event.target.value)} required>
                <option value="">Seleccionar</option>
                {data?.medicos.map((medico) => (
                  <option key={medico.numeroDocumento} value={medico.numeroDocumento}>{fullName(medico)} - {medico.especialidadNombre}</option>
                ))}
              </select>
            </label>
          )}
          <label>
            Duracion
            <input type="number" min={15} max={240} value={form.duracionMinutos} onChange={(event) => updateField('duracionMinutos', Number(event.target.value))} required />
          </label>
          <label>
            Fecha para consultar agenda
            <input type="date" value={fechaConsulta} onChange={(event) => setFechaConsulta(event.target.value)} required />
          </label>
          <div className="form-actions span-2">
            <button type="button" className="secondary-button" onClick={consultarHorarios} disabled={consultandoSlots}>
              {consultandoSlots ? 'Consultando...' : 'Consultar horarios disponibles'}
            </button>
          </div>
          {consultandoSlots && <LoadingState />}
          {slots.length > 0 && (
            <div className="slot-list span-2">
              {slots.map((slot) => (
                <button type="button" className="slot-button" key={slot.inicio} onClick={() => seleccionarSlot(slot)}>
                  {formatDateTime(slot.inicio)}
                </button>
              ))}
            </div>
          )}
          {!consultandoSlots && slotMessage && !slots.length && <div className="span-2"><EmptyState title="Sin horarios" description={slotMessage} /></div>}
          <label>
            Fecha y hora seleccionada
            <input type="datetime-local" value={form.fechaHora} onChange={(event) => actualizarFechaManual(event.target.value)} required />
            <span className="field-hint">Selecciona un horario disponible cuando sea posible.</span>
          </label>
          <label>
            Motivo
            <input value={form.motivo} onChange={(event) => updateField('motivo', event.target.value)} required />
          </label>
          <label className="span-2">
            Observaciones
            <textarea value={form.observaciones ?? ''} onChange={(event) => updateField('observaciones', event.target.value)} rows={4} />
          </label>
          <div className="form-actions">
            <button type="button" className="secondary-button" onClick={() => navigate('/citas')}>Cancelar</button>
            <button type="submit" className="primary-button" disabled={saving || (isDoctor() && !medicoIdSesion)}>{saving ? 'Guardando...' : 'Guardar'}</button>
          </div>
        </form>
      )}
    </>
  );
}
