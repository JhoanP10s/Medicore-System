import { ShieldAlert } from 'lucide-react';
import { Link } from 'react-router-dom';
import { PageHeader } from '../components/PageHeader';

export function AccessDeniedPage() {
  return (
    <section className="detail-panel access-denied-panel">
      <div className="metric-icon">
        <ShieldAlert size={24} />
      </div>
      <PageHeader
        title="Acceso denegado"
        subtitle="No tienes permisos para acceder a esta seccion."
        actions={<Link className="primary-button" to="/">Volver al dashboard</Link>}
      />
    </section>
  );
}
