import { CalendarDays, LogOut, Stethoscope, Users, Activity, LayoutDashboard, BadgePlus } from 'lucide-react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

const navItems = [
  { to: '/', label: 'Dashboard', icon: LayoutDashboard },
  { to: '/pacientes', label: 'Pacientes', icon: Users },
  { to: '/medicos', label: 'Medicos', icon: Stethoscope },
  { to: '/citas', label: 'Citas', icon: CalendarDays },
  { to: '/especialidades', label: 'Especialidades', icon: BadgePlus }
];

export function AppLayout() {
  const { auth, logout } = useAuth();
  const navigate = useNavigate();

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
          {navItems.map((item) => {
            const Icon = item.icon;
            return (
              <NavLink key={item.to} to={item.to} end={item.to === '/'}>
                <Icon size={18} />
                <span>{item.label}</span>
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
