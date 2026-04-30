import { FormEvent, useCallback, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { pacientesApi } from '../api/clinicApi';
import { getErrorMessage } from '../api/http';
import { Alert } from '../components/Alert';
import { LoadingState } from '../components/LoadingState';
import { PageHeader } from '../components/PageHeader';
import { useAsync } from '../hooks/useAsync';
import type { PacienteRequest } from '../types/domain';

const initialForm: PacienteRequest = {
  numeroDocumento: '',
  primerNombre: '',
  segundoNombre: '',
  primerApellido: '',
  segundoApellido: '',
  tipoDocumento: 'CC',
  fechaExpedicionDoc: '',
  email: '',
  telefono: '',
  activo: true
};

export function PacienteFormPage() {
  const { documento } = useParams();
  const isEditing = Boolean(documento);
  const navigate = useNavigate();
  const [form, setForm] = useState<PacienteRequest>(initialForm);
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const loader = useCallback(() => pacientesApi.get(documento ?? ''), [documento]);
  const { data, loading, error: loadError } = useAsync(loader, isEditing);

  useEffect(() => {
    if (data) setForm({ ...initialForm, ...data });
  }, [data]);

  const updateField = (field: keyof PacienteRequest, value: string | boolean) => {
    setForm((current) => ({ ...current, [field]: value }));
  };

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setSaving(true);
    setError(null);

    try {
      if (isEditing && documento) {
        await pacientesApi.update(documento, form);
      } else {
        await pacientesApi.create(form);
      }
      navigate('/pacientes', { state: { message: 'Paciente guardado correctamente' } });
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setSaving(false);
    }
  };

  return (
    <>
      <PageHeader title={isEditing ? 'Editar paciente' : 'Nuevo paciente'} subtitle="Datos personales, contacto y estado operativo." />
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
          <label className="checkbox-field">
            <input type="checkbox" checked={Boolean(form.activo)} onChange={(event) => updateField('activo', event.target.checked)} />
            Activo
          </label>
          <div className="form-actions">
            <button type="button" className="secondary-button" onClick={() => navigate('/pacientes')}>Cancelar</button>
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
