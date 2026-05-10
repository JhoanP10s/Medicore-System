# Medicore System

Medicore System es un sistema web para gestion clinica orientado a consultorios y clinicas pequenas. Centraliza pacientes, medicos, especialidades, agenda, citas, disponibilidad, bloqueos, historias clinicas y un dashboard operativo por rol.

## Problema Que Resuelve

Muchas clinicas pequenas gestionan pacientes, horarios, citas e historias clinicas con hojas de calculo, mensajes o agendas manuales. Esto dificulta evitar solapamientos, controlar disponibilidad medica, consultar informacion clinica y tener visibilidad de la operacion diaria.

## Objetivo

Centralizar la operacion clinica basica en una aplicacion web con autenticacion, roles, agenda medica, reglas de negocio, historia clinica y dashboard, manteniendo una arquitectura preparada para crecer.

## Tecnologias

Backend:
- Java 17
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA / Hibernate
- MySQL
- Swagger / OpenAPI
- JUnit 5 y Mockito

Frontend:
- React
- Vite
- TypeScript
- Axios
- React Router
- Lucide React

Infraestructura:
- Docker
- Docker Compose
- Nginx
- MySQL 8

## Arquitectura General

El backend usa arquitectura por capas:
- `controller`: expone endpoints REST.
- `service` y `service.impl`: reglas de negocio.
- `repository`: acceso a datos con Spring Data JPA.
- `model.entity`: entidades JPA.
- `dto.request` y `dto.response`: contratos de entrada/salida.
- `mapper`: conversion entre entidades y DTOs.
- `exception`: excepciones de dominio.
- `config`: seguridad, Swagger, seed demo y manejo global de errores.

El frontend esta organizado en:
- `api`: cliente HTTP y servicios REST.
- `components`: piezas reutilizables.
- `pages`: pantallas por modulo.
- `layouts`: estructura visual principal.
- `routes`: rutas y guards.
- `hooks`: estado compartido y utilidades React.
- `types`: contratos TypeScript.
- `utils`: helpers de formato y sesion.

La comunicacion entre frontend y backend se realiza por API REST usando JWT en el header `Authorization`.

## Funcionalidades Principales

- Autenticacion con JWT.
- Roles `ADMIN`, `DOCTOR` y `USER`.
- Ownership para usuarios `DOCTOR`.
- Gestion de pacientes.
- Gestion de medicos.
- Gestion de especialidades.
- Citas clinicas con estado y duracion.
- Estados de cita: `PROGRAMADA`, `CONFIRMADA`, `CANCELADA`, `COMPLETADA`.
- Agenda medica con horarios disponibles.
- Disponibilidad medica semanal.
- Bloqueos de agenda.
- Historia clinica asociada a citas.
- Dashboard clinico por rol.
- Manejo global de errores.
- Documentacion Swagger/OpenAPI.

## Roles Y Permisos

ADMIN:
- Gestion global del sistema.
- Puede administrar medicos, especialidades, pacientes, citas, disponibilidad, bloqueos e historias clinicas.
- Puede consultar dashboard global.

DOCTOR:
- Puede consultar y gestionar recursos asociados a su propio medico.
- Puede ver sus citas, agenda, disponibilidad, bloqueos propios e historias clinicas propias.
- No puede ver recursos de otros medicos.

USER:
- Acceso limitado por ahora.
- No cuenta aun con portal de paciente completo.

## Flujo Clinico Principal

1. Crear una especialidad.
2. Crear un medico.
3. Crear un paciente.
4. Configurar disponibilidad medica.
5. Crear bloqueos de agenda.
6. Consultar horarios disponibles.
7. Crear una cita.
8. Confirmar la cita.
9. Crear historia clinica desde la cita.
10. Consultar dashboard actualizado.

## Ejecucion Con Docker

Crear archivo `.env` desde el ejemplo:

```bash
cp .env.example .env
```

Para una demo con datos iniciales, confirmar en `.env`:

```env
SPRING_PROFILES_ACTIVE=prod,demo
```

