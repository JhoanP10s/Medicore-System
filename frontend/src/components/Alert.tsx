interface Props {
  type?: 'success' | 'error' | 'info';
  message?: string | null;
}

export function Alert({ type = 'info', message }: Props) {
  if (!message) return null;
  return <div className={`alert alert-${type}`}>{message}</div>;
}
