import { FormEvent, useCallback, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { especialidadesApi, medicosApi } from '../api/clinicApi';
import { getErrorMessage } from '../api/http';
import { Alert } from '../components/Alert';
import { LoadingState } from '../components/LoadingState';
import { PageHeader } from '../components/PageHeader';
import { useAsync } from '../hooks/useAsync';
import type { MedicoRequest } from '../types/domain';

const initialForm: MedicoRequest = {
  numeroDocumento: '',
  primerNombre: '',
  segundoNombre: '',
  primerApellido: '',
  segundoApellido: '',
  tipoDocumento: 'CC',
  fechaExpedicionDoc: '',
  email: '',
  telefono: '',
  activo: true,
  especialidadId: 0
};

export function MedicoFormPage() {
  const { documento } = useParams();
  const isEditing = Boolean(documento);
  const navigate = useNavigate();
  const [form, setForm] = useState<MedicoRequest>(initialForm);
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const loader = useCallback(async () => {
    const [especialidades, medico] = await Promise.all([
      especialidadesApi.list(),
      isEditing ? medicosApi.get(documento ?? '') : Promise.resolve(null)
    ]);
    return { especialidades, medico };
  }, [documento, isEditing]);

  const { data, loading, error: loadError } = useAsync(loader);

  useEffect(() => {
    if (data?.medico) setForm({ ...initialForm, ...data.medico });
  }, [data]);

  const updateField = (field: keyof MedicoRequest, value: string | boolean | number) => {
    setForm((current) => ({ ...current, [field]: value }));
  };

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setSaving(true);
    setError(null);
    try {
      if (isEditing && documento) {
        await medicosApi.update(documento, form);
      } else {
        await medicosApi.create(form);
      }
      navigate('/medicos');
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setSaving(false);
    }
  };

  return (
    <>
      <PageHeader title={isEditing ? 'Editar medico' : 'Nuevo medico'} subtitle="Datos profesionales, contacto y especialidad." />
      <Alert type="error" message={error ?? loadError} />
      {loading ? <LoadingState /> : (
        <form className="panel form-grid" onSubmit={handleSubmit}>
          <Input label="Documento" value={form.numeroDocumento} onChange={(v) => updateField('numeroDocumento', v)} disabled={isEditing} required />
          <Input label="Tipo documento" value={form.tipoDocumento} onChange={(v) => updateField('tipoDocumento', v)} required />
          <Input label="Primer nombre" value={form.primerNombre} onChange={(v) => updateField('primerNombre', v)} required />
          <Input label="Segundo nombre" value={form.segundoNombre ?? ''} onChange={(v) => updateField('segundoNombre', v)} />
          <Input label="Primer apellido" value={form.primerApellido} onChange={(v) => updateField('primerApellido', v)} required />
          <Input label="Segundo apellido" value={form.segundoApellido ?? ''} onChange={(v) => updateField('segundoApellido', v)} />
          <Input label="Fecha expedicion" type="date" value={form.fechaExpedicionDoc ?? ''} onChange={(v) => updateField('fechaExpedicionDoc', v)} />
          <Input label="Email" type="email" value={form.email ?? ''} onChange={(v) => updateField('email', v)} />
          <Input label="Telefono" value={form.telefono ?? ''} onChange={(v) => updateField('telefono', v)} />
          <label>
            Especialidad
            <select value={form.especialidadId} onChange={(event) => updateField('especialidadId', Number(event.target.value))} required>
              <option value={0}>Seleccionar</option>
              {data?.especialidades.map((especialidad) => (
                <option key={especialidad.id} value={especialidad.id}>{especialidad.nombre}</option>
              ))}
            </select>
          </label>
          <label className="checkbox-field">
            <input type="checkbox" checked={Boolean(form.activo)} onChange={(event) => updateField('activo', event.target.checked)} />
            Activo
          </label>
          <div className="form-actions">
            <button type="button" className="secondary-button" onClick={() => navigate('/medicos')}>Cancelar</button>
            <button type="submit" className="primary-button" disabled={saving}>{saving ? 'Guardando...' : 'Guardar'}</button>
          </div>
        </form>
      )}
    </>
  );
}

function Input({ label, value, onChange, type = 'text', required, disabled }: {
  label: string;
  value: string;
  onChange: (value: string) => void;
  type?: string;
  required?: boolean;
  disabled?: boolean;
}) {
  return (
    <label>
      {label}
      <input type={type} value={value} onChange={(event) => onChange(event.target.value)} required={required} disabled={disabled} />
    </label>
  );
}
