import { Activity } from 'lucide-react';
import { FormEvent, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getErrorMessage } from '../api/http';
import { Alert } from '../components/Alert';
import { useAuth } from '../hooks/useAuth';

export function LoginPage() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setLoading(true);
    setError(null);

    try {
      await login({ email, password });
      navigate('/', { replace: true });
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="login-page">
      <section className="login-panel">
        <div className="brand login-brand">
          <Activity size={30} />
          <div>
            <strong>Medicore System</strong>
            <span>Backend medico conectado</span>
          </div>
        </div>
        <h1>Iniciar sesion</h1>
        <p>Accede al panel para gestionar pacientes, medicos y citas.</p>
        <Alert type="error" message={error} />
        <form onSubmit={handleSubmit} className="form">
          <label>
            Email
            <input type="email" value={email} onChange={(event) => setEmail(event.target.value)} required />
          </label>
          <label>
            Contrasena
            <input type="password" value={password} onChange={(event) => setPassword(event.target.value)} required />
          </label>
          <button className="primary-button" type="submit" disabled={loading}>
            {loading ? 'Validando...' : 'Entrar'}
          </button>
        </form>
      </section>
    </main>
  );
}
