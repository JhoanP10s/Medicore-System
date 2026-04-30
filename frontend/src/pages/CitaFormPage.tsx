import { FormEvent, useCallback, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { citasApi, medicosApi, pacientesApi } from '../api/clinicApi';
import { getErrorMessage } from '../api/http';
import { Alert } from '../components/Alert';
import { LoadingState } from '../components/LoadingState';
import { PageHeader } from '../components/PageHeader';
import { useAsync } from '../hooks/useAsync';
import type { CitaRequest } from '../types/domain';
import { fullName, toDateTimeLocal } from '../utils/format';

const initialForm: CitaRequest = {
  numeroDocumentoPaciente: '',
  numeroDocumentoMedico: '',
  fechaHora: '',
  motivo: '',
  observaciones: ''
};

export function CitaFormPage() {
  const { id } = useParams();
  const isEditing = Boolean(id);
  const navigate = useNavigate();
  const [form, setForm] = useState<CitaRequest>(initialForm);
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const loader = useCallback(async () => {
    const [pacientes, medicos, cita] = await Promise.all([
      pacientesApi.list(),
      medicosApi.list(),
      isEditing ? citasApi.get(Number(id)) : Promise.resolve(null)
    ]);
    return { pacientes, medicos, cita };
  }, [id, isEditing]);

  const { data, loading, error: loadError } = useAsync(loader);

  useEffect(() => {
    if (data?.cita) {
      setForm({
        numeroDocumentoPaciente: data.cita.pacienteNumeroDocumento,
        numeroDocumentoMedico: data.cita.medicoNumeroDocumento,
        fechaHora: toDateTimeLocal(data.cita.fechaHora),
        motivo: data.cita.motivo,
        observaciones: data.cita.observaciones ?? ''
      });
    }
  }, [data]);

  const updateField = (field: keyof CitaRequest, value: string) => {
    setForm((current) => ({ ...current, [field]: value }));
  };

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setSaving(true);
    setError(null);
    try {
      const payload = { ...form, fechaHora: `${form.fechaHora}:00` };
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
      <PageHeader title={isEditing ? 'Editar cita' : 'Nueva cita'} subtitle="Programa una consulta entre paciente y medico." />
      <Alert type="error" message={error ?? loadError} />
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
          <label>
            Medico
            <select value={form.numeroDocumentoMedico} onChange={(event) => updateField('numeroDocumentoMedico', event.target.value)} required>
              <option value="">Seleccionar</option>
              {data?.medicos.map((medico) => (
                <option key={medico.numeroDocumento} value={medico.numeroDocumento}>{fullName(medico)} - {medico.especialidadNombre}</option>
              ))}
            </select>
          </label>
          <label>
            Fecha y hora
            <input type="datetime-local" value={form.fechaHora} onChange={(event) => updateField('fechaHora', event.target.value)} required />
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
            <button type="submit" className="primary-button" disabled={saving}>{saving ? 'Guardando...' : 'Guardar'}</button>
          </div>
        </form>
      )}
    </>
  );
}
