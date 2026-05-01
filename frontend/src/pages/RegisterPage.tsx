import { Activity } from 'lucide-react';
import { FormEvent, useMemo, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { register } from '../api/authApi';
import { getErrorMessage } from '../api/http';
import { Alert } from '../components/Alert';
import type { Role } from '../types/domain';

const passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/;
const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

interface FormState {
  nombre: string;
  email: string;
  password: string;
  confirmPassword: string;
  rol: Role;
}

export function RegisterPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState<FormState>({
    nombre: '',
    email: '',
    password: '',
    confirmPassword: '',
    rol: 'USER'
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const validationError = useMemo(() => validate(form), [form]);

  const updateField = (field: keyof FormState, value: string) => {
    setForm((current) => ({ ...current, [field]: value }));
  };

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setError(null);
    setSuccess(null);

    const message = validate(form);
    if (message) {
      setError(message);
      return;
    }

    setLoading(true);

    try {
      await register({
        nombre: form.nombre.trim(),
        email: form.email.trim().toLowerCase(),
        password: form.password,
        rol: form.rol
      });

      setSuccess('Usuario registrado correctamente. Ahora puedes iniciar sesion.');
      setTimeout(() => {
        navigate('/login', {
          replace: true,
          state: {
            email: form.email.trim().toLowerCase(),
            message: 'Usuario registrado correctamente. Ahora puedes iniciar sesion.'
          }
        });
      }, 900);
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="login-page">
      <section className="login-panel register-panel">
        <div className="brand login-brand">
          <Activity size={30} />
          <div>
            <strong>Medicore System</strong>
            <span>Registro academico de usuarios</span>
          </div>
        </div>
        <h1>Crear cuenta</h1>
        <p>Registra un usuario para acceder al panel de gestion clinica.</p>
        <Alert type="success" message={success} />
        <Alert type="error" message={error} />
        <form onSubmit={handleSubmit} className="form">
          <label>
            Nombre
            <input
              value={form.nombre}
              onChange={(event) => updateField('nombre', event.target.value)}
              required
              maxLength={100}
            />
          </label>
          <label>
            Email
            <input
              type="email"
              value={form.email}
              onChange={(event) => updateField('email', event.target.value)}
              required
            />
          </label>
          <label>
            Contrasena
            <input
              type="password"
              value={form.password}
              onChange={(event) => updateField('password', event.target.value)}
              required
              minLength={8}
            />
          </label>
          <label>
            Confirmar contrasena
            <input
              type="password"
              value={form.confirmPassword}
              onChange={(event) => updateField('confirmPassword', event.target.value)}
              required
              minLength={8}
            />
          </label>
          <label>
            Rol
            <select value={form.rol} onChange={(event) => updateField('rol', event.target.value)}>
              <option value="USER">USER</option>
              <option value="DOCTOR">DOCTOR</option>
              <option value="ADMIN">ADMIN</option>
            </select>
            <span className="field-hint">Selector habilitado solo para entorno academico/desarrollo.</span>
          </label>
          {validationError ? <span className="field-error">{validationError}</span> : null}
          <button className="primary-button" type="submit" disabled={loading}>
            {loading ? 'Registrando...' : 'Crear cuenta'}
          </button>
        </form>
        <p className="auth-switch">
          Ya tienes cuenta? <Link to="/login">Inicia sesion</Link>
        </p>
      </section>
    </main>
  );
}

function validate(form: FormState) {
  if (!form.nombre.trim()) return 'El nombre es obligatorio.';
  if (!form.email.trim()) return 'El email es obligatorio.';
  if (!emailPattern.test(form.email)) return 'Ingresa un email valido.';
  if (!form.password) return 'La contrasena es obligatoria.';
  if (!passwordPattern.test(form.password)) {
    return 'La contrasena debe tener minimo 8 caracteres, una mayuscula, una minuscula y un numero.';
  }
  if (form.password !== form.confirmPassword) return 'Las contrasenas no coinciden.';
  if (!form.rol) return 'El rol es obligatorio.';
  return null;
}
