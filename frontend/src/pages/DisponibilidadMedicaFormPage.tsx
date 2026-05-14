import { FormEvent, useCallback, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { disponibilidadMedicaApi, medicosApi } from '../api/clinicApi';
import { getErrorMessage } from '../api/http';
import { Alert } from '../components/Alert';
import { LoadingState } from '../components/LoadingState';
import { PageHeader } from '../components/PageHeader';
import { useAsync } from '../hooks/useAsync';
import type { DiaSemana, DisponibilidadMedicaRequest } from '../types/domain';
import { fullName } from '../utils/format';

const diasSemana: DiaSemana[] = ['LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO', 'DOMINGO'];

const initialForm: DisponibilidadMedicaRequest = {
  numeroDocumentoMedico: '',
  diaSemana: 'LUNES',
  horaInicio: '',
  horaFin: ''
};

export function DisponibilidadMedicaFormPage() {
  const { id } = useParams();
  const isEditing = Boolean(id);
  const navigate = useNavigate();
  const [form, setForm] = useState<DisponibilidadMedicaRequest>(initialForm);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const loader = useCallback(async () => {
    const [medicos, disponibilidad] = await Promise.all([
      medicosApi.list(),
      isEditing ? disponibilidadMedicaApi.obtenerDisponibilidad(Number(id)) : Promise.resolve(null)
    ]);
    return { medicos, disponibilidad };
  }, [id, isEditing]);

  const { data, loading, error: loadError } = useAsync(loader);
  const medicos = Array.isArray(data?.medicos) ? data.medicos : [];
  const medicosActivos = medicos.filter((medico) => medico.activo !== false);
  const medicoSeleccionado = medicos.find((medico) => medico.numeroDocumento === form.numeroDocumentoMedico);
  const medicoSeleccionadoInactivo = Boolean(medicoSeleccionado && medicoSeleccionado.activo === false);

  useEffect(() => {
    if (data?.disponibilidad) {
      setForm({
        numeroDocumentoMedico: data.disponibilidad.medicoNumeroDocumento,
        diaSemana: data.disponibilidad.diaSemana,
        horaInicio: normalizeTimeInput(data.disponibilidad.horaInicio),
        horaFin: normalizeTimeInput(data.disponibilidad.horaFin)
      });
    }
  }, [data]);

  const updateField = (field: keyof DisponibilidadMedicaRequest, value: string) => {
    setForm((current) => ({ ...current, [field]: value }));
  };

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setError(null);
    setSaving(true);

    try {
      if (!form.numeroDocumentoMedico) throw new Error('El medico es obligatorio.');
      if (medicoSeleccionadoInactivo) throw new Error('El medico asociado esta inactivo. Selecciona un medico activo.');
      if (!form.diaSemana) throw new Error('El dia de la semana es obligatorio.');
      if (!form.horaInicio) throw new Error('La hora de inicio es obligatoria.');
      if (!form.horaFin) throw new Error('La hora de fin es obligatoria.');
      if (form.horaInicio >= form.horaFin) throw new Error('La hora de inicio debe ser menor que la hora de fin.');

      const payload: DisponibilidadMedicaRequest = {
        ...form,
        horaInicio: toBackendTime(form.horaInicio),
        horaFin: toBackendTime(form.horaFin)
      };

      if (isEditing && id) {
        await disponibilidadMedicaApi.actualizarDisponibilidad(Number(id), payload);
      } else {
        await disponibilidadMedicaApi.crearDisponibilidad(payload);
      }

      navigate('/disponibilidad-medica');
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setSaving(false);
    }
  };

  return (
    <>
      <PageHeader
        title={isEditing ? 'Editar disponibilidad' : 'Nueva disponibilidad'}
        subtitle="Define el dia y horario semanal en que un medico puede atender citas."
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
              {medicoSeleccionadoInactivo && data?.disponibilidad && (
                <option value={data.disponibilidad.medicoNumeroDocumento} disabled>
                  {data.disponibilidad.medicoNombreCompleto ?? data.disponibilidad.medicoNumeroDocumento} - {data.disponibilidad.medicoNumeroDocumento} (inactivo)
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
            Dia de semana
            <select value={form.diaSemana} onChange={(event) => updateField('diaSemana', event.target.value)} required>
              {diasSemana.map((dia) => <option key={dia} value={dia}>{formatDia(dia)}</option>)}
            </select>
          </label>
          <label>
            Hora inicio
            <input type="time" value={form.horaInicio} onChange={(event) => updateField('horaInicio', event.target.value)} required />
          </label>
          <label>
            Hora fin
            <input type="time" value={form.horaFin} onChange={(event) => updateField('horaFin', event.target.value)} required />
          </label>
          <div className="form-actions">
            <button type="button" className="secondary-button" onClick={() => navigate('/disponibilidad-medica')}>Cancelar</button>
            <button type="submit" className="primary-button" disabled={saving || medicoSeleccionadoInactivo}>{saving ? 'Guardando...' : 'Guardar'}</button>
          </div>
        </form>
      )}
    </>
  );
}

function normalizeTimeInput(value: string) {
  return value?.slice(0, 5) ?? '';
}

function toBackendTime(value: string) {
  return value.length === 5 ? `${value}:00` : value;
}

function formatDia(value?: string | null) {
  if (!value) return 'Sin dia';
  return value.charAt(0) + value.slice(1).toLowerCase();
}
