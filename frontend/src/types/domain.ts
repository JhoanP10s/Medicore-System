export type Role = 'ADMIN' | 'DOCTOR' | 'USER';

export interface AuthResponse {
  id: number;
  nombre: string;
  email: string;
  rol: Role;
  token: string;
  tokenType: string;
}

export interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
}

export interface MessageResponse {
  message: string;
}

export interface Paciente {
  numeroDocumento: string;
  primerNombre: string;
  segundoNombre?: string;
  primerApellido: string;
  segundoApellido?: string;
  tipoDocumento: string;
  fechaExpedicionDoc?: string;
  email?: string;
  telefono?: string;
  activo?: boolean;
}

export interface Medico extends Paciente {
  especialidadId: number;
  especialidadNombre?: string;
}

export interface Especialidad {
  id: number;
  nombre: string;
  descripcion?: string;
  activo?: boolean;
}

export interface Cita {
  id: number;
  fechaHora: string;
  motivo: string;
  observaciones?: string;
  pacienteNumeroDocumento: string;
  pacienteNombreCompleto: string;
  medicoNumeroDocumento: string;
  medicoNombreCompleto: string;
  especialidadNombre?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  nombre: string;
  email: string;
  password: string;
  rol: Role;
}

export type PacienteRequest = Paciente;

export interface MedicoRequest extends Paciente {
  especialidadId: number;
}

export interface EspecialidadRequest {
  nombre: string;
  descripcion?: string;
  activo?: boolean;
}

export interface CitaRequest {
  numeroDocumentoPaciente: string;
  numeroDocumentoMedico: string;
  fechaHora: string;
  motivo: string;
  observaciones?: string;
}
