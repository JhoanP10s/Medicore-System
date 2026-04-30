import type { Paciente } from '../types/domain';

export function fullName(person: Pick<Paciente, 'primerNombre' | 'segundoNombre' | 'primerApellido' | 'segundoApellido'>) {
  return [person.primerNombre, person.segundoNombre, person.primerApellido, person.segundoApellido]
    .filter(Boolean)
    .join(' ');
}

export function formatDateTime(value?: string) {
  if (!value) return 'Sin fecha';
  return new Intl.DateTimeFormat('es-CO', {
    dateStyle: 'medium',
    timeStyle: 'short'
  }).format(new Date(value));
}

export function toDateTimeLocal(value?: string) {
  if (!value) return '';
  return value.slice(0, 16);
}
