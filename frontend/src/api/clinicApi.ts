import { http } from './http';
import type {
  BloqueoAgendaRequest,
  BloqueoAgendaResponse,
  Cita,
  CitaRequest,
  DashboardResponse,
  DisponibilidadMedicaRequest,
  DisponibilidadMedicaResponse,
  Especialidad,
  EspecialidadRequest,
  EstadoCita,
  HistoriaClinicaRequest,
  HistoriaClinicaResponse,
  HorarioDisponibleResponse,
  Medico,
  MedicoRequest,
  MessageResponse,
  Paciente,
  PacienteRequest
} from '../types/domain';


export const dashboardApi = {
  obtenerResumen: async () => (await http.get<DashboardResponse>('/dashboard/resumen')).data
};

export const pacientesApi = {
  list: async () => (await http.get<Paciente[]>('/paciente')).data,
  get: async (documento: string) => (await http.get<Paciente>(`/paciente/${documento}`)).data,
  create: async (payload: PacienteRequest) => (await http.post<MessageResponse>('/paciente/crearPaciente', payload)).data,
  update: async (documento: string, payload: PacienteRequest) => (await http.put<MessageResponse>(`/paciente/${documento}`, payload)).data,
  remove: async (documento: string) => (await http.delete<MessageResponse>(`/paciente/eliminarPaciente/${documento}`)).data
};

export const medicosApi = {
  list: async () => (await http.get<Medico[]>('/medico')).data,
  get: async (documento: string) => (await http.get<Medico>(`/medico/${documento}`)).data,
  create: async (payload: MedicoRequest) => (await http.post<MessageResponse>('/medico/crearMedico', payload)).data,
  update: async (documento: string, payload: MedicoRequest) => (await http.put<MessageResponse>(`/medico/${documento}`, payload)).data,
  remove: async (documento: string) => (await http.delete<MessageResponse>(`/medico/eliminarMedico/${documento}`)).data
};

export const especialidadesApi = {
  list: async () => (await http.get<Especialidad[]>('/especialidad')).data,
  get: async (id: number) => (await http.get<Especialidad>(`/especialidad/${id}`)).data,
  create: async (payload: EspecialidadRequest) => (await http.post<Especialidad>('/especialidad', payload)).data,
  update: async (id: number, payload: EspecialidadRequest) => (await http.put<Especialidad>(`/especialidad/${id}`, payload)).data,
  remove: async (id: number) => (await http.delete<MessageResponse>(`/especialidad/${id}`)).data
};

export const citasApi = {
  list: async () => (await http.get<Cita[]>('/cita')).data,
  get: async (id: number) => (await http.get<Cita>(`/cita/${id}`)).data,
  create: async (payload: CitaRequest) => (await http.post<Cita>('/cita', payload)).data,
  update: async (id: number, payload: CitaRequest) => (await http.put<Cita>(`/cita/${id}`, payload)).data,
  cambiarEstado: async (id: number, estado: EstadoCita) => (await http.patch<Cita>(`/cita/${id}/estado`, { estado })).data,
  cancelar: async (id: number) => (await http.patch<MessageResponse>(`/cita/${id}/cancelar`)).data,
  remove: async (id: number) => (await http.patch<MessageResponse>(`/cita/${id}/cancelar`)).data,
  listarPorMedico: async (medicoId: string) => (await http.get<Cita[]>(`/cita/medico/${medicoId}`)).data,
  listarPorPaciente: async (pacienteId: string) => (await http.get<Cita[]>(`/cita/paciente/${pacienteId}`)).data,
  listarPorEstado: async (estado: EstadoCita) => (await http.get<Cita[]>(`/cita/estado/${estado}`)).data,
  listarPorRango: async (inicio: string, fin: string) => (await http.get<Cita[]>('/cita/rango', { params: { inicio, fin } })).data
};

export const agendaApi = {
  obtenerHorariosDisponibles: async (medicoId: string, fecha: string, duracionMinutos: number) => (
    await http.get<HorarioDisponibleResponse[]>(`/agenda/medico/${medicoId}/disponibles`, {
      params: { fecha, duracionMinutos }
    })
  ).data
};

export const historiaClinicaApi = {
  list: async () => (await http.get<HistoriaClinicaResponse[]>('/historia-clinica')).data,
  listarHistoriasClinicas: async () => (await http.get<HistoriaClinicaResponse[]>('/historia-clinica')).data,
  get: async (id: number) => (await http.get<HistoriaClinicaResponse>(`/historia-clinica/${id}`)).data,
  obtenerHistoriaClinica: async (id: number) => (await http.get<HistoriaClinicaResponse>(`/historia-clinica/${id}`)).data,
  create: async (payload: HistoriaClinicaRequest) => (await http.post<HistoriaClinicaResponse>('/historia-clinica', payload)).data,
  crearHistoriaClinica: async (payload: HistoriaClinicaRequest) => (await http.post<HistoriaClinicaResponse>('/historia-clinica', payload)).data,
  update: async (id: number, payload: HistoriaClinicaRequest) => (await http.put<HistoriaClinicaResponse>(`/historia-clinica/${id}`, payload)).data,
  actualizarHistoriaClinica: async (id: number, payload: HistoriaClinicaRequest) => (await http.put<HistoriaClinicaResponse>(`/historia-clinica/${id}`, payload)).data,
  byPaciente: async (pacienteId: string) => (await http.get<HistoriaClinicaResponse[]>(`/historia-clinica/paciente/${pacienteId}`)).data,
  listarHistoriasPorPaciente: async (pacienteId: string) => (await http.get<HistoriaClinicaResponse[]>(`/historia-clinica/paciente/${pacienteId}`)).data,
  byMedico: async (medicoId: string) => (await http.get<HistoriaClinicaResponse[]>(`/historia-clinica/medico/${medicoId}`)).data,
  listarHistoriasPorMedico: async (medicoId: string) => (await http.get<HistoriaClinicaResponse[]>(`/historia-clinica/medico/${medicoId}`)).data,
  byCita: async (citaId: number) => (await http.get<HistoriaClinicaResponse>(`/historia-clinica/cita/${citaId}`)).data,
  obtenerHistoriaPorCita: async (citaId: number) => (await http.get<HistoriaClinicaResponse>(`/historia-clinica/cita/${citaId}`)).data
};

