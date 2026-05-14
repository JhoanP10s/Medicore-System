import { Component, type ErrorInfo, type ReactNode } from 'react';
import { Link, useLocation } from 'react-router-dom';

interface Props {
  children: ReactNode;
  resetKey: string;
}

interface State {
  hasError: boolean;
}

class RouteErrorBoundary extends Component<Props, State> {
  state: State = { hasError: false };

  static getDerivedStateFromError(): State {
    return { hasError: true };
  }

  componentDidUpdate(previousProps: Props) {
    if (this.state.hasError && previousProps.resetKey !== this.props.resetKey) {
      this.setState({ hasError: false });
    }
  }

  componentDidCatch(error: Error, info: ErrorInfo) {
    if (import.meta.env.DEV) {
      console.error('ErrorBoundary captured an error', error, info);
    }
  }

  render() {
    if (this.state.hasError) {
      return (
        <main className="main">
          <section className="content">
            <div className="panel empty-state">
              <h1>Ocurrio un error al cargar esta seccion.</h1>
              <p>Recarga la pagina o vuelve al dashboard para continuar.</p>
              <div className="form-actions">
                <button className="secondary-button" type="button" onClick={() => window.location.reload()}>
                  Recargar
                </button>
                <Link className="primary-button" to="/">
                  Volver al dashboard
                </Link>
              </div>
            </div>
          </section>
        </main>
      );
    }

    return this.props.children;
  }
}

export function ErrorBoundary({ children }: { children: ReactNode }) {
  const location = useLocation();
  return <RouteErrorBoundary resetKey={location.pathname} key={location.pathname}>
    {children}
  </RouteErrorBoundary>;
}
