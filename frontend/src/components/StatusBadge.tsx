interface Props {
  active?: boolean;
}

export function StatusBadge({ active }: Props) {
  return <span className={`badge ${active ? 'badge-success' : 'badge-muted'}`}>{active ? 'Activo' : 'Inactivo'}</span>;
}