export const disponibilidadMedicaApi = {
  list: async () => (await http.get<DisponibilidadMedicaResponse[]>('/disponibilidad-medica')).data,
  listarDisponibilidades: async () => (await http.get<DisponibilidadMedicaResponse[]>('/disponibilidad-medica')).data,
  get: async (id: number) => (await http.get<DisponibilidadMedicaResponse>(`/disponibilidad-medica/${id}`)).data,
  obtenerDisponibilidad: async (id: number) => (await http.get<DisponibilidadMedicaResponse>(`/disponibilidad-medica/${id}`)).data,
  create: async (payload: DisponibilidadMedicaRequest) => (await http.post<DisponibilidadMedicaResponse>('/disponibilidad-medica', payload)).data,
  crearDisponibilidad: async (payload: DisponibilidadMedicaRequest) => (await http.post<DisponibilidadMedicaResponse>('/disponibilidad-medica', payload)).data,
  update: async (id: number, payload: DisponibilidadMedicaRequest) => (await http.put<DisponibilidadMedicaResponse>(`/disponibilidad-medica/${id}`, payload)).data,
  actualizarDisponibilidad: async (id: number, payload: DisponibilidadMedicaRequest) => (await http.put<DisponibilidadMedicaResponse>(`/disponibilidad-medica/${id}`, payload)).data,
  desactivar: async (id: number) => (await http.patch<MessageResponse>(`/disponibilidad-medica/${id}/desactivar`)).data,
  desactivarDisponibilidad: async (id: number) => (await http.patch<MessageResponse>(`/disponibilidad-medica/${id}/desactivar`)).data,
  byMedico: async (medicoId: string) => (await http.get<DisponibilidadMedicaResponse[]>(`/disponibilidad-medica/medico/${medicoId}`)).data,
  listarDisponibilidadesPorMedico: async (medicoId: string) => (await http.get<DisponibilidadMedicaResponse[]>(`/disponibilidad-medica/medico/${medicoId}`)).data
};

export const bloqueoAgendaApi = {
  list: async () => (await http.get<BloqueoAgendaResponse[]>('/bloqueo-agenda')).data,
  listarBloqueos: async () => (await http.get<BloqueoAgendaResponse[]>('/bloqueo-agenda')).data,
  get: async (id: number) => (await http.get<BloqueoAgendaResponse>(`/bloqueo-agenda/${id}`)).data,
  obtenerBloqueo: async (id: number) => (await http.get<BloqueoAgendaResponse>(`/bloqueo-agenda/${id}`)).data,
  create: async (payload: BloqueoAgendaRequest) => (await http.post<BloqueoAgendaResponse>('/bloqueo-agenda', payload)).data,
  crearBloqueo: async (payload: BloqueoAgendaRequest) => (await http.post<BloqueoAgendaResponse>('/bloqueo-agenda', payload)).data,
  update: async (id: number, payload: BloqueoAgendaRequest) => (await http.put<BloqueoAgendaResponse>(`/bloqueo-agenda/${id}`, payload)).data,
  actualizarBloqueo: async (id: number, payload: BloqueoAgendaRequest) => (await http.put<BloqueoAgendaResponse>(`/bloqueo-agenda/${id}`, payload)).data,
  desactivar: async (id: number) => (await http.patch<MessageResponse>(`/bloqueo-agenda/${id}/desactivar`)).data,
  desactivarBloqueo: async (id: number) => (await http.patch<MessageResponse>(`/bloqueo-agenda/${id}/desactivar`)).data,
  byMedico: async (medicoId: string) => (await http.get<BloqueoAgendaResponse[]>(`/bloqueo-agenda/medico/${medicoId}`)).data,
  listarBloqueosPorMedico: async (medicoId: string) => (await http.get<BloqueoAgendaResponse[]>(`/bloqueo-agenda/medico/${medicoId}`)).data,
  byRango: async (inicio: string, fin: string) => (await http.get<BloqueoAgendaResponse[]>('/bloqueo-agenda/rango', { params: { inicio, fin } })).data,
  listarBloqueosPorRango: async (inicio: string, fin: string) => (await http.get<BloqueoAgendaResponse[]>('/bloqueo-agenda/rango', { params: { inicio, fin } })).data,
  activosByRango: async (inicio: string, fin: string) => (await http.get<BloqueoAgendaResponse[]>('/bloqueo-agenda/rango/activos', { params: { inicio, fin } })).data,
  listarBloqueosActivosPorRango: async (inicio: string, fin: string) => (await http.get<BloqueoAgendaResponse[]>('/bloqueo-agenda/rango/activos', { params: { inicio, fin } })).data
};
