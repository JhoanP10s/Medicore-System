import { FormEvent, useCallback, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { citasApi, historiaClinicaApi } from '../api/clinicApi';
import { getErrorMessage } from '../api/http';
import { Alert } from '../components/Alert';
import { LoadingState } from '../components/LoadingState';
import { PageHeader } from '../components/PageHeader';
import { useAsync } from '../hooks/useAsync';
import type { HistoriaClinicaRequest } from '../types/domain';
import { formatDateTime } from '../utils/format';

const initialForm: HistoriaClinicaRequest = {
  citaId: 0,
  sintomas: '',
  diagnostico: '',
  tratamiento: '',
  observaciones: ''
};

export function HistoriaClinicaFormPage() {
  const { id, citaId } = useParams();
  const isEditing = Boolean(id);
  const navigate = useNavigate();
  const [form, setForm] = useState<HistoriaClinicaRequest>({
    ...initialForm,
    citaId: citaId ? Number(citaId) : 0
  });
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const loader = useCallback(async () => {
    const [historia, cita] = await Promise.all([
      isEditing ? historiaClinicaApi.obtenerHistoriaClinica(Number(id)) : Promise.resolve(null),
      citaId ? citasApi.get(Number(citaId)) : Promise.resolve(null)
    ]);
    return { historia, cita };
  }, [citaId, id, isEditing]);

  const { data, loading, error: loadError } = useAsync(loader);

  useEffect(() => {
    if (data?.historia) {
      setForm({
        citaId: data.historia.citaId,
        sintomas: data.historia.sintomas,
        diagnostico: data.historia.diagnostico,
        tratamiento: data.historia.tratamiento,
        observaciones: data.historia.observaciones ?? ''
      });
    }
  }, [data]);

  const updateField = (field: keyof HistoriaClinicaRequest, value: string | number) => {
    setForm((current) => ({ ...current, [field]: value }));
  };

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setSaving(true);
    setError(null);

    try {
      if (!form.citaId) throw new Error('El id de la cita es obligatorio.');
      if (!form.sintomas.trim()) throw new Error('Los sintomas son obligatorios.');
      if (!form.diagnostico.trim()) throw new Error('El diagnostico es obligatorio.');
      if (!form.tratamiento.trim()) throw new Error('El tratamiento es obligatorio.');

      const payload = {
        ...form,
        sintomas: form.sintomas.trim(),
        diagnostico: form.diagnostico.trim(),
        tratamiento: form.tratamiento.trim(),
        observaciones: form.observaciones?.trim() || undefined
      };

      const response = isEditing && id
        ? await historiaClinicaApi.actualizarHistoriaClinica(Number(id), payload)
        : await historiaClinicaApi.crearHistoriaClinica(payload);

      navigate(`/historias-clinicas/${response.id}`);
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setSaving(false);
    }
  };

  return (
    <>
      <PageHeader
        title={isEditing ? 'Editar historia clinica' : 'Nueva historia clinica'}
        subtitle="Registra sintomas, diagnostico y tratamiento derivados de una cita confirmada o completada."
      />
      <Alert type="error" message={error ?? loadError} />
      {!isEditing && !citaId && (
        <Alert
          type="info"
          message="Ingresa el ID de una cita confirmada o completada. Tambien puedes crear la historia clinica desde el listado de citas."
        />
      )}
      {data?.cita && (
        <Alert
          type="info"
          message={`Cita seleccionada: ${data.cita.pacienteNombreCompleto} con ${data.cita.medicoNombreCompleto} - ${formatDateTime(data.cita.fechaHora)} (${data.cita.estado})`}
        />
      )}
      {loading ? <LoadingState /> : (
        <form className="panel form-grid" onSubmit={handleSubmit}>
          <label>
            Cita asociada
            <input
              type="number"
              min={1}
              value={form.citaId || ''}
              onChange={(event) => updateField('citaId', Number(event.target.value))}
              disabled={Boolean(citaId) || isEditing}
              required
            />
          </label>
          <label className="span-2">
            Sintomas
            <textarea value={form.sintomas} onChange={(event) => updateField('sintomas', event.target.value)} rows={4} required maxLength={1000} />
          </label>
          <label className="span-2">
            Diagnostico
            <textarea value={form.diagnostico} onChange={(event) => updateField('diagnostico', event.target.value)} rows={4} required maxLength={1000} />
          </label>
          <label className="span-2">
            Tratamiento
            <textarea value={form.tratamiento} onChange={(event) => updateField('tratamiento', event.target.value)} rows={4} required maxLength={1000} />
          </label>
          <label className="span-2">
            Observaciones
            <textarea value={form.observaciones ?? ''} onChange={(event) => updateField('observaciones', event.target.value)} rows={3} maxLength={1000} />
          </label>
          <div className="form-actions">
            <button type="button" className="secondary-button" onClick={() => navigate('/historias-clinicas')}>Cancelar</button>
            <button type="submit" className="primary-button" disabled={saving}>{saving ? 'Guardando...' : 'Guardar'}</button>
          </div>
        </form>
      )}
    </>
  );
}
