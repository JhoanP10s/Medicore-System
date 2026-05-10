import { FormEvent, useCallback, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { bloqueoAgendaApi, medicosApi } from '../api/clinicApi';
import { getErrorMessage } from '../api/http';
import { Alert } from '../components/Alert';
import { LoadingState } from '../components/LoadingState';
import { PageHeader } from '../components/PageHeader';
import { useAsync } from '../hooks/useAsync';
import type { BloqueoAgendaRequest } from '../types/domain';
import { fullName } from '../utils/format';

const initialForm: BloqueoAgendaRequest = {
  numeroDocumentoMedico: '',
  fechaInicio: '',
  fechaFin: '',
  motivo: ''
};

export function BloqueoAgendaFormPage() {
  const { id } = useParams();
  const isEditing = Boolean(id);
  const navigate = useNavigate();
  const [form, setForm] = useState<BloqueoAgendaRequest>(initialForm);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const loader = useCallback(async () => {
    const [medicos, bloqueo] = await Promise.all([
      medicosApi.list(),
      isEditing ? bloqueoAgendaApi.obtenerBloqueo(Number(id)) : Promise.resolve(null)
    ]);
    return { medicos, bloqueo };
  }, [id, isEditing]);

  const { data, loading, error: loadError } = useAsync(loader);
  const medicosActivos = data?.medicos.filter((medico) => medico.activo !== false) ?? [];
  const medicoSeleccionado = data?.medicos.find((medico) => medico.numeroDocumento === form.numeroDocumentoMedico);
  const medicoSeleccionadoInactivo = Boolean(medicoSeleccionado && medicoSeleccionado.activo === false);

  useEffect(() => {
    if (data?.bloqueo) {
      setForm({
        numeroDocumentoMedico: data.bloqueo.medicoNumeroDocumento,
        fechaInicio: normalizeDateTimeInput(data.bloqueo.fechaInicio),
        fechaFin: normalizeDateTimeInput(data.bloqueo.fechaFin),
        motivo: data.bloqueo.motivo
      });
    }
  }, [data]);

  const updateField = (field: keyof BloqueoAgendaRequest, value: string) => {
    setForm((current) => ({ ...current, [field]: value }));
  };

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setError(null);
    setSaving(true);

    try {
      if (!form.numeroDocumentoMedico) throw new Error('El medico es obligatorio.');
      if (medicoSeleccionadoInactivo) throw new Error('El medico asociado esta inactivo. Selecciona un medico activo.');
      if (!form.fechaInicio) throw new Error('La fecha de inicio es obligatoria.');
      if (!form.fechaFin) throw new Error('La fecha de fin es obligatoria.');
      if (form.fechaInicio >= form.fechaFin) throw new Error('La fecha de inicio debe ser menor que la fecha de fin.');
      if (!form.motivo.trim()) throw new Error('El motivo es obligatorio.');

      const payload: BloqueoAgendaRequest = {
        numeroDocumentoMedico: form.numeroDocumentoMedico,
        fechaInicio: toBackendDateTime(form.fechaInicio),
        fechaFin: toBackendDateTime(form.fechaFin),
        motivo: form.motivo.trim()
      };

      if (isEditing && id) {
        await bloqueoAgendaApi.actualizarBloqueo(Number(id), payload);
      } else {
        await bloqueoAgendaApi.crearBloqueo(payload);
      }

      navigate('/bloqueo-agenda');
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setSaving(false);
    }
  };

  return (
    <>
      <PageHeader
        title={isEditing ? 'Editar bloqueo de agenda' : 'Nuevo bloqueo de agenda'}
        subtitle="Define rangos de tiempo en los que un medico no puede recibir citas."
      />
      <Alert type="error" message={error ?? loadError} />
      {medicoSeleccionadoInactivo && (
        <Alert type="info" message="El medico asociado esta inactivo. El backend puede rechazar modificaciones; selecciona un medico activo para continuar." />
      )}
      {loading ? <LoadingState /> : (
        <form className="panel form-grid" onSubmit={handleSubmit}>
          <label>
            Medico
            <select value={form.numeroDocumentoMedico} onChange={(event) => updateField('numeroDocumentoMedico', event.target.value)} required>
              <option value="">Seleccionar</option>
              {medicoSeleccionadoInactivo && data?.bloqueo && (
                <option value={data.bloqueo.medicoNumeroDocumento} disabled>
                  {data.bloqueo.medicoNombreCompleto ?? data.bloqueo.medicoNumeroDocumento} - {data.bloqueo.medicoNumeroDocumento} (inactivo)
                </option>
              )}
              {medicosActivos.map((medico) => (
                <option key={medico.numeroDocumento} value={medico.numeroDocumento}>
                  {fullName(medico)} - {medico.numeroDocumento}
                </option>
              ))}
            </select>
            <span className="field-hint">Solo se muestran medicos activos.</span>
          </label>
          <label>
            Fecha inicio
            <input type="datetime-local" value={form.fechaInicio} onChange={(event) => updateField('fechaInicio', event.target.value)} required />
          </label>
          <label>
            Fecha fin
            <input type="datetime-local" value={form.fechaFin} onChange={(event) => updateField('fechaFin', event.target.value)} required />
          </label>
          <label className="span-2">
            Motivo
            <textarea value={form.motivo} onChange={(event) => updateField('motivo', event.target.value)} rows={3} required maxLength={255} />
          </label>
          <div className="form-actions">
            <button type="button" className="secondary-button" onClick={() => navigate('/bloqueo-agenda')}>Cancelar</button>
            <button type="submit" className="primary-button" disabled={saving || medicoSeleccionadoInactivo}>{saving ? 'Guardando...' : 'Guardar'}</button>
          </div>
        </form>
      )}
    </>
  );
}

function normalizeDateTimeInput(value: string) {
  return value?.slice(0, 16) ?? '';
}

function toBackendDateTime(value: string) {
  return value.length === 16 ? `${value}:00` : value;
}
