import { CalendarDays, LogOut, Stethoscope, Users, Activity, LayoutDashboard, BadgePlus, Clock, FileText, CalendarCheck, CalendarX } from 'lucide-react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import type { Role } from '../types/domain';

const navItems: Array<{ to: string; label: string; doctorLabel?: string; icon: typeof LayoutDashboard; roles: Role[] }> = [
  { to: '/', label: 'Dashboard', icon: LayoutDashboard, roles: ['ADMIN', 'DOCTOR', 'USER'] },
  { to: '/pacientes', label: 'Pacientes', icon: Users, roles: ['ADMIN', 'USER'] },
  { to: '/medicos', label: 'Medicos', icon: Stethoscope, roles: ['ADMIN'] },
  { to: '/citas', label: 'Citas', icon: CalendarDays, roles: ['ADMIN', 'DOCTOR'] },
  { to: '/agenda', label: 'Agenda', icon: Clock, roles: ['ADMIN', 'DOCTOR'] },
  { to: '/disponibilidad-medica', label: 'Disponibilidad Medica', doctorLabel: 'Mi Disponibilidad', icon: CalendarCheck, roles: ['ADMIN', 'DOCTOR'] },
  { to: '/bloqueo-agenda', label: 'Bloqueos de Agenda', doctorLabel: 'Mis Bloqueos', icon: CalendarX, roles: ['ADMIN', 'DOCTOR'] },
  { to: '/historias-clinicas', label: 'Historias Clinicas', icon: FileText, roles: ['ADMIN', 'DOCTOR'] },
  { to: '/especialidades', label: 'Especialidades', icon: BadgePlus, roles: ['ADMIN'] }
];

export function AppLayout() {
  const { auth, logout } = useAuth();
  const navigate = useNavigate();
  const visibleItems = navItems.filter((item) => auth?.rol && item.roles.includes(auth.rol));

  const handleLogout = () => {
    logout();
    navigate('/login', { replace: true });
  };

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <Activity size={26} />
          <div>
            <strong>Medicore System</strong>
            <span>Gestion medica</span>
          </div>
        </div>
        <nav>
          {visibleItems.map((item) => {
            const Icon = item.icon;
            return (
              <NavLink key={item.to} to={item.to} end={item.to === '/'}>
                <Icon size={18} />
                <span>{auth?.rol === 'DOCTOR' && item.doctorLabel ? item.doctorLabel : item.label}</span>
              </NavLink>
            );
          })}
        </nav>
      </aside>

      <main className="main">
        <header className="topbar">
          <div>
            <span className="eyebrow">Sesion activa</span>
            <strong>{auth?.email}</strong>
          </div>
          <div className="topbar-actions">
            <span className="role-pill">{auth?.rol}</span>
            <button className="icon-button" type="button" onClick={handleLogout} title="Cerrar sesion">
              <LogOut size={18} />
            </button>
          </div>
        </header>
        <section className="content">
          <Outlet />
        </section>
      </main>
    </div>
  );
}