Resetear base de datos y levantar todo:

```bash
docker compose down -v
docker compose up --build
```

Si no desea cargar datos demo, use:

```env
SPRING_PROFILES_ACTIVE=prod
```

## URLs

- Frontend: http://localhost:5173
- Backend: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui/index.html

## Credenciales Demo

Estas credenciales son solo para demo/desarrollo:

| Rol | Email | Password |
| --- | --- | --- |
| ADMIN | `admin@medicore.com` | `Admin12345` |
| DOCTOR | `laura.doctor@medicore.com` | `Doctor12345` |
| USER | `user@medicore.com` | `User12345` |

## Datos Demo

El perfil `demo` crea datos iniciales idempotentes:

Especialidades:
- Medicina General
- Cardiologia
- Pediatria

Medicos:
- Dra. Laura Rojas, Medicina General, activa y asociada al usuario DOCTOR.
- Dr. Andres Perez, Cardiologia, activo.
- Dra. Camila Torres, Pediatria, activa.

Pacientes:
- Cinco pacientes activos.
- Un paciente inactivo para demostrar reglas de negocio.

Agenda:
- Disponibilidad semanal para cada medico.
- Bloqueos activos e inactivos.
- Citas en diferentes estados.
- Historias clinicas de ejemplo.

## Variables De Entorno

| Variable | Descripcion |
| --- | --- |
| `MYSQL_DATABASE` | Nombre de la base de datos MySQL. |
| `MYSQL_ROOT_PASSWORD` | Password del usuario root de MySQL. |
| `MYSQL_USER` | Usuario de aplicacion para MySQL. |
| `MYSQL_PASSWORD` | Password del usuario de aplicacion. |
| `SPRING_PROFILES_ACTIVE` | Perfiles activos. Para demo use `prod,demo`. |
| `SERVER_PORT` | Puerto del backend. |
| `JWT_SECRET` | Secreto para firmar JWT. No usar el ejemplo en produccion. |
| `JWT_EXPIRATION_MS` | Duracion del token JWT en milisegundos. |
| `DDL_AUTO` | Estrategia Hibernate. Demo usa `update`. Produccion debe usar migraciones. |
| `VITE_API_URL` | URL base del backend para el frontend. Vacio usa proxy/Nginx local. |

## Pruebas Y Build

Backend:

```bash
./mvnw clean test
./mvnw clean -DskipTests package
```

Frontend local:

```bash
cd frontend
npm install
npm run build
```

Frontend con Docker:

```bash
cd frontend
docker build --no-cache -t medicore-frontend-demo .
```

Validar Compose:

```bash
docker compose config
docker compose up --build
```

## Capturas

Pendiente agregar capturas reales:

- Login
- Dashboard
- Agenda
- Citas
- Historia clinica
- Disponibilidad medica
- Bloqueos de agenda

## Estado Del Proyecto

Estado actual: demo funcional para presentacion tecnica y comercial inicial.

No esta listo para produccion medica real sin hardening adicional de seguridad, auditoria, migraciones y cumplimiento normativo.

## Pendientes De Produccion

- Migraciones con Flyway o Liquibase.
- Auditoria clinica de accesos y cambios.
- Hardening de seguridad y gestion de secretos.
- Evitar doble reserva simultanea con control transaccional/concurrencia.
- Timezone configurable.
- Tests frontend.
- Lint frontend.
- Portal paciente.
- Prescripciones.
- Adjuntos clinicos.
- Multi-tenant.
- Facturacion.

## Roadmap

Corto plazo:
- Pulir demo visual.
- Agregar capturas al README.
- Mejorar documentacion Swagger por modulo.
- Agregar pruebas frontend basicas.

Mediano plazo:
- Migraciones formales.
- Auditoria clinica.
- Calendario visual avanzado.
- Portal paciente.

Futuro:
- Multi-tenant.
- Facturacion.
- Prescripciones.
- Adjuntos clinicos.
- Integraciones externas.

## Autor

Proyecto desarrollado como sistema clinico fullstack para demostracion academica/profesional.
