export type Role = 'ADMIN' | 'DOCTOR' | 'USER';

export type EstadoCita = 'PROGRAMADA' | 'CONFIRMADA' | 'CANCELADA' | 'COMPLETADA';

export type DiaSemana = 'LUNES' | 'MARTES' | 'MIERCOLES' | 'JUEVES' | 'VIERNES' | 'SABADO' | 'DOMINGO';

export interface AuthResponse {
  id: number;
  nombre: string;
  email: string;
  rol: Role;
  medicoId?: string | null;
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
  estado: EstadoCita;
  duracionMinutos: number;
  pacienteNumeroDocumento: string;
  pacienteNombreCompleto: string;
  pacienteActivo?: boolean;
  medicoNumeroDocumento: string;
  medicoNombreCompleto: string;
  medicoActivo?: boolean;
  especialidadNombre?: string;
  historiaClinicaId?: number | null;
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
  numeroDocumentoMedico?: string | null;
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
  duracionMinutos: number;
}

export interface CambiarEstadoCitaRequest {
  estado: EstadoCita;
}

export interface HorarioDisponibleResponse {
  inicio: string;
  fin: string;
}

export interface HistoriaClinicaRequest {
  citaId: number;
  sintomas: string;
  diagnostico: string;
  tratamiento: string;
  observaciones?: string;
}

export interface HistoriaClinicaResponse {
  id: number;
  sintomas: string;
  diagnostico: string;
  tratamiento: string;
  observaciones?: string;
  fechaRegistro: string;
  pacienteNumeroDocumento: string;
  pacienteNombreCompleto: string;
  medicoNumeroDocumento: string;
  medicoNombreCompleto: string;
  citaId: number;
  citaFechaHora: string;
  citaEstado: EstadoCita;
  createdAt?: string;
  updatedAt?: string;
}

export interface DisponibilidadMedicaRequest {
  numeroDocumentoMedico: string;
  diaSemana: DiaSemana;
  horaInicio: string;
  horaFin: string;
}

export interface DisponibilidadMedicaResponse {
  id: number;
  medicoNumeroDocumento: string;
  medicoNombreCompleto?: string;
  diaSemana: DiaSemana;
  horaInicio: string;
  horaFin: string;
  activo: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface BloqueoAgendaRequest {
  numeroDocumentoMedico: string;
  fechaInicio: string;
  fechaFin: string;
  motivo: string;
}

export interface BloqueoAgendaResponse {
  id: number;
  medicoNumeroDocumento: string;
  medicoNombreCompleto?: string;
  fechaInicio: string;
  fechaFin: string;
  motivo: string;
  activo: boolean;
  createdAt?: string;
  updatedAt?: string;
}


export type DashboardTipo = 'ADMIN' | 'DOCTOR' | 'USER';

export interface DashboardResponse {
  tipo: DashboardTipo;
  admin?: AdminDashboardResponse | null;
  doctor?: DoctorDashboardResponse | null;
  user?: UserDashboardResponse | null;
}

export interface AdminDashboardResponse {
  totalPacientesActivos: number;
  totalMedicosActivos: number;
  totalEspecialidadesActivas: number;
  citasHoy: number;
  historiasClinicasRegistradas: number;
  citasPorEstado: EstadoCitaCountResponse[];
  proximasCitas: CitaResumenResponse[];
  alertas: AlertaDashboardResponse[];
}

export interface DoctorDashboardResponse {
  citasHoy: number;
  proximasCitas: CitaResumenResponse[];
  citasPorEstado: EstadoCitaCountResponse[];
  citasPendientesHistoria: number;
  historiasRecientes: HistoriaClinicaResumenResponse[];
  alertas: AlertaDashboardResponse[];
}

export interface UserDashboardResponse {
  mensaje: string;
  accesosDisponibles: string[];
}

export interface CitaResumenResponse {
  id: number;
  fechaHora: string;
  estado: EstadoCita;
  duracionMinutos: number;
  motivo: string;
  pacienteNumeroDocumento: string;
  pacienteNombreCompleto: string;
  medicoNumeroDocumento: string;
  medicoNombreCompleto: string;
}

export interface EstadoCitaCountResponse {
  estado: EstadoCita;
  total: number;
}

export interface AlertaDashboardResponse {
  tipo: string;
  mensaje: string;
  severidad: string;
}

export interface HistoriaClinicaResumenResponse {
  id: number;
  fechaRegistro: string;
  pacienteNumeroDocumento: string;
  pacienteNombreCompleto: string;
  diagnostico: string;
  citaId: number;
}
