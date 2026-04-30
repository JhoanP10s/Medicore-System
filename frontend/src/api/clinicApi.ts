import { http } from './http';
import type {
  Cita,
  CitaRequest,
  Especialidad,
  EspecialidadRequest,
  Medico,
  MedicoRequest,
  MessageResponse,
  Paciente,
  PacienteRequest
} from '../types/domain';

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
  remove: async (id: number) => (await http.delete<MessageResponse>(`/cita/${id}`)).data
};
